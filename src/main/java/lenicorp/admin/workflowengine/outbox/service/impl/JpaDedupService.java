package lenicorp.admin.workflowengine.outbox.service.impl;

import jakarta.transaction.Transactional;
import lenicorp.admin.workflowengine.outbox.model.entities.OutboxActionLog;
import lenicorp.admin.workflowengine.outbox.repo.OutboxActionLogRepository;
import lenicorp.admin.workflowengine.outbox.service.DedupService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaDedupService implements DedupService {
    private final OutboxActionLogRepository repo;

    @Override
    @Transactional
    public boolean runOnce(String dedupKey, String name, Runnable runnable) {
        try {
            // Try to reserve the key
            OutboxActionLog log = new OutboxActionLog();
            log.setDedupKey(dedupKey);
            log.setName(name);
            log.setStatus("RESERVED");
            repo.save(log);
        } catch (DataIntegrityViolationException e) {
            // Already exists => consider action already executed (or in progress)
            return false;
        }
        try {
            runnable.run();
            repo.findByDedupKey(dedupKey).ifPresent(l -> { l.setStatus("SUCCESS"); l.setLastError(null); repo.save(l);});
            return true;
        } catch (RuntimeException re) {
            repo.findByDedupKey(dedupKey).ifPresent(l -> { l.setStatus("FAILED"); l.setLastError(shortMsg(re)); repo.save(l);});
            throw re;
        }
    }

    private String shortMsg(Throwable t) {
        String m = t.getMessage();
        if (m == null) return t.getClass().getSimpleName();
        return m.length() > 1000 ? m.substring(0, 1000) : m;
    }
}
