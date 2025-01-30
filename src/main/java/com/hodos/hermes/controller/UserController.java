package com.hodos.hermes.controller;

import com.hodos.hermes.dto.dtos.UserDto;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.Error;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hodos.hermes.utils.ValidationUtil.doObjectValidation;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/u")
    public String register(@RequestBody UserDto userDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication != null ? authentication.getName() : null;

        if (userEmail == null) {
            throw new CustomException(ErrorTypes.UN_AUTHORIZED,"User not authenticated");
        }
        System.out.println(userEmail);
        List<Error> errorList = doObjectValidation(userDto);
        if(errorList.isEmpty())
            return userService.updateUser(userDto);
        else throw new CustomException(ErrorTypes.INVALID_DATA,errorList);
    }

    @GetMapping()
    public String getUser(@RequestParam("userId")String userId,@RequestParam("email")String email){

    }

}
