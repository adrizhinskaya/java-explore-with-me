package ru.practicum.user.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.user.model.dto.NewUserDto;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserEntity mapToUserEntity(NewUserDto newUserDto) {
        return UserEntity.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }

    public static UserDto mapToUserDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }

    public static UserShortDto mapToUserShortDto(UserEntity userEntity) {
        return UserShortDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .build();
    }

    public static List<UserDto> mapToUserDto(Iterable<UserEntity> userEntities) {
        List<UserDto> dtos = new ArrayList<>();
        for (UserEntity entity : userEntities) {
            dtos.add(mapToUserDto(entity));
        }
        return dtos;
    }
}
