package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.model.dto.NewUserDto;
import ru.practicum.user.model.dto.UserDto;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(NewUserDto newUserDto) {
        UserEntity entity = userRepository.save(UserMapper.mapToUserEntity(newUserDto));
        return UserMapper.mapToUserDto(entity);
    }

    @Override
    public List<UserDto> getAll(Set<Long> ids, int from, int size) {
        PaginationHelper<UserEntity> paginationHelper = new PaginationHelper<>(from, size);
        List<UserEntity> entities = paginationHelper.findAllWithPagination(userRepository::findAllByIdIn, ids);
        return UserMapper.mapToUserDto(entities);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
