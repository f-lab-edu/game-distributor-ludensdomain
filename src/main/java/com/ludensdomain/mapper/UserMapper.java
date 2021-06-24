package com.ludensdomain.mapper;

import com.ludensdomain.dto.GameDto;
import com.ludensdomain.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    UserDto getUserInfo(long id);

    void insertUserInfo(UserDto userDto);

    void updateUserInfo(UserDto userDto);

    void changePassword(long id, String password);

    boolean checkIdExists(long id);

    void deleteUser(long id);

    List<GameDto> getPurchaseHistory(long id);

}
