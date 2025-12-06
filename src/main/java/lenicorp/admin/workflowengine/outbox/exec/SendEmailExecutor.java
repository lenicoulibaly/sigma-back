package lenicorp.admin.workflowengine.outbox.exec;

import lenicorp.admin.notification.controller.services.MailServiceInterface;
import lenicorp.admin.notification.model.dto.MailRequest;
import lenicorp.admin.workflowengine.outbox.service.DedupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SendEmailExecutor implements OutboxActionExecutor {
    public static final String TYPE = "SEND_EMAIL";

    private final DedupService dedupService;
    private final MailServiceInterface mailService;

    @Override
    public String actionType() {
        return TYPE;
    }

    @Override
    public void execute(ActionContext ctx) throws Exception {
        dedupService.runOnce(ctx.dedupKey(), () -> {
            Map<String, Object> cfg = ctx.config();
            Object to = ctx.resolver().eval(cfg.get("to"), Map.of(
                    "event", ctx.event(),
                    "facts", ctx.facts(),
                    "config", cfg
            ));
            Object subject = ctx.resolver().eval(cfg.get("subject"), Map.of(
                    "event", ctx.event(),
                    "facts", ctx.facts(),
                    "config", cfg
            ));
            Object content = ctx.resolver().eval(cfg.get("content"), Map.of(
                    "event", ctx.event(),
                    "facts", ctx.facts(),
                    "config", cfg
            ));

            MailRequest.MailRequestBuilder builder = MailRequest.builder()
                    .to(to != null ? to.toString() : null)
                    .subject(subject != null ? subject.toString() : "")
                    .content(content != null ? content.toString() : "")
                    .isHtml(true);

            // optional CC/BCC as list of strings
            Object cc = cfg.get("cc");
            if (cc instanceof List<?> l) builder.cc(l.stream().map(String::valueOf).toList());
            Object bcc = cfg.get("bcc");
            if (bcc instanceof List<?> l2) builder.bcc(l2.stream().map(String::valueOf).toList());

            var req = builder.build();
            try {
                mailService.sendMailAsync(req).join();
            } catch (RuntimeException ex) {
                throw ex; // let dispatcher retry
            }
        });
    }
}
