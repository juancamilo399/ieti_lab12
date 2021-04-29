package com.example.myapplication2.services;

import com.example.myapplication2.model.LoginWrapper;
import com.example.myapplication2.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth")
    Call<Token> login(@Body LoginWrapper loginWrapper);
}
