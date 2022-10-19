package com.codevui.realworldapp.model.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDTOReponse {
    private String username;
    private String bio;
    private String image;
    private boolean following;
}
