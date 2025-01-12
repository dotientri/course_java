package com.example.demo.mapper;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
// bap cho mapstruct biet la cta se

public interface UserMapper {
    User toUser(UserCreationRequest request);
//    source  la nguon de ta map
//    target la ta se map ve
//    lastName va firstName bay gio se trung nhau
// tren database van vay con tren postn=man thi no hay doi thanh 2 cai giong nhau
//    @Mapping(source = "lastName",target = "firstName")
//    @Mapping(target = "firstName",ignore = true)
//    nay se khong map cai fild class name
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
//    cta se define map data userupdate request vap user
}
