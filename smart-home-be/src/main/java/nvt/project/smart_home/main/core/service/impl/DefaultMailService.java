package nvt.project.smart_home.main.core.service.impl;

import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.service.interf.IMailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Service
public class DefaultMailService implements IMailService {

    private final JavaMailSender mailSender;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Configuration freemarkerConfiguration;
    private final TemplateEngine templateEngine;

    private final String SENDER = "ubert472@gmail.com";

    @Override
    public void sendTextEmail(String subject, String receiver, String emailContent) {
        executorService.execute(() -> sendRegularMail(subject, receiver, emailContent));
    }

    @Override
    public void sendConfirmationEmail(String subject, String receiver, Map<String, Object> context) {
        executorService.execute(() -> sendConfirmationMail(subject, receiver, context));
    }

    @Override
    public void sendApproveOrDenyPropertyEmail(String subject, String receiver, Map<String, Object> context, String template) {
        executorService.execute(() -> sendApproveOrDenyPropertyMail(subject, receiver, context, template));
    }

    private void sendRegularMail(String subject, String receiver, String emailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER);
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(emailContent);
        mailSender.send(message);
    }
    public void sendConfirmationMail(String subject, String receiver, Map<String, Object> context) {
        // Prepare the Thymeleaf context
        Context thymeleafContext = new Context();
        for (String key : context.keySet()) {
            thymeleafContext.setVariable(key, context.get(key));
        }

        // Process the Thymeleaf template
        String emailContent = templateEngine.process("email-activation", thymeleafContext);

        // Send the email
        sendMail(subject, receiver, emailContent);
    }

    private void sendApproveOrDenyPropertyMail(String subject, String receiver, Map<String, Object> context, String template) {
        Context thymeleafContext = new Context();
        for (String key : context.keySet()) {
            thymeleafContext.setVariable(key, context.get(key));
        }
        String emailContent = templateEngine.process(template, thymeleafContext);
        sendMail(subject, receiver, emailContent);
    }

    @SneakyThrows
    private void sendMail(String subject, String recipient, String emailContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(SENDER);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }
}

