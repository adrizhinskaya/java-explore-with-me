package ru.practicum.user.model;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserParam;
import ru.practicum.user.model.dto.UserShortDto;

import java.util.List;
import java.util.Set;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    public abstract UserEntity toUserEntity(NewUserRequest dto);

    public abstract UserDto toUserDto(UserEntity dto);

    public abstract List<UserDto> toUserDto(Iterable<UserEntity> dto);

    public abstract UserShortDto toUserShortDto(UserEntity dto);

    public UserParam createUserParam(Set<Long> ids, Integer from, Integer size) {
        return UserParam.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
    }
}