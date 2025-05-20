package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.user.UserDto;
import com.lenaevd.advertisements.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    List<UserDto> usersToUserDtos(List<User> users);
}
