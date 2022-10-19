package com.codevui.realworldapp.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codevui.realworldapp.entity.User;
import com.codevui.realworldapp.exception.custom.CustomBadRequestException;
import com.codevui.realworldapp.exception.custom.CustomNotFoundException;
import com.codevui.realworldapp.mapper.UserMapper;
import com.codevui.realworldapp.model.user.CustomError;
import com.codevui.realworldapp.model.user.dto.ProfileDTOReponse;
import com.codevui.realworldapp.model.user.dto.UserDTOCreateRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOLoginRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOReponse;
import com.codevui.realworldapp.repository.UserRepository;
import com.codevui.realworldapp.service.UserService;
import com.codevui.realworldapp.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, UserDTOReponse> authenticate(Map<String, UserDTOLoginRequest> userLoginRequestMap)
            throws CustomBadRequestException {
        UserDTOLoginRequest userDTOLoginRequest = userLoginRequestMap.get("user");

        Optional<User> userOptional = userRepository.findByEmail(userDTOLoginRequest.getEmail());

        boolean isAuthen = false;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String rawPassword = userDTOLoginRequest.getPassword();
            String password = user.getPassword();
            if (passwordEncoder.matches(rawPassword, password)) {
                isAuthen = true;
            }
        }
        if (!isAuthen) {
            // System.out.println("User name and password incorrect");
            throw new CustomBadRequestException(
                    CustomError.builder().code("400").message("Username or password incorrect").build());
        }

        Map<String, UserDTOReponse> wrapper = new HashMap<>();
        wrapper = buildUserDTOReponse(userOptional.get());
        return wrapper;

    }

    @Override
    public Map<String, UserDTOReponse> registerUser(Map<String, UserDTOCreateRequest> userCreateMap) {
        UserDTOCreateRequest userDTOCreateRequest = userCreateMap.get("user");
        User user = UserMapper.toUser(userDTOCreateRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        Map<String, UserDTOReponse> wrapper = new HashMap<>();
        wrapper = buildUserDTOReponse(user);
        return wrapper;
    }


    @Override
    public Map<String, UserDTOReponse> getCurrentUser() throws CustomNotFoundException {
        User userLoggedIn = getUserLoggedIn();
        if (userLoggedIn != null) {
            return buildUserDTOReponse(userLoggedIn);
        }
        throw new CustomNotFoundException(CustomError.builder().code("404").message("User not exist").build());
    }

    // return user logging
    public User getUserLoggedIn() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(email).get();
            return user;
        }
        return null;
    }

    @Override
    public Map<String, ProfileDTOReponse> getProfile(String username) throws CustomNotFoundException {

        Optional<User> userOptional = userRepository.findByUsername(username);
        Map<String, ProfileDTOReponse> wrapper = new HashMap<>();

        if (!userOptional.isPresent()) {
            throw new CustomNotFoundException(
                    CustomError.builder().code("404").message("Profile's user is not found").build());
        }
        
        // check xem nguoi loggin hien tai co following nguoi can xem profile
        User userLoggedIn = getUserLoggedIn();
        User user = userOptional.get();
        Set<User> followers = user.getFollowers();
        boolean isFollowing = false;
        for (User U : followers){
            if (userLoggedIn.getId() == U.getId()){
                isFollowing = true;
                break;
            }

        }
        
        wrapper = buildProfileDTOReponse(userOptional.get(),isFollowing);
        
        return wrapper;
    }

    @Override
    public Map<String, ProfileDTOReponse> followUser(String username) throws CustomNotFoundException {
        
        // lay ra user ma minh can follow
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.get();
        Map<String, ProfileDTOReponse> wrapper = new HashMap<>();
        if (!userOptional.isPresent()) {
            throw new CustomNotFoundException(
                    CustomError.builder().code("404").message("Profile's user is not found").build());
        }
        // nguoi user loggin hien tai follow nguoi user can follow o tren
        User userLoggedIn = getUserLoggedIn();
        Set<User> followers = user.getFollowers();
        boolean isFollowing = false;
        for (User U : followers){
            if (userLoggedIn.getId() == U.getId()){
                isFollowing = true;
                break;
            }

        }

        if (!isFollowing){
            isFollowing = true;
            user.getFollowers().add(userLoggedIn);
            user = userRepository.save(user);
            isFollowing = true;
        }
        
        wrapper = buildProfileDTOReponse(user,isFollowing);
        
        return wrapper;
    }

    @Override
    public Map<String, ProfileDTOReponse> unfollowUser(String username) throws CustomNotFoundException {
        // lay ra user can unfollow co ten la "username"
        Optional <User> userOptional = userRepository.findByUsername(username);
        Map<String, ProfileDTOReponse> wrapper = new HashMap<>();
        if (!userOptional.isPresent()){
            throw new CustomNotFoundException(
                CustomError.builder().code("404").message("Profile's user is not found").build());
        }
        User user = userOptional.get();

        // lay ra user dang login hien tai
        User userLoggined = getUserLoggedIn();

        //ktra xem userLoggined co dang follow user khong
        Set<User> followerOfUser = user.getFollowers();
        boolean isFollowing = false;
        for (User U : followerOfUser) {
            if (U.getId() == userLoggined.getId()){
                isFollowing = true;
                break;
            }
        }

        if (isFollowing){
            user.getFollowers().remove(userLoggined);
            isFollowing = false;
            user = userRepository.save(user);
        }

        wrapper = buildProfileDTOReponse(user, isFollowing);
        return wrapper;
    }

    private Map<String, ProfileDTOReponse> buildProfileDTOReponse(User user, boolean isFollowing) {
        Map<String, ProfileDTOReponse> wrapper = new HashMap<>();
        ProfileDTOReponse profileDTOReponse = UserMapper.toProfileDTOReponse(user,isFollowing);
        wrapper.put("profile", profileDTOReponse);
        return wrapper;
    }

    private Map<String, UserDTOReponse> buildUserDTOReponse(User user) {
        Map<String, UserDTOReponse> wrapper = new HashMap<>();
        UserDTOReponse userDTOReponse = UserMapper.toUserDTOResponse(user);
        String token = jwtTokenUtil.generateToken(user, (long) (24 * 60 * 60));
        userDTOReponse.setToken(token);
        wrapper.put("user", userDTOReponse);
        return wrapper;
    }



}
