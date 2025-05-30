package ru.practicum.ewm.service.user;

import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserNewRequest;
import ru.practicum.ewm.model.user.User;

import java.util.List;

public interface UserService {
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size);
    public UserDto addUser(UserNewRequest userNewRequest);
    public void deleteUser(Long userId);
    public User getUserById(Long userId);
}
