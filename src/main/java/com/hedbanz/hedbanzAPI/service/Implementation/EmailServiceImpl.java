package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.model.Mail;
import com.hedbanz.hedbanzAPI.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(mail.getModel());
            String html = templateEngine.process("email-template", context);

            helper.setTo(mail.getTo());
            helper.setFrom(mail.getFrom());
            helper.setSubject(mail.getSubject());
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
