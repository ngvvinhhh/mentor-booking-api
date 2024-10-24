package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.dto.payment.PaymentRequest;
import com.swd392.mentorbooking.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("deposit")
    public ResponseEntity<String> deposit (@RequestBody PaymentRequest paymentRequest) throws Exception {
        String vnpayUrl = paymentService.createUrl(paymentRequest);
        return ResponseEntity.ok(vnpayUrl);
    }

    @GetMapping("/payment-pending")
    public String paymentSucceed(@RequestParam("id") Long paymentId, @RequestParam Map<String, String> params) {
        return paymentService.processPaymentCallback(paymentId, params);
    }


}
