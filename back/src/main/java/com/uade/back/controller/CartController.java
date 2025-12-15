package com.uade.back.controller;

import com.uade.back.dto.cart.AddItemRequest;
import com.uade.back.dto.cart.CartResponse;
import com.uade.back.dto.cart.UpdateItemRequest;
import com.uade.back.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing the shopping cart.
 */
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    /**
     * Retrieves the current user's cart.
     *
     * @return The cart details.
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(service.getCurrentCart());
    }

    /**
     * Adds an item to the cart.
     *
     * @param request The item details to add.
     * @return The updated cart.
     */
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
        @RequestBody AddItemRequest request
    ) {
        return ResponseEntity.ok(service.addItem(request));
    }

    /**
     * Updates an item in the cart.
     *
     * @param itemId  The ID of the item to update.
     * @param request The update details (e.g., quantity).
     * @return The updated cart.
     */
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
        @PathVariable Integer itemId,
        @RequestBody UpdateItemRequest request
    ) {
        return ResponseEntity.ok(service.updateItem(itemId, request));
    }

    /**
     * Removes an item from the cart.
     *
     * @param itemId The ID of the item to remove.
     * @return The updated cart.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
        @PathVariable Integer itemId
    ) {
        return ResponseEntity.ok(service.removeItem(itemId));
    }

    /**
     * Clears all items from the cart.
     *
     * @return The cleared cart.
     */
    @DeleteMapping("/items")
    public ResponseEntity<CartResponse> clear() {
        return ResponseEntity.ok(service.clear());
    }

    /**
     * Applies a coupon to the cart.
     *
     * @param request The coupon application request.
     * @return The updated cart with the coupon applied.
     */
    @PostMapping("/cupon")
    public ResponseEntity<CartResponse> aplicarCupon(
        @RequestBody com.uade.back.dto.cart.AplicarCuponRequest request
    ) {
        return ResponseEntity.ok(service.aplicarCupon(request));
    }

    /**
     * Removes the applied coupon from the cart.
     *
     * @return The updated cart without the coupon.
     */
    @DeleteMapping("/cupon")
    public ResponseEntity<CartResponse> removeCoupon() {
        return ResponseEntity.ok(service.removeCoupon());
    }
}
