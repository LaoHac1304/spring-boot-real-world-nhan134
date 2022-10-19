package com.codevui.realworldapp.exception.custom;

import com.codevui.realworldapp.model.user.CustomError;

public class CustomNotFoundException extends BaseCustomException{

    public CustomNotFoundException(CustomError customError) {
        super(customError);
        //TODO Auto-generated constructor stub
    }
    
}
