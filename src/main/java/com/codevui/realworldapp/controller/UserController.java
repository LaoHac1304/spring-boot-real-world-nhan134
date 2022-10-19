package com.codevui.realworldapp.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codevui.realworldapp.model.user.dto.UserDTOReponse;
import com.codevui.realworldapp.service.UserService;

import lombok.RequiredArgsConstructor;

import com.codevui.realworldapp.exception.custom.CustomBadRequestException;
import com.codevui.realworldapp.exception.custom.CustomNotFoundException;
import com.codevui.realworldapp.model.user.dto.UserDTOCreateRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOLoginRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PostMapping("/users/login")
    public Map<String,UserDTOReponse> login(@RequestBody Map<String,UserDTOLoginRequest> userLoginRequestMap) throws CustomBadRequestException{
        return userService.authenticate(userLoginRequestMap);
    }

    @PostMapping("/users")
    public Map<String,UserDTOReponse> registerUser(@RequestBody Map<String,UserDTOCreateRequest> userCreateMap){
        return userService.registerUser(userCreateMap);
    }

    @GetMapping("/user")
    public Map<String,UserDTOReponse> getCurrentUser() throws CustomNotFoundException{
        return userService.getCurrentUser();
    }
}
