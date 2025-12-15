package com.uade.back.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for health checks.
 */
@RestController
public class HealthController {
  /**
   * Performs a health check.
   *
   * @return A map with the status.
   */
  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }
}
