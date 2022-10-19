package com.codevui.realworldapp.mapper;

import com.codevui.realworldapp.entity.User;
import com.codevui.realworldapp.model.user.dto.ProfileDTOReponse;
import com.codevui.realworldapp.model.user.dto.UserDTOCreateRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOReponse;

public class UserMapper {
    public static UserDTOReponse toUserDTOResponse(User user){
        return UserDTOReponse
        .builder()
        .email(user.getEmail())
        .username(user.getUsername())
        .bio(user.getBio())
        .image(user.getImage())
        .build();
    }

    public static User toUser(UserDTOCreateRequest userDTOCreateRequest) {
        return User
        .builder()
        .email(userDTOCreateRequest.getEmail())
        .password(userDTOCreateRequest.getPassword())
        .username(userDTOCreateRequest.getUsername())
        .build();

    }

    public static ProfileDTOReponse toProfileDTOReponse(User user,boolean isFollowing) {
        return ProfileDTOReponse
        .builder()
        .username(user.getUsername())
        .bio(user.getBio())
        .image(user.getImage())
        .following(isFollowing)
        .build();
    }
}
