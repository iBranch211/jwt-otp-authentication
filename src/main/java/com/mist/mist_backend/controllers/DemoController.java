package com.mist.mist_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    @Value("${com.mist.mist_backend.version}")
    private String version;

    @GetMapping("/ver")
    public String ver() {
        return version;
    }
}
