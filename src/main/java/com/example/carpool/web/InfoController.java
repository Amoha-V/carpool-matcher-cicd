package com.example.carpool.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;

@RestController
public class InfoController {

    @Value("${app.version}")
    private String version;

    @Value("${app.message}")
    private String message;

    @Value("${app.build}")
    private String build;

    @Value("${app.environment}")
    private String environment;

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of(
                "message", message,
                "version", version,
                "build", build.length() > 7 ? build.substring(0, 7) : build,
                "environment", environment,
                "pod", hostname());
    }

    private String hostname() {
        String env = System.getenv("HOSTNAME");
        if (env != null && !env.isBlank()) {
            return env;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
