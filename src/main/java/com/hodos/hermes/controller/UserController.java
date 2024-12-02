package com.hodos.hermes.controller;

import com.hodos.hermes.dto.dtos.UserDto;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.Error;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hodos.hermes.utils.ValidationUtil.doObjectValidation;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/u")
    private String register(@RequestBody UserDto userDto){
        List<Error> errorList = doObjectValidation(userDto);
        if(errorList.isEmpty())
            return userService.updateUser(userDto);
        else throw new CustomException(ErrorTypes.INVALID_DATA,errorList);
    }
}
