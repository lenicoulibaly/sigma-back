package lenicorp.admin.notification.controller.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.Duration;
import java.util.Properties;

/**
 * Mail configuration for Spring Boot.
 * 
 * Note: This class has been adapted from Quarkus to Spring Boot.
 * The MailService class will need to be updated to use Spring Boot's JavaMailSender
 * instead of Quarkus Mailer for sending emails.
 */

@Configuration
@Getter
public class MailConfig
{
    @Value("${spring.mail.host}")
    String smtpHost;

    @Value("${spring.mail.port}")
    Integer smtpPort;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.properties.mail.smtp.from:}")
    String defaultFrom;

    @Value("${spring.mail.properties.mail.smtp.timeout:10000}")
    Duration timeout;

    @Value("${spring.mail.properties.mail.smtp.connection-timeout:10000}")
    Duration connectionTimeout;

    @Value("${spring.mail.properties.mail.smtp.writetimeout:10000}")
    Duration writeTimeout;

    @Value("${spring.mail.test-connection:false}")
    Boolean mockMode;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}")
    Boolean tlsEnabled;

    @Value("${spring.mail.properties.mail.smtp.starttls.required:false}")
    Boolean startTls;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust:}")
    String trustAll;

    @Value("${spring.mail.properties.mail.smtp.from:}")
    String bounceAddress;

    @Value("${app.mail.template.from:}")
    String templateFrom;

    @Value("${mail.from.name:Application E-Courrier}")
    String fromName;

    @Value("${spring.mail.password}")
    String password;

    /**
     * Creates and configures a JavaMailSender bean for sending emails.
     * 
     * @return a configured JavaMailSender
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", tlsEnabled.toString());
        props.put("mail.smtp.starttls.required", startTls.toString());
        props.put("mail.smtp.ssl.trust", trustAll.toString());
        props.put("mail.smtp.timeout", timeout.toMillis());
        props.put("mail.smtp.connectiontimeout", connectionTimeout.toMillis());
        props.put("mail.smtp.writetimeout", writeTimeout.toMillis());
        props.put("mail.debug", "false");

        return mailSender;
    }


}
