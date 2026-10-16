package com.flower.service;

import com.flower.entity.Address;
import com.flower.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public List<Address> getAddressList(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }

    public Address getAddressById(Long id) {
        return addressMapper.findById(id);
    }

    public void addAddress(Address address) {
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        if (address.getIsDefault() == 1) {
            addressMapper.clearDefault(address.getUserId());
        }

        addressMapper.insert(address);
    }

    public void updateAddress(Address address) {
        address.setUpdatedAt(LocalDateTime.now());
        addressMapper.update(address);
    }

    public void setDefaultAddress(Long userId, Long addressId) {
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(addressId);
    }

    public void deleteAddress(Long id) {
        addressMapper.deleteById(id);
    }
}