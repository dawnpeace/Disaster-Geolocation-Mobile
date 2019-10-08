package com.example.disastergeolocation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disastergeolocation.ErrorModel.UploadFile;
import com.example.disastergeolocation.RetrofitInterface.Authentication;
import com.example.disastergeolocation.RetrofitInterface.ReportInterface;
import com.example.disastergeolocation.Tasks.LogoutTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private Button btnUploadImage, btnPolice, btnFirefighter;
    private ImageView ivImage;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;

    private static final int PICK_LOCATION_REQUEST_CODE = 20;
    private static final int PICK_IMAGE_REQUEST_CODE = 30;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 40;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 50;

    private static final int FIREFIGHTER = 0;
    private static final int POLICE = 1;

    private boolean hasLatLng = false;
    private boolean hasImage = false;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;


    private double lat;
    private double lng;
    private File file;

    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefHelper = SharedPrefHelper.getInstance(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getSupportActionBar().setTitle("EMERGENCY CALL");
        storeTokenOnceAvailable();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    file = getFileFromURI(uri);

                    Glide.with(this)
                            .load(uri)
                            .into(ivImage);

                    hasImage = file != null;

                    if(gpsEnabled && networkEnabled){
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "Harap aktifkan perizinan lokasi untuk aplikasi ini", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        Toast.makeText(MainActivity.this, location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_LONG).show();
                                        lat = location.getLatitude();
                                        lng = location.getLongitude();
                                        hasLatLng = true;
                                    } else {
                                        hasLatLng = false;
                                        Toast.makeText(MainActivity.this, "Pastikan layanan lokasi di aktifkan", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Harap aktifkan konektifiktas dan Layanan Lokasi", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:

        }
    }

    protected void initView() {
        btnUploadImage = findViewById(R.id.btn_upload_image);
        ivImage = findViewById(R.id.iv_image);
        btnPolice = findViewById(R.id.btn_get_police);
        btnFirefighter = findViewById(R.id.btn_get_firefighter);

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                openImagePicker();
            }
        });

        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Upload dokumen ke polisi ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (hasImage && hasLatLng) {
                                    doUpload(lat, lng, file, POLICE);
                                } else {
                                    Toast.makeText(MainActivity.this, "Pastikan anda telah memilih gambar dan lokasi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create()
                        .show();
            }
        });

        btnFirefighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Upload dokument ke pemadam kebakaran?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (hasImage && hasLatLng) {
                                    doUpload(lat, lng, file, FIREFIGHTER);
                                } else {
                                    Toast.makeText(MainActivity.this, "Pastikan anda telah memilih gambar dan lokasi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create()
                        .show();
            }
        });

    }

    protected File getFileFromURI(Uri uri) {
        OutputStream out = null;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String mimeType = mime.getExtensionFromMimeType(getApplicationContext().getContentResolver().getType(uri));
        String filename = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
        File file = new File(getCacheDir(), filename + "." + mimeType);
        InputStream in = null;
        try {
            in = getApplicationContext().getContentResolver().openInputStream(uri);
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void storeTokenOnceAvailable() {
        if(sharedPrefHelper.getFirebaseToken() != null && !sharedPrefHelper.issetFCMToken() && sharedPrefHelper.isLoggedIn()){
            Retrofit retrofit = RetrofitInstance.getRetrofit(sharedPrefHelper.getInterceptor());
            Authentication authentication = retrofit.create(Authentication.class);
            Call<Void> call = authentication.storeFirebaseToken(sharedPrefHelper.getFirebaseToken());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Log.d("FCMTOKENLOGGEDIN", "onResponse: "+sharedPrefHelper.getFirebaseToken());
                        sharedPrefHelper.setFcmTokenAvailability(true);
                    } else {
                        Log.d("FCMTOKEN", "onResponse: something wrong"+response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("FCMTOKEN", "onFailure: server failed");
                }
            });
        }
    }


    @AfterPermissionGranted(STORAGE_PERMISSION_REQUEST_CODE)
    private void openImagePicker() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
            imageIntent.setType("image/*");
            startActivityForResult(imageIntent, PICK_IMAGE_REQUEST_CODE);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.permissions_rationale_text), STORAGE_PERMISSION_REQUEST_CODE,permissions);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    protected void doUpload(double lat, double lng, File file, int target) {
        String curdate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        RequestBody rbLat = MultipartBody.create(MediaType.parse("text/plain"), String.valueOf(lat));
        RequestBody rbLng = MultipartBody.create(MediaType.parse("text/plain"), String.valueOf(lng));
        File compressedFile = null;
        try{
            compressedFile = new Compressor(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .compressToFile(file);
        } catch (IOException e){
            e.printStackTrace();
        }
        RequestBody rbFile = RequestBody.create(MediaType.parse("multipart/form-data"), compressedFile);
        RequestBody rbCurtime = RequestBody.create(MediaType.parse("text/plain"),curdate);
        MultipartBody.Part partPhoto = MultipartBody.Part.createFormData("photo", file.getName(), rbFile);

        Retrofit retrofit = RetrofitInstance.getRetrofit(sharedPrefHelper.getInterceptor());
        ReportInterface reportInterface = retrofit.create(ReportInterface.class);
        Call<Void> call = reportInterface.sendReport(rbLat, rbLng, partPhoto, target,rbCurtime);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Laporan Terunggah", Toast.LENGTH_SHORT).show();
                    hasLatLng = false;
                    hasImage = false;
                    ivImage.setImageDrawable(getDrawable(R.drawable.ic_image_black_24dp));
                } else {
                    Toast.makeText(MainActivity.this, response.code()+" "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_history:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Anda Yakin ingin keluar ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        Retrofit retrofit = RetrofitInstance.getRetrofit(sharedPrefHelper.getInterceptor());
        Authentication authentication = retrofit.create(Authentication.class);
        Call<Void> call = authentication.destroyFirebaseToken();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    new LogoutTask().execute();
                    sharedPrefHelper.logout();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    ActivityCompat.finishAffinity(MainActivity.this);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
