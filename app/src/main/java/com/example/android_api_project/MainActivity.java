package com.example.android_api_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText name, email;
    Button submit;
    TextView response;
    ImageButton refresh;

    static String url = "http://10.0.2.2/android_api_project/sql_set_data.php";
    static String get_url = "http://10.0.2.2/android_api_project/sql_get_data.php";

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
        refresh = findViewById(R.id.refresh_button);
        response = findViewById(R.id.response);

        get_data();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_data();
            }
        });

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
        // Use JsonObjectRequest as the PHP script returns a JSON object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, get_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Check the status from the JSON response
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                // Get the 'data' JSONArray
                                JSONArray dataArray = response.getJSONArray("data");
                                StringBuilder formattedData = new StringBuilder();
                                // Loop through the array to get each user's data
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject userObject = dataArray.getJSONObject(i);
                                    // Assuming the table has 'name' and 'email' columns
                                    String name = userObject.getString("name");
                                    String email = userObject.getString("email");
                                    formattedData.append("Name: ").append(name).append(", Email: ").append(email).append("\n\n");
                                }
                                // Display the formatted data in the TextView
                                MainActivity.this.response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                MainActivity.this.response.setText(formattedData.toString());
                            } else {
                                // Handle failure status
                                String message = response.getString("message");
                                MainActivity.this.response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                                MainActivity.this.response.setText("Status: " + status + "\nMessage: " + message);
                            }
                        } catch (JSONException e) {
                            // Handle JSON parsing error
                            MainActivity.this.response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            MainActivity.this.response.setText("JSON parsing error: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle Volley errors
                        MainActivity.this.response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        MainActivity.this.response.setText("Error: " + error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    void set_data(final String name, final String email){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonResponse = new JSONObject(s);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if(status.equals("Success")) {
                                // Refresh data after successful submission
                                get_data();
                            } else {
                                response.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                                response.setText("Status: " + status + "\nMessage: " + message);
                            }
                        } catch (JSONException e) {
                            response.setText("Something went wrong on response : " + e.toString());
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", name);
                map.put("email", email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
}
