package xuandong.ecommerce_ver_2.service.email;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import xuandong.ecommerce_ver_2.dto.response.OrderDetailResponse;
import xuandong.ecommerce_ver_2.entity.Order;
import xuandong.ecommerce_ver_2.entity.OrderDetail;
import xuandong.ecommerce_ver_2.repository.OrderDetailRepository;

@Service
public class EmailService {
	private JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	private OrderDetailRepository orderDetailRepository;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine ,OrderDetailRepository orderDetailRepository) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
		this.orderDetailRepository = orderDetailRepository;
	}

	
	public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    public void sendEmailFromTemplateSync( Order order, String to, String subject, String templateName) {
        Context context = new Context();
        
        // set  tên khách hàng
        context.setVariable("customerName", order.getCustomerName());
        // set mã đơn hàng
        context.setVariable("orderId", order.getId());
        //set thời gian đặt hàng
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(order.getCreateAt());
        context.setVariable("orderDate", formattedDate);
        // set tổng tiền đơn hàng
        context.setVariable("totalAmount", order.getTotalPrice());
        
        List<OrderDetail> details = orderDetailRepository.findByOrder(order);
        List<OrderDetailResponse> responses = new ArrayList<>();
        
        details.forEach(detail -> {
        	OrderDetailResponse detailResponse = new OrderDetailResponse();
        			detailResponse.setNameProduct(detail.getProductSku().getProductColorImg().getProduct().getName());
        			detailResponse.setPathImage(detail.getProductSku().getProductColorImg().getImage());
        			detailResponse.setSize(detail.getProductSku().getSize().getName());
        			detailResponse.setPrice(detail.getProductSku().getPrice());
        			detailResponse.setQuantity(detail.getQuantity());
        			detailResponse.setTotalPrice(detail.getQuantity() *  detail.getProductSku().getPrice());
        			responses.add(detailResponse);
        });
        
        context.setVariable("orderItems", responses);
        

        String content = templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }


}
