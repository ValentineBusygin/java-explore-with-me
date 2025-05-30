package ru.practicum.ewm.model.user;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserNewRequest;
import ru.practicum.ewm.dto.user.UserShortDto;

@UtilityClass
public class UserMapper {
    public static User toUser(UserNewRequest userNewRequest) {
        return User.builder()
                .name(userNewRequest.getName())
                .email(userNewRequest.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
