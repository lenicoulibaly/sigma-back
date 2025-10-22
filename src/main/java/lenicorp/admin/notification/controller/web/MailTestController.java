package lenicorp.admin.notification.controller.web;

import lenicorp.admin.notification.controller.services.MailServiceInterface;
import lenicorp.admin.notification.model.dto.MailRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/open/mail")
public class MailTestController
{
    @Autowired
    private MailServiceInterface mailService;

    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<?>> sendMail(@Valid @RequestBody MailRequest mailRequest)
    {
        return mailService.sendMailAsync(mailRequest).thenApply(response ->
        {
            if (response.isSuccess()) return ResponseEntity.ok(response);
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
        });
    }
}
