package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserNewRequest;
import ru.practicum.ewm.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserNewRequest userNewRequest) {
        return userService.addUser(userNewRequest);
    }
}
