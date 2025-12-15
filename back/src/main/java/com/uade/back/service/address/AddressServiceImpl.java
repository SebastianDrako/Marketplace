package com.uade.back.service.address;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.back.dto.address.AddressDto;
import com.uade.back.dto.address.CreateAddressRequest;
import com.uade.back.entity.Address;
import com.uade.back.entity.UserInfo;
import com.uade.back.entity.Usuario;
import com.uade.back.repository.AddressRepository;
import com.uade.back.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the AddressService.
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Helper method to get the current authenticated user's information.
     *
     * @return The UserInfo entity.
     */
    private UserInfo getCurrentUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUserInfo_Mail(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
        return usuario.getUserInfo();
    }

    /**
     * Creates a new address for the current user.
     *
     * @param addressRequest The address creation details.
     * @return The created address DTO.
     */
    @Override
    public AddressDto createAddress(CreateAddressRequest addressRequest) {
        UserInfo userInfo = getCurrentUserInfo();

        Address address = new Address();
        address.setStreet(addressRequest.getStreet());
        address.setApt(addressRequest.getApt());
        address.setPostalCode(addressRequest.getPostalCode());
        address.setOthers(addressRequest.getOthers());
        address.setName(addressRequest.getName());
        // Link the address to the current user
        address.getUsersInfo().add(userInfo);
        
        Address savedAddress = addressRepository.save(address);
        return AddressDto.fromEntity(savedAddress);
    }

    /**
     * Retrieves all addresses associated with the current user.
     *
     * @return A list of address DTOs.
     */
    @Override
    public List<AddressDto> getAddresses() {
        UserInfo userInfo = getCurrentUserInfo();
        
        // Convert user's addresses to DTOs
        return userInfo.getAddresses().stream()
                .map(AddressDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific address by its ID.
     *
     * @param addressId The ID of the address.
     * @return The address DTO.
     */
    @Override
    public AddressDto getAddressById(Integer addressId) {
        UserInfo userInfo = getCurrentUserInfo();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verify that the address belongs to the authenticated user
        boolean isUserAddress = address.getUsersInfo().stream()
                .anyMatch(ui -> ui.getUserInfoId().equals(userInfo.getUserInfoId()));

        if (!isUserAddress) {
            throw new RuntimeException("Address does not belong to the user");
        }

        return AddressDto.fromEntity(address);
    }

    /**
     * Updates an existing address.
     *
     * @param addressId      The ID of the address to update.
     * @param addressRequest The address update details.
     * @return The updated address DTO.
     */
    @Override
    public AddressDto updateAddress(Integer addressId, CreateAddressRequest addressRequest) {
        UserInfo userInfo = getCurrentUserInfo();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verify ownership before updating
        boolean isUserAddress = address.getUsersInfo().stream()
                .anyMatch(ui -> ui.getUserInfoId().equals(userInfo.getUserInfoId()));

        if (!isUserAddress) {
            throw new RuntimeException("Address does not belong to the user");
        }

        address.setStreet(addressRequest.getStreet());
        address.setApt(addressRequest.getApt());
        address.setPostalCode(addressRequest.getPostalCode());
        address.setOthers(addressRequest.getOthers());
        address.setName(addressRequest.getName());

        Address updatedAddress = addressRepository.save(address);
        return AddressDto.fromEntity(updatedAddress);
    }

    /**
     * Deletes an address by its ID.
     *
     * @param addressId The ID of the address to delete.
     */
    @Override
    public void deleteAddress(Integer addressId) {
        UserInfo userInfo = getCurrentUserInfo();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verify ownership before deleting
        boolean isUserAddress = address.getUsersInfo().stream()
                .anyMatch(ui -> ui.getUserInfoId().equals(userInfo.getUserInfoId()));

        if (!isUserAddress) {
            throw new RuntimeException("Address does not belong to the user");
        }

        addressRepository.delete(address);
    }
}
