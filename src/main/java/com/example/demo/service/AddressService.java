// Sửa file: src/main/java/com/example/demo/service/AddressService.java
package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User currentUser = getCurrentUser();
        Address address = addressMapper.toAddress(request);
        address.setUser(currentUser);

        // Nếu địa chỉ mới được set là default, bỏ default của các địa chỉ cũ
        if (request.isDefault()) {
            addressRepository.unsetAllDefaultsForUser(currentUser);
        }

        Address savedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(savedAddress);
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request) {
        User currentUser = getCurrentUser();
        Address address = findAddressByIdAndUser(addressId, currentUser);

        // Nếu địa chỉ này được set là default, bỏ default của các địa chỉ cũ
        if (request.isDefault() && !address.isDefault()) {
            addressRepository.unsetAllDefaultsForUser(currentUser);
        }

        addressMapper.updateAddress(address, request);
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        User currentUser = getCurrentUser();
        Address address = findAddressByIdAndUser(addressId, currentUser);
        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        User currentUser = getCurrentUser();
        return addressRepository.findByUser(currentUser).stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddress(Long addressId) {
        User currentUser = getCurrentUser();
        Address address = findAddressByIdAndUser(addressId, currentUser);
        return addressMapper.toAddressResponse(address);
    }

    // --- Helper Methods ---
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private Address findAddressByIdAndUser(Long addressId, User user) {
        return addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
    }
}