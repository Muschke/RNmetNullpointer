package be.hi10.realnutrition.services.custom;

import be.hi10.realnutrition.pojos.woo.WooWebhookOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendOrder(List<WooWebhookOrder> orderList) {

        MimeMessage mime = this.emailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mime, true);
            message.setTo("stijnn.buys@gmail.com");
            message.setSubject("Nieuw order LV8Dnutrition");
            message.setText(orderListToString(orderList),true);
            this.emailSender.send(mime);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private String orderListToString(List<WooWebhookOrder> orderList)
    {
        StringBuilder text = new StringBuilder("");

        text.append("<html style='background-color:white;'><body style='margin: 0;'><div style='background-color:black;'>" +
                "<center><img src='https://modeatelier.be/wp-content/uploads/2020/06/logozwart.png' height=100/>" +
                "</center></div><center><h3><b>Nieuw order van LV8Dnutrition</b></h3><div style='width: 400'>");

        for (WooWebhookOrder order : orderList ) {
            text.append("<div style='background: #cfcfcf'><br>");
            text.append("<div><b>" + order.getName() + "</span><span> x "+ order.getQuantity() + "</b></div>");
            text.append("<div>ean </span><span>"+ order.getEan() + "</div><br></div><br>");
        }

        text.append("</div></center></body></html>");
        return text.toString();
    }

}
