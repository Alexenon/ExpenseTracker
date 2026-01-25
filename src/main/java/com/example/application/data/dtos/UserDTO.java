package com.example.application.data.dtos;

import com.example.application.entities.User;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;

    public static UserDTO mappedFrom(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
