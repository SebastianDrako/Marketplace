package com.uade.back.service.address;

import java.util.List;

import com.uade.back.dto.address.AddressDto;
import com.uade.back.dto.address.CreateAddressRequest;

/**
 * Service interface for managing addresses.
 */
public interface AddressService {
    /**
     * Creates a new address for the current user.
     *
     * @param addressRequest The address creation details.
     * @return The created address DTO.
     */
    AddressDto createAddress(CreateAddressRequest addressRequest);

    /**
     * Retrieves all addresses associated with the current user.
     *
     * @return A list of address DTOs.
     */
    List<AddressDto> getAddresses();

    /**
     * Retrieves a specific address by its ID.
     *
     * @param addressId The ID of the address.
     * @return The address DTO.
     */
    AddressDto getAddressById(Integer addressId);

    /**
     * Updates an existing address.
     *
     * @param addressId      The ID of the address to update.
     * @param addressRequest The address update details.
     * @return The updated address DTO.
     */
    AddressDto updateAddress(Integer addressId, CreateAddressRequest addressRequest);

    /**
     * Deletes an address by its ID.
     *
     * @param addressId The ID of the address to delete.
     */
    void deleteAddress(Integer addressId);
}
