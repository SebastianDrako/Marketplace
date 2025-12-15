package com.uade.back.controller;

import java.util.List;

import com.uade.back.dto.order.OrderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.dto.order.CreateOrderRequest;
import com.uade.back.dto.order.OrderIdRequest;
import com.uade.back.dto.order.OrderResponse;
import com.uade.back.dto.order.UpdateDeliveryStatusRequest;
import com.uade.back.dto.order.UpdatePaymentRequest;
import com.uade.back.service.order.OrderService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing orders.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService service;

  /**
   * Creates a new order.
   *
   * @param request The order creation details.
   * @return The created order.
   */
  @PostMapping
  public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
    return ResponseEntity.ok(service.create(request));
  }

  /**
   * Retrieves an order by its ID.
   *
   * @param request The order ID request.
   * @return The order details.
   */
  @PostMapping("/by-id")
  public ResponseEntity<OrderResponse> get(@RequestBody OrderIdRequest request) {
    return ResponseEntity.ok(service.getById(request));
  }

  /**
   * Retrieves the current user's orders.
   *
   * @return A list of the user's orders.
   */
  @GetMapping("/my-orders")
  public ResponseEntity<List<OrderDTO>> myOrders() {
    return ResponseEntity.ok(service.getMyOrders());
  }

  /**
   * Updates the payment status of an order.
   *
   * @param pagoId  The ID of the payment.
   * @param request The new payment status.
   * @return A response entity with no content.
   */
  @PatchMapping("/payment/{pagoId}")
  public ResponseEntity<Void> updatePayment(
      @PathVariable Integer pagoId,
      @RequestBody UpdatePaymentRequest request) {
    service.updatePaymentStatus(pagoId, request.getNewStatus());
    return ResponseEntity.noContent().build();
  }

  /**
   * Updates the delivery status of an order.
   *
   * @param orderId The ID of the order.
   * @param request The new delivery status.
   * @return A response entity with no content.
   */
  @PatchMapping("/{orderId}/delivery-status")
  public ResponseEntity<Void> updateDeliveryStatus(
      @PathVariable Integer orderId,
      @RequestBody UpdateDeliveryStatusRequest request) {
    service.updateDeliveryStatus(orderId, request.getNewStatus());
    return ResponseEntity.noContent().build();
  }

  /**
   * Retries payment for a specific order.
   *
   * @param orderId The ID of the order.
   * @param request The retry payment request.
   * @return The updated order response.
   */
  @PostMapping("/{orderId}/retry-payment")
  public ResponseEntity<OrderResponse> retryPayment(
      @PathVariable Integer orderId,
      @RequestBody com.uade.back.dto.order.RetryPaymentRequest request) {
    return ResponseEntity.ok(service.retryPayment(orderId, request));
  }
}
