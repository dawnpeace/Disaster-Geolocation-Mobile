package com.example.disastergeolocation;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disastergeolocation.RetrofitInterface.Authentication;
import com.google.android.gms.auth.api.Auth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etFullname, etIdentityNumber, etPhone, etAddress;
    private RadioGroup rgGender;
    private Button btnRegister, btnBack, btnUploadImage;
    private ImageView ivProfile;

    private String email, fullname, password, identityNumber, phone, gender, address;

    private boolean isUploading = false;

    private static final int PROFILE_PICTURE_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(ivProfile);
        }
    }

    protected void initView() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etFullname = findViewById(R.id.et_fullname);
        etIdentityNumber = findViewById(R.id.et_identity_number);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        rgGender = findViewById(R.id.rg_gender);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);
        ivProfile = findViewById(R.id.iv_profile);
        btnUploadImage = findViewById(R.id.btn_upload_image);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                fullname = etFullname.getText().toString();
                identityNumber = etIdentityNumber.getText().toString();
                phone = etPhone.getText().toString();
                gender = rgGender.getCheckedRadioButtonId() == R.id.rb_gender_male ? "male" : "female";
                address = etAddress.getText().toString();

                if (!isUploading) {
                    doUpload(fullname, email, password, phone, gender, identityNumber, address);
                } else {
                    Toast.makeText(RegisterActivity.this, "Terjadi Kesalahan !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    protected void doUpload(String name, String email, String password, String phone, String gender, String identityNumber, String address) {

        if(!checkFormValid(email,name,password,identityNumber,address)){
            return;
        }

        RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody rbEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody rbPassword = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody rbPhone = RequestBody.create(MediaType.parse("text/plain"), phone);
        RequestBody rbGender = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody rbIdentityNumber = RequestBody.create(MediaType.parse("text/plain"), identityNumber);
        RequestBody rbAddress = RequestBody.create(MediaType.parse("text/plain"), address);


        Retrofit retrofit = RetrofitInstance.getRetrofit();
        Authentication authentication = retrofit.create(Authentication.class);
        Call<Void> call = authentication.register(rbName, rbEmail, rbPassword, rbIdentityNumber, rbPhone, rbGender, rbAddress);

        isUploading = true;

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Akun Berhasil dibuat !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Terjadi Kesalahan, HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                isUploading = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Failure" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                isUploading = false;
            }
        });
    }

    protected boolean checkFormValid(String email, String fullname, String password, String identityNumber, String address){
        boolean isValid = true;
        if(email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Email tidak valid");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        if(fullname.isEmpty()){
            etFullname.setError("Harap isikan nama");
            isValid = false;
        } else {
            etFullname.setError(null);
        }

        if(password.length() < 6){
            etPassword.setError("Harap isikan password");
            isValid = false;
        } else {
            etPassword.setError(null);
        }

        if(phone.length() < 9 ){
            etPhone.setError("Nomor tidak valid");
            isValid = false;
        } else {
            etPhone.setError(null);
        }

        if(identityNumber.length() < 10){
            etIdentityNumber.setError("Nomor identitas tidak valid");
            isValid = false;
        } else {
            etIdentityNumber.setError(null);
        }

        if(address.isEmpty()){
            etAddress.setError("Harap isikan alamat");
            isValid = false;
        } else {
            etAddress.setError(null);
        }

        return isValid;
    }

}
