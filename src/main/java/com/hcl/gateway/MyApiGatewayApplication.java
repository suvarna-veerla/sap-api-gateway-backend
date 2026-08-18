package com.hcl.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApiGatewayApplication {
    public static void main(String[] args) {
        // ఇది మన స్ప్రింగ్ బూట్ ఇన్-బిల్ట్ టామ్‌క్యాట్ (Tomcat) సర్వర్‌ను 8080 పోర్ట్‌లో స్టార్ట్ చేస్తుంది
        SpringApplication.run(MyApiGatewayApplication.class, args);
        System.out.println("🛡️ Cyber Shield API Gateway Started on http://localhost:8080");
    }
}
