package com.example.demo.mapper;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // Báo cho MapStruct bỏ qua các trường này vì chúng ta xử lý thủ công
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Address toAddress(AddressRequest request);

    @Mapping(source = "address", target = "fullAddress", qualifiedByName = "toFullAddressString")
    AddressResponse toAddressResponse(Address address);

    // Tương tự, bỏ qua các trường không cần cập nhật từ request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateAddress(@MappingTarget Address address, AddressRequest request);

    @Named("toFullAddressString")
    default String toFullAddressString(Address address) {
        if (address == null) {
            return null;
        }
        // Logic này bây giờ sẽ hoạt động vì entity đã có đủ các trường
        return String.join(", ",
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getProvince());
    }
}