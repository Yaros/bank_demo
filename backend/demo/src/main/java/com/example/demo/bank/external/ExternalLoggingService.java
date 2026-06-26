package com.example.demo.bank.external;

import org.springframework.stereotype.Service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalLoggingService {

    private final ExternalLoggingClient client;

    @Retry(name = "externalLogging")
    public void logDebit() {
        client.logDebit();
    }
}
