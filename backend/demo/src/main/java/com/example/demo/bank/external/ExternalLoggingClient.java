package com.example.demo.bank.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "externalLoggingClient", url = "${external.logging.url}")
public interface ExternalLoggingClient {

    @GetMapping("/200")
    void logDebit();
}
