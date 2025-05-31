package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserNewRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        if (ids != null && ids.length > 0) {
            return userRepository.findAllByIdIn(ids, page).stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        } else {
            return userRepository.findAll(page).stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        }
    }

    @Override
    public UserDto addUser(UserNewRequest userNewRequest) {
        User newUser = UserMapper.toUser(userNewRequest);

        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));
    }
}
