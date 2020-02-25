package com.example.powerautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class Login extends AppCompatActivity {
    private static final int SEND_SMS_PERMISSION_CODE = 1;
    EditText user, pass;
    Button login;
    String loginTag = "userLogin";
    RequestQueue queue;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.btnLogin);

        if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Login.this,
                    new String[] {Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_CODE);
        }

        LoginButton();

    }

    private boolean checkInput(EditText user, EditText pass) {
        if(user.getText().toString().isEmpty()){
            user.setError("Please enter username");
            user.requestFocus();
            return false;
        } else if (pass.getText().toString().isEmpty()){
            pass.setError("Please enter password");
            pass.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    public void LoginButton() {
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(checkInput(user, pass)){
                            String URL = MyURLs.userLoginURL(user.getText().toString().toLowerCase(), pass.getText().toString());
                            Log.i(loginTag, URL);


                            queue = Volley.newRequestQueue(Login.this);
                            StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("1")){
                                        loginSuccess();
                                    } else {
                                        Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                    }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("error",error.toString());
                                }
                            });
                            queue.add(request);
                        } else {


                        }


                    }
                }
        );
    }

    private void loginSuccess() {
        Context context;
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("username", user.getText().toString().toLowerCase());
        editor.putString("password", pass.getText().toString());
        editor.commit();

        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == SEND_SMS_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
