package com.hodos.hermes.service.impl;

import com.hodos.hermes.athena.UserScrolls;
import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.dtos.UserDto;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.hodos.hermes.utils.mapper.UserMapper.getUser;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserScrolls userScrolls;

    public UserServiceImpl(UserScrolls userScrolls) {
        this.userScrolls = userScrolls;
    }

    @Override
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
                return userScrolls.findByEmail(userName).orElseThrow(()-> new CustomException(ErrorTypes.NOT_FOUND,"User not found"));
            }
        };
    }

    @Override
    public String updateUser(UserDto userDto) {
        try {
            String email = userDto.getEmail();
            Optional<User> optionalTraveller = userScrolls.findByEmail(email);
            if (optionalTraveller.isEmpty()) {
                throw new CustomException(ErrorTypes.NOT_FOUND,String.format("Account Not fount -> %s",email));
            }
            long pk = optionalTraveller.get().getId();
            User user = getUser(userDto);
            user.setId(pk);
            userScrolls.save(user);
            return "Saved successfully";
        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error - > ", e);
            throw new CustomException(ErrorTypes.INTERNAL_SERVER_ERROR);
        }

    }
}
