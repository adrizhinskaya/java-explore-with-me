package ru.practicum.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserParam;
import ru.practicum.user.model.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class UserMapper {
    public UserEntity toUserEntity(NewUserRequest dto) {
        return UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserDto toUserDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public List<UserDto> toUserDto(Iterable<UserEntity> entities) {
        List<UserDto> dtos = new ArrayList<>();
        for (UserEntity entity : entities) {
            dtos.add(toUserDto(entity));
        }
        return dtos;
    }

    public UserShortDto toUserShortDto(UserEntity dto) {
        return UserShortDto.builder()
                .id(dto.getId())
                .name(dto.getEmail())
                .build();
    }

    public UserParam createUserParam(Set<Long> ids, Integer from, Integer size) {
        return UserParam.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
    }
}