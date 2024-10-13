package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.payment.PaymentRequest;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.PaymentStatusEnum;
import com.swd392.mentorbooking.entity.Enum.WalletLogType;
import com.swd392.mentorbooking.entity.Payment;
import com.swd392.mentorbooking.entity.Wallet;
import com.swd392.mentorbooking.entity.WalletLog;
import com.swd392.mentorbooking.exception.auth.InvalidAccountException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.PaymentRepository;
import com.swd392.mentorbooking.repository.WalletLogRepository;
import com.swd392.mentorbooking.repository.WalletRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PaymentService {

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletLogRepository walletLogRepository;

    public Payment create(PaymentRequest paymentRequest) {
        Account customer = accountRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new InvalidAccountException("Account not found."));
        Payment payment = new Payment();

        payment.setAccount(customer);

        payment.setCreatedAt(LocalDateTime.now());
        payment.setTotal(paymentRequest.getAmount());
        payment.setDescription(paymentRequest.getDescription());

        // Save the payment to the database
        Payment savedPayment = paymentRepository.save(payment);

        // Add money to wallet
        addToWallet(customer.getWallet(), savedPayment.getTotal());

        return savedPayment;
    }

    public String createUrl(PaymentRequest paymentRequest) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        // Create payment
        Payment payment = create(paymentRequest);

        // Save the transaction status (money not added to wallet yet)
        payment.setStatus(PaymentStatusEnum.PENDING);
        paymentRepository.save(payment); // Save DB

        double money = payment.getTotal() * 100;
        String amount = String.valueOf((int) money);

        String tmnCode = "C2CFQOTD";
        String secretKey = "867ZAJFLY3FAR95N9MHKC4SHN7VGCZLN";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "https://circuit-project.vercel.app/payment-succeed?" + payment.getId();
        String currCode = "VND";

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", payment.getId().toString());
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + payment.getId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", amount);
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "128.199.178.23");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

        return urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private void addToWallet(Wallet wallet, double amount) {
        // Create a WalletLog entry to track the transaction
        WalletLog walletLog = new WalletLog();
        walletLog.setWallet(wallet);
        walletLog.setAmount(amount);
        walletLog.setTypeOfLog(WalletLogType.DEPOSIT);
        walletLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(walletLog);

        // Update wallet balance
        wallet.setTotal(wallet.getTotal() + amount);
        walletRepository.save(wallet);
    }
}
