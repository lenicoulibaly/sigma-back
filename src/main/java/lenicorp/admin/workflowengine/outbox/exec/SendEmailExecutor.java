package lenicorp.admin.workflowengine.outbox.exec;

import lenicorp.admin.notification.controller.services.MailService;
import lenicorp.admin.notification.controller.services.MailServiceInterface;
import lenicorp.admin.notification.model.dto.MailRequest;
import lenicorp.admin.workflowengine.outbox.service.DedupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
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
        dedupService.runOnce(ctx.dedupKey(), ctx.name(), () -> {
            Map<String, Object> cfg = ctx.config();
            Map<String, Object> facts = ctx.facts() != null ? ctx.facts() : Map.of();
            log.debug("[DEBUG_LOG] Facts: {}", facts);
            Map<String, Object> fullContext = new HashMap<>();
            fullContext.putAll(facts); // Put facts at root for direct access like ${nomDestinataire}
            fullContext.put("event", ctx.event());
            fullContext.put("facts", facts);
            fullContext.put("config", cfg);
            
            // Résoudre tous les paramètres de config et les ajouter au contexte racine pour le template
            Map<String, Object> resolvedConfig = new HashMap<>();
            cfg.forEach((k, v) -> resolvedConfig.put(k, ctx.resolver().eval(v, fullContext)));
            fullContext.putAll(resolvedConfig);
            
            log.debug("[DEBUG_LOG] Full context keys after enrichment: {}", fullContext.keySet());

            String content;
            if (cfg.containsKey("templatePath")) {
                Object rawTemplatePath = cfg.get("templatePath");
                Object resolvedPath = fullContext.get("templatePath");
                String templatePath = resolvedPath != null ? resolvedPath.toString() : "";
                String rawTemplate = MailService.chargerTemplate(templatePath);
                log.debug("[DEBUG_LOG] Raw template length: {}", rawTemplate.length());
                content = ctx.resolver().eval(rawTemplate, fullContext).toString();
                log.debug("[DEBUG_LOG] Resolved content preview: {}", content.substring(0, Math.min(content.length(), 100)));
            } else {
                Object resolvedContent = fullContext.get("content");
                content = resolvedContent != null ? resolvedContent.toString() : "";
            }

            Object to = fullContext.get("to");
            Object subject = fullContext.get("subject");

            MailRequest.MailRequestBuilder builder = MailRequest.builder()
                    .to(to != null ? to.toString() : null)
                    .subject(subject != null ? subject.toString() : "")
                    .content(content)
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
