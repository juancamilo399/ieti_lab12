package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication2.model.LoginWrapper;
import com.example.myapplication2.model.Token;
import com.example.myapplication2.services.AuthService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newFixedThreadPool( 1 );
    private Retrofit retrofit;
    private AuthService authService;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onSubmitLogin(View view){
        initialize();
        String email=editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        if(!email.isEmpty()&&!password.isEmpty()){
            executorService.execute( new Runnable() {
                @Override
                public void run() {
                    try{
                        Response<Token> response =
                                authService.login( new LoginWrapper( email, password ) ).execute();
                        Token token = response.body();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(response.isSuccessful()){
                                    editor.putString( LaunchActivity.TOKEN_KEY,token.getAccessToken());
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    editTextEmail.setText("");
                                    editTextPassword.setText("");
                                }
                                else{
                                    editTextPassword.setError("User and password do not match");
                                }
                            }
                        });
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
        if (email.isEmpty()){
            editTextEmail.setError("This field can not be blank");
        }
        if(password.isEmpty()){
            editTextPassword.setError("This field can not be blank");
        }
    }

    private void initialize(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http:/10.0.2.2:8080") //localhost for emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authService = retrofit.create(AuthService.class);
        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        SharedPreferences sharedPref =
                getSharedPreferences( getString( R.string.preference_file_key), Context.MODE_PRIVATE );
        editor = sharedPref.edit();

    }
}