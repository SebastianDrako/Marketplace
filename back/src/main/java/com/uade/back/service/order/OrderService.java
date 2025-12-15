package com.uade.back.service.order;

import java.util.List;

import com.uade.back.dto.order.CreateOrderRequest;
import com.uade.back.dto.order.OrderDTO;
import com.uade.back.dto.order.OrderIdRequest;
import com.uade.back.dto.order.OrderResponse;
import com.uade.back.dto.order.RetryPaymentRequest;

/**
 * Service interface for managing orders.
 */
public interface OrderService {
  /**
   * Creates a new order.
   *
   * @param request The order creation details.
   * @return The created order response.
   */
  OrderResponse create(CreateOrderRequest request);

  /**
   * Retrieves an order by its ID.
   *
   * @param request The order ID request.
   * @return The order response.
   */
  OrderResponse getById(OrderIdRequest request);

  /**
   * Retrieves all orders for the current user.
   *
   * @return A list of order DTOs.
   */
  List<OrderDTO> getMyOrders();

  /**
   * Retrieves all orders (admin).
   *
   * @return A list of order DTOs.
   */
  List<OrderDTO> getAllOrders();

  /**
   * Updates the payment status of an order.
   *
   * @param pagoId   The ID of the payment.
   * @param newStatus The new status.
   */
    void updatePaymentStatus(Integer pagoId, String newStatus);

    /**
     * Updates the delivery status of an order.
     *
     * @param orderId   The ID of the order.
     * @param newStatus The new status.
     */
    void updateDeliveryStatus(Integer orderId, String newStatus);

    /**
     * Retries payment for an order.
     *
     * @param orderId The ID of the order.
     * @param request The retry payment details.
     * @return The updated order response.
     */
    OrderResponse retryPayment(Integer orderId, RetryPaymentRequest request);

    /**
     * Retrieves all payments for a specific order.
     *
     * @param orderId The ID of the order.
     * @return A list of payment DTOs.
     */
    List<com.uade.back.dto.order.PaymentDTO> getOrderPayments(Integer orderId);
}
