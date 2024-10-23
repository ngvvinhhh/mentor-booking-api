package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.entity.Otp;
import com.swd392.mentorbooking.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    private static final int OTP_LENGTH = 6; // Độ dài của mã OTP
    private static final int OTP_EXPIRATION_MINUTES = 1; // OTP hết hạn sau 1 phút

    public void saveOTPForUser(String email, String otp) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
        System.out.println("Saving OTP for email: " + email + ", expiration time: " + expirationTime);

        Otp otpEntity = new Otp();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setSentTime(LocalDateTime.now());
        otpEntity.setExpirationTime(expirationTime);

        otpRepository.save(otpEntity);
    }

    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Tạo số ngẫu nhiên từ 0-9
        }

        return otp.toString();
    }

    public boolean validateOTP(String email, String otp) {
        Otp storedOtp = otpRepository.findByEmail(email).orElse(null);
        return storedOtp != null && storedOtp.getOtp().equals(otp) && LocalDateTime.now().isBefore(storedOtp.getExpirationTime());
    }

    public void deleteOtp(Otp otp) {
        // Xóa OTP từ cơ sở dữ liệu
        otpRepository.delete(otp);
    }

    public Otp findByEmail(String email) {
        return otpRepository.findByEmail(email).orElse(null);
    }

    @Scheduled(fixedRate = 60000) // 60000 milliseconds
    public void deleteExpiredOtps() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(OTP_EXPIRATION_MINUTES);


        List<Otp> expiredOtps = otpRepository.findByExpirationTimeBefore(expirationTime); // Tìm các OTP đã hết hạn


        otpRepository.deleteAll(expiredOtps); // Xóa các OTP đã hết hạn

    }
}
