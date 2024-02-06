package nvt.project.smart_home.main.core.service.interf;

import java.util.Map;

public interface IMailService {
    void sendTextEmail(String subject, String receiver, String emailContent);
    void sendConfirmationEmail(String subject, String receiver, Map<String, Object> model);
    void sendApproveOrDenyPropertyEmail(String subject, String receiver, Map<String, Object> context, String template);
}
