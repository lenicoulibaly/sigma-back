package lenicorp.admin.workflowengine.outbox.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.workflowengine.outbox.exec.*;
import lenicorp.admin.workflowengine.outbox.model.entities.OutboxEvent;
import lenicorp.admin.workflowengine.outbox.model.enums.OutboxStatus;
import lenicorp.admin.workflowengine.outbox.model.payload.OutboxAction;
import lenicorp.admin.workflowengine.outbox.model.payload.TransitionAppliedPayload;
import lenicorp.admin.workflowengine.outbox.repo.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class OutboxDispatcher {
    private final OutboxEventRepository outboxRepo;
    private final ActionExecutorRegistry registry;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;
    private final ExpressionResolver resolver = new ExpressionResolver();

    @Value("${outbox.dispatch.fixed-delay:1000}")
    private long fixedDelayMs;

    @Value("${outbox.dispatch.batch-size:100}")
    private int batchSize;

    @Value("${outbox.dispatch.max-attempts:5}")
    private int maxAttempts;

    @Scheduled(fixedDelayString = "${outbox.dispatch.fixed-delay:1000}")
    public void dispatch() {
        Instant now = Instant.now();
        List<OutboxEvent> batch = outboxRepo.findReadyBatch(OutboxStatus.NEW, OutboxStatus.RETRY, now, PageRequest.of(0, batchSize));
        if (batch.isEmpty()) return;

        for (OutboxEvent evt : batch) {
            try {
                applicationContext.getBean(OutboxDispatcher.class).processEvent(evt, now);
            } catch (Exception e) {
                log.error("Failed to process outbox event {}: {}", evt.getId(), e.getMessage());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(OutboxEvent evt, Instant now) {
        try {
            evt.markProcessing();
            outboxRepo.save(evt);

            handleEvent(evt);

            evt.markSent();
            outboxRepo.save(evt);
        } catch (Exception ex) {
            int attempts = evt.getAttempts() + 1;
            if (attempts >= maxAttempts || isNonRetryable(ex)) {
                evt.markDead(shortMsg(ex));
            } else {
                Duration backoff = backoff(attempts);
                evt.scheduleRetry(now.plus(backoff), shortMsg(ex), attempts);
            }
            outboxRepo.save(evt);
        }
    }

    private void handleEvent(OutboxEvent evt) throws Exception {
        if (!"TransitionApplied".equals(evt.getEventType())) {
            // unknown event type: consider sent (or throw to DLQ). We'll ignore silently here.
            return;
        }
        TransitionAppliedPayload payload = objectMapper.readValue(evt.getPayload(), TransitionAppliedPayload.class);
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("workflowCode", payload.getWorkflowCode());
        eventMap.put("transitionCode", payload.getTransitionCode()); eventMap.put("transitionId", payload.getTransitionId());
        eventMap.put("objectType", payload.getObjectType());
        eventMap.put("objectId", payload.getObjectId());
        eventMap.put("fromStatus", payload.getFromStatus());
        eventMap.put("toStatus", payload.getToStatus());
        eventMap.put("correlationId", payload.getCorrelationId());
        if (payload.getOccurredAt() != null) eventMap.put("occurredAt", payload.getOccurredAt().toString());

        Map<String, Object> facts = payload.getFacts() != null ? payload.getFacts() : Map.of();
        List<OutboxAction> actions = payload.getActions();
        if (actions == null || actions.isEmpty()) return; // nothing to do

        for (OutboxAction action : actions) {
            OutboxActionExecutor exec = registry.get(action.getActionType());
            if (exec == null) continue; // unknown action type → skip
            String dedupKey = action.getDedupKey();
            String name = action.getName();
            Map<String, Object> cfg = action.getConfig() != null ? action.getConfig() : Map.of();
            ActionContext ctx = new ActionContext(name, dedupKey, eventMap, facts, cfg, resolver, applicationContext);
            exec.execute(ctx);
        }
    }

    private Duration backoff(int attempts) {
        long seconds = Math.min(60, (long) Math.pow(2, attempts));
        long jitterMs = ThreadLocalRandom.current().nextLong(0, 1000);
        return Duration.ofSeconds(seconds).plusMillis(jitterMs);
    }

    private boolean isNonRetryable(Exception ex) {
        // Simplified classification; callers can refine by throwing specific exceptions
        String msg = shortMsg(ex).toLowerCase();
        return msg.contains("invalid address") || msg.contains("template not found");
    }

    private String shortMsg(Exception ex) {
        String m = ex.getMessage();
        if (m == null) m = ex.getClass().getSimpleName();
        return m.length() > 1000 ? m.substring(0, 1000) : m;
    }
}
