package com.codevui.realworldapp.service;

import java.util.Map;

import com.codevui.realworldapp.exception.custom.CustomBadRequestException;
import com.codevui.realworldapp.exception.custom.CustomNotFoundException;
import com.codevui.realworldapp.model.user.dto.ProfileDTOReponse;
import com.codevui.realworldapp.model.user.dto.UserDTOCreateRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOLoginRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOReponse;

public interface UserService {

    public Map<String, UserDTOReponse> authenticate(Map<String, UserDTOLoginRequest> userLoginRequestMap) throws CustomBadRequestException;

    public Map<String, UserDTOReponse> registerUser(Map<String, UserDTOCreateRequest> userCreateMap);

    public Map<String, UserDTOReponse> getCurrentUser() throws CustomNotFoundException;

    public Map<String, ProfileDTOReponse> getProfile(String username) throws CustomNotFoundException;

    public Map<String, ProfileDTOReponse> followUser(String username) throws CustomNotFoundException;

    public Map<String, ProfileDTOReponse> unfollowUser(String username) throws CustomNotFoundException;
    
}
