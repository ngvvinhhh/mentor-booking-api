package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.dto.payment.PaymentRequest;
import com.swd392.mentorbooking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@CrossOrigin("**")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("create")
    public ResponseEntity create (@RequestBody PaymentRequest paymentRequest) throws Exception {
        String  vnpayUrl = paymentService.createUrl(paymentRequest);
        return  ResponseEntity.ok(vnpayUrl);
    }
}
