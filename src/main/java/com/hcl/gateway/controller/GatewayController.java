package com.hcl.gateway.controller;

import com.hcl.gateway.manager.RateLimiterManager;
import com.hcl.gateway.breaker.CircuitBreaker;
import com.hcl.gateway.model.SapSalesOrder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
// ఇది నిజమైన SAP S/4HANA క్లౌడ్ యొక్క అఫీషియల్ సేల్స్ ఆర్డర్ OData API రూట్ పాత్
@RequestMapping("/sap/opu/odata/sap/API_SALES_ORDER_SRV")
@CrossOrigin(origins = "*",allowedHeaders = "*") // రియాక్ట్ పోర్ట్ కనెక్షన్ కోసం
public class GatewayController {

    private final RateLimiterManager rateLimiterManager;
    private final CircuitBreaker circuitBreaker;
    private final Random random = new Random();
    private int orderSequence = 10001; // SAP ఆర్డర్ సీక్వెన్స్ నంబర్ కౌంటర్

    public GatewayController() {
        this.rateLimiterManager = new RateLimiterManager(5, 1);
        this.circuitBreaker = new CircuitBreaker();
    }

    @GetMapping("/A_SalesOrder")
    public Map<String, Object> processSapOdataRequest(
            @RequestParam(value = "ip", defaultValue = "127.0.0.1") String clientIp,
            @RequestParam(value = "crash", defaultValue = "false") boolean simulateServerCrash) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("d_timestamp", System.currentTimeMillis());
        response.put("clientIp", clientIp);

        // 1. సర్క్యూట్ బ్రేకర్ చెక్
        if (!circuitBreaker.allowRequest()) {
            response.put("status", 503);
            response.put("message", "SAP RFC Connection Failed: Circuit is OPEN (ERP Overloaded)");
            response.put("circuitState", "OPEN");
            return response;
        }

        // 2. సర్వర్ క్రాష్ టెస్ట్
        if (simulateServerCrash) {
            circuitBreaker.recordFailure();
            response.put("status", 500);
            response.put("message", "SAP Gateway System Error: Short Dump Generated (Simulated)");
            response.put("circuitState", circuitBreaker.getCurrentState().toString());
            return response;
        }

        // 3. రేట్ లిమిటర్ బకెట్ చెక్
        if (rateLimiterManager.isAllowed(clientIp)) {
            circuitBreaker.recordSuccess();
            
            // డైనమిక్ గా ఫేక్ SAP ఆర్డర్ డేటా క్రియేట్ చేస్తున్నాం
            String vbeln = "SO" + (orderSequence++);
            String kunnr = "CUST" + (random.nextInt(9000) + 1000);
            String matnr = "MAT" + (random.nextInt(9000) + 1000);
            double netwr = Math.round((100 + (1500 * random.nextDouble())) * 100.0) / 100.0;
            
            SapSalesOrder sapOrder = new SapSalesOrder(vbeln, kunnr, matnr, netwr, "INR");

            response.put("status", 200);
            response.put("message", "Success: SAP OData Payload Registered");
            response.put("sapPayload", sapOrder); // JSON లోపలికి SAP ఆబ్జెక్ట్ ని పంపుతున్నాం
            response.put("circuitState", "CLOSED");
        } else {
            response.put("status", 429);
            response.put("message", "SAP Gateway Alert: API Throttle Triggered (Memory Lock Blocked)");
            response.put("circuitState", "CLOSED");
        }

        return response;
    }
}
