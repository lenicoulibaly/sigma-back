package lenicorp.admin.workflowengine.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.workflowengine.outbox.model.entities.OutboxEvent;
import lenicorp.admin.workflowengine.outbox.model.enums.OutboxStatus;
import lenicorp.admin.workflowengine.outbox.model.payload.TransitionAppliedPayload;
import lenicorp.admin.workflowengine.outbox.repo.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID enqueueTransitionApplied(TransitionAppliedPayload payload) {
        if (payload.getOccurredAt() == null) payload.setOccurredAt(LocalDateTime.now());
        if (payload.getCorrelationId() == null) payload.setCorrelationId(UUID.randomUUID().toString());
        String json = toJson(payload);
        OutboxEvent evt = new OutboxEvent();
        evt.setEventType("TransitionApplied");
        evt.setPayload(json);
        evt.setHeaders(toJson(Map.of("workflowCode", payload.getWorkflowCode(),
                "transitionCode", payload.getTransitionCode())));
        evt.setStatus(OutboxStatus.NEW);
        outboxRepo.save(evt);
        return evt.getId();
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}
