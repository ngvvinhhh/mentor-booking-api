package com.swd392.mentorbooking.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
@CrossOrigin("**")
@SecurityRequirement(name = "api")
@RequestMapping("/endpoint")
public class EndpointController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/count-endpoints")
    public ResponseEntity<Integer> countEndpoints() {
        int count = applicationContext.getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods().size();
        return ResponseEntity.ok(count);
    }

}
