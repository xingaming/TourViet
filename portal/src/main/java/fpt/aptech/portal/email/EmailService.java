package fpt.aptech.portal.email;

import fpt.aptech.portal.entities.Booking;
import fpt.aptech.portal.entities.Informationbooking;
import fpt.aptech.portal.entities.Payment;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendConfirmationEmail(String recipientEmail, String userFullName, int orderID, Payment payment, Booking booking, List<Informationbooking> informationBookings) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Confirmation of Payment - Order #" + orderID);

            Context context = new Context();
            context.setVariable("userFullName", userFullName);
            context.setVariable("orderID", orderID);
            context.setVariable("payment", payment); // Thêm payment vào context
            context.setVariable("booking", booking); // Thêm booking vào context
            context.setVariable("informationBookings", informationBookings); // Thêm danh sách informationBookings vào context
            String emailContent = templateEngine.process("admin/email/confirmation_email", context);
            //String emailContent = templateEngine.process("confirmation_email", context);

            helper.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            // Handle exceptions
        }
    }
    
    
    
    public void sendCancelBookingEmail(String recipientEmail, String userFullName, String reason, int orderID, Payment payment, Booking booking, List<Informationbooking> informationBookings) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Confirmation of Payment - Order #" + orderID);

            Context context = new Context();
            context.setVariable("userFullName", userFullName);
            context.setVariable("reason", reason);
            context.setVariable("orderID", orderID);
            context.setVariable("payment", payment); // Thêm payment vào context
            context.setVariable("booking", booking); // Thêm booking vào context
            context.setVariable("informationBookings", informationBookings); // Thêm danh sách informationBookings vào context
            String emailContent = templateEngine.process("admin/email/cancel_email", context);
            //String emailContent = templateEngine.process("confirmation_email", context);

            helper.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            // Handle exceptions
        }
    }
    
}
