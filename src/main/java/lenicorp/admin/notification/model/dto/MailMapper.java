package lenicorp.admin.notification.model.dto;

import lenicorp.admin.notification.controller.services.MailConfig;
import lenicorp.admin.notification.model.entities.EmailNotification;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class MailMapper {

    @Autowired
    protected MailConfig mailCfg;

    @Mapping(target = "mailId", ignore = true)
    @Mapping(target = "email", source = "to")
    @Mapping(target = "recipientName", source = "recipientName")
    @Mapping(target = "seen", constant = "false")
    @Mapping(target = "sent", constant = "false")
    @Mapping(target = "mailObject", source = "subject")
    @Mapping(target = "mailMessage", source = "content")
    @Mapping(target = "senderUsername", constant = "system")
    @Mapping(target = "systemMailSender", expression = "java(mailCfg.getUsername())")
    @Mapping(target = "token", ignore = true)
    public abstract EmailNotification mapToToEmailNotification(MailRequest request);
}
