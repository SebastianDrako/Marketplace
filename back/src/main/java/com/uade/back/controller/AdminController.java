package com.uade.back.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.dto.OtpDTO;
import com.uade.back.dto.catalog.ProductPageResponse;
import com.uade.back.dto.order.OrderDTO;
import com.uade.back.dto.user.AdminUserUpdateDTO;
import com.uade.back.dto.user.UserListDTO;
import com.uade.back.service.order.OrderService;
import com.uade.back.service.product.ProductService;
import com.uade.back.service.security.AuthenticationService;
import com.uade.back.service.user.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for administrative actions.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final OrderService orderService;
    private final ProductService productService;

    /**
     * Updates user details by an admin.
     *
     * @param userId             The ID of the user to update.
     * @param adminUserUpdateDTO The new user details.
     * @return A response entity with no content.
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> adminUpdateUser(@PathVariable Integer userId, @RequestBody AdminUserUpdateDTO adminUserUpdateDTO) {
        userService.adminUpdateUser(userId, adminUserUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * Checks the OTP for a user (admin only).
     *
     * @param userId The ID of the user.
     * @return The OTP details.
     */
    @GetMapping("/users/{userId}/otp")
    public ResponseEntity<OtpDTO> adminCheckOtp(@PathVariable Integer userId) {
        return ResponseEntity.ok(authenticationService.adminCheckOtp(userId));
    }

    /**
     * Regenerates OTP for a user (admin only).
     *
     * @param userId The ID of the user.
     * @return The new OTP details.
     */
    @PostMapping("/users/{userId}/regenerate-otp")
    public ResponseEntity<OtpDTO> adminRegenerateOtp(@PathVariable Integer userId) {
        return ResponseEntity.ok(authenticationService.adminRegenerateOtp(userId));
    }

    /**
     * Retrieves all orders in the system.
     *
     * @return A list of all orders.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userService.getAllUsers().stream()
                .map(UserListDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Searches for products with administrative filters (like active status).
     *
     * @param categoryId Optional category ID filter.
     * @param q          Optional search query.
     * @param active     Optional active status filter.
     * @param page       Page number (default 0).
     * @param size       Page size (default 20).
     * @return A paginated response of products.
     */
    @GetMapping("/products/all")
    public ResponseEntity<ProductPageResponse> searchAdmin(
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Boolean active,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(productService.searchAdmin(categoryId, q, active, page, size));
    }

    /**
     * Retrieves payments for a specific order.
     *
     * @param orderId The ID of the order.
     * @return A list of payments associated with the order.
     */
    @GetMapping("/orders/{orderId}/payments")
    public ResponseEntity<List<com.uade.back.dto.order.PaymentDTO>> getOrderPayments(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderPayments(orderId));
    }
}
