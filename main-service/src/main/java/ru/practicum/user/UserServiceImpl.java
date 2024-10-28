package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserDto;
import ru.practicum.user.model.dto.UserParam;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    private void emailExistsCheck(String email) {
        if(userRepository.findFirstByEmail(email).isPresent()) {
            throw new ConstraintConflictException("Such user email already exsists .");
        }
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        emailExistsCheck(newUserRequest.getEmail());
        UserEntity entity = userRepository.save(mapper.toUserEntity(newUserRequest));
        return mapper.toUserDto(entity);
    }

    @Override
    public List<UserDto> getAll(UserParam params) {
        PaginationHelper<UserEntity> paginationHelper = new PaginationHelper<>(params.getFrom(), params.getSize());
        List<UserEntity> entities;
        if (params.getIds().isEmpty()) {
            entities = paginationHelper.findAllWithPagination(userRepository::findAll);
        } else {
            entities = paginationHelper.findAllWithPagination(userRepository::findAllByIdIn, params.getIds());
        }
        return mapper.toUserDto(entities);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
