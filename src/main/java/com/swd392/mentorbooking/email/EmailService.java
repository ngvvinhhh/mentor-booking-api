package com.swd392.mentorbooking.email;

import com.swd392.mentorbooking.jwt.JWTService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JWTService jwtService;

    public void sendVerifyEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();

            context.setVariable("name", emailDetail.getName());
            String token = jwtService.generateToken(emailDetail.getRecipient());

            String link = "http://localhost:8080/auth/verify/" + token;
            context.setVariable("link", link);

            context.setVariable("button", "Verify");

            proceedToSendMail(emailDetail, context, "VerifyAccount");
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
        }
    }

    public void sendEmailJoinGroup(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());
            context.setVariable("button", "Go to page");
            context.setVariable("link", emailDetail.getAttachment());

            emailDetail.setSubject("You've been invited to join a group!");
            emailDetail.setMsgBody("Hi " + emailDetail.getName() + ",\n\n" +
                    "You've been invited to join a group. Click the button below to join:\n\n" +
                    "<a href=\"" + emailDetail.getAttachment() + "\">" + context.getVariable("button") + "</a>");

            proceedToSendMail(emailDetail, context, "invite-template");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private void proceedToSendMail(EmailDetail emailDetail, Context context, String template) throws MessagingException {
        String text = templateEngine.process(template, context);

        // Creating a simple mail message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        // Setting up necessary details
        mimeMessageHelper.setFrom("admin@gmail.com");
        mimeMessageHelper.setTo(emailDetail.getRecipient());
        mimeMessageHelper.setText(text, true);
        mimeMessageHelper.setSubject(emailDetail.getSubject());
        javaMailSender.send(mimeMessage);
    }
}

