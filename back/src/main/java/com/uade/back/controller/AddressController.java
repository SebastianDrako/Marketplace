package com.uade.back.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.dto.address.AddressDto;
import com.uade.back.dto.address.CreateAddressRequest;
import com.uade.back.service.address.AddressService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing addresses.
 */
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Creates a new address for the authenticated user.
     *
     * @param addressRequest The address details.
     * @return The created address.
     */
    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@RequestBody CreateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.createAddress(addressRequest));
    }

    /**
     * Retrieves all addresses for the authenticated user.
     *
     * @return A list of addresses.
     */
    @GetMapping
    public ResponseEntity<List<AddressDto>> getAddresses() {
        return ResponseEntity.ok(addressService.getAddresses());
    }

    /**
     * Retrieves a specific address by its ID.
     *
     * @param addressId The ID of the address.
     * @return The address.
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Integer addressId) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    /**
     * Updates an existing address.
     *
     * @param addressId      The ID of the address to update.
     * @param addressRequest The new address details.
     * @return The updated address.
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Integer addressId, @RequestBody CreateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressRequest));
    }

    /**
     * Deletes an address.
     *
     * @param addressId The ID of the address to delete.
     * @return A response entity with no content.
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
