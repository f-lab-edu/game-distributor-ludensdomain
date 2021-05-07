package com.ludensdomain.mapper;

import com.ludensdomain.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserDto getUserInfo(long id);

    void insertUserInfo(UserDto userDto);

    void updateUserInfo(long id, UserDto userDto);

    void changePassword(long id, String password);

    Boolean checkIdExists(long id);

    void deleteUser(long id);

}
