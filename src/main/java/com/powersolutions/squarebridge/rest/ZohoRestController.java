package com.powersolutions.squarebridge.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
public class ZohoRestController {

    @GetMapping("/create_payment")
    public void createPayment(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
    }

}
