package com.example.android_api_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText name, email;
    Button submit;
    TextView response;
    static String url = "http://localhost/android_api_project/sql_set_data.php";
    static String get_url = "http://192.168.6.48/android_api_project/sql_get_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
        response = findViewById(R.id.response);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty()){
                    response.setText("Please fill all the fields");
                    return;
                }
                set_data(name.getText().toString(), email.getText().toString());
            }
        });
    }

    void get_data(){
        StringRequest request = new StringRequest(Request.Method.GET, get_url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            }
        );
    }
    void set_data(final String name, final String email){
        StringRequest request = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    JSONObject jsonResponse = null;
                    String status = null;
                    String message = null;
                    try {
                        jsonResponse = new JSONObject(s);
                        status = jsonResponse.getString("status");
                        message = jsonResponse.getString("message");
                    } catch (JSONException e) {
                        response.setText("Something went wrong on response : " + e.toString());
                    }
                    if(status.equals("Success")) {
                        response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                        response.setText("Status: " + status + "\nMessage: " + message);
                    }
                    else {
                        response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        response.setText("Status: " + status + "\nMessage: " + message);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    response.setText("Error : " + volleyError.toString());
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("email", email);
                    return map;
                }
            };

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(request);
        } catch (Exception e) {
            response.setText("Something went wrong on request queue : " + e.toString());
        }
    }
}