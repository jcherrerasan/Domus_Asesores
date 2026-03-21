package com.crm.crm_domus.service;

import org.springframework.stereotype.Service;

@Service
public class HomeService {

    public String homeRedirect() {
        return "redirect:/swagger-ui/index.html";
    }
}
