package com.codesoft.edu.dto;

import com.codesoft.edu.model.User;

public class UserTransformer {

    public static UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().getName(),
                user.getMyTodos(),
                user.getOtherTodos()
        );
    }

    public static User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setMyTodos(userDto.getMyTodos());
        user.setOtherTodos(userDto.getOtherTodos());
        return user;
    }

}
