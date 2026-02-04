package com.gm.hrms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

    @GetMapping("/test")
    public String testApi() {
        return "HRMS Backend is Working 🚀";
    }

    @GetMapping("/yashraj")
    public String yashrajApi() {
        return "Yashraj Backend is Pushed 🥳";
    }

}
