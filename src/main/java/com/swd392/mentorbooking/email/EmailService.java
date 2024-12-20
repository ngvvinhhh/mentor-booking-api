package com.swd392.mentorbooking.email;

import com.swd392.mentorbooking.entity.Otp;
import com.swd392.mentorbooking.jwt.JWTService;
import com.swd392.mentorbooking.repository.OTPRepository;
import com.swd392.mentorbooking.service.OTPService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private OTPRepository otpRepository;

    @Value("${URL}")
    private String url;

    @Async
    public void sendVerifyEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();

            context.setVariable("name", emailDetail.getName());
            String token = jwtService.generateToken(emailDetail.getRecipient());

            String link = url + "/auth/verify/" + token;
            context.setVariable("link", link);

            context.setVariable("button", "Verify");

            proceedToSendMail(emailDetail, context, "VerifyAccount");
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
        }
    }

    public void sendOTPForgotPasswordEmail(EmailDetail emailDetail) {
        try {
            // Kiểm tra xem đã có OTP nào tồn tại cho email này chưa
            Optional<Otp> existingOtp = otpRepository.findByEmail(emailDetail.getRecipient());
            if (existingOtp.isPresent()) {
                // Nếu có, có thể xóa hoặc cập nhật
                otpService.deleteOtp(existingOtp.get()); // hoặc otpService.updateOtp(existingOtp.get(), newOtp);
            }

            // Tạo OTP mới
            String otp = otpService.generateOTP();

            // Lưu OTP vào cơ sở dữ liệu
            otpService.saveOTPForUser(emailDetail.getRecipient(), otp);

            // Tạo nội dung email
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());
            context.setVariable("otp", otp); // Đặt OTP vào nội dung email

            emailDetail.setSubject("Password Reset Request");
            emailDetail.setMsgBody("Hi " + emailDetail.getName() + ",\n\n" +
                    "We received a request to reset your password. Your OTP code is: " + otp + "\n\n" +
                    "Enter this code on the password reset page to proceed.");

            // Gửi email với OTP
            proceedToSendMail(emailDetail, context, "forgot-password-otp");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void sendForgotPasswordEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());

            // Tạo token để người dùng có thể reset mật khẩu
            String token = jwtService.generateToken(emailDetail.getRecipient());
            String resetLink = "https://circuit-project.vercel.app/forgotPassword?" + token;

            context.setVariable("link", resetLink);
            context.setVariable("button", "Reset Password");

            emailDetail.setSubject("Password Reset Request");
            emailDetail.setMsgBody("Hi " + emailDetail.getName() + ",\n\n" +
                    "We received a request to reset your password. Click the button below to reset it:\n\n" +
                    "<a href=\"" + resetLink + "\">Reset Password</a>");

            proceedToSendMail(emailDetail, context, "forgot-password");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
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
        mimeMessageHelper.setFrom("dungnlmse170490@fpt.edu.vn");
        mimeMessageHelper.setTo(emailDetail.getRecipient());
        mimeMessageHelper.setText(text, true);
        mimeMessageHelper.setSubject(emailDetail.getSubject());
        javaMailSender.send(mimeMessage);
    }
}

