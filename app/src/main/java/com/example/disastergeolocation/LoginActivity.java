package com.example.disastergeolocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.disastergeolocation.Model.AuthModel;
import com.example.disastergeolocation.RetrofitInterface.Authentication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private EditText etUsername, etPassword;
    private String username,password;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPrefHelper = SharedPrefHelper.getInstance(this);if(sharedPrefHelper.isLoggedIn()){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
    }

    protected void initView(){
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                login(username,password);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    protected void login(String username, String password){
        if(validateString(username,password)){
            Retrofit retrofit = RetrofitInstance.getRetrofit();
            Authentication authentication = retrofit.create(Authentication.class);
            Call<AuthModel> call = authentication.login(username,password);
            call.enqueue(new Callback<AuthModel>() {
                @Override
                public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                    if(response.isSuccessful()){
                        sharedPrefHelper.storeToken(response.body().getAccess_token());
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if(response.code() > 400 && response.code() < 500){
                            Toast.makeText(LoginActivity.this, "Akun tidak ditemukan", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AuthModel> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Connection Problem", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Username/Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean validateString(String ...strings){
        if(strings.length != 0) {
            for(String string : strings){
                if(!(string.trim().length() > 0)) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

}
