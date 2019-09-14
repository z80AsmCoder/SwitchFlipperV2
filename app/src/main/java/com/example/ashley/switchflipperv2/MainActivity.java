package com.example.ashley.switchflipperv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Switch sw1,sw2,sw3,sw4,sw5;
    private Button btn1,btn2;

    private ImageView[] bulbs;

    RequestQueue queue;
    SharedPreferences sharedPreferences;

    String[] stateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("switchflipperpref", Context.MODE_PRIVATE);

        bulbs = new ImageView[]{
                findViewById(R.id.bulb1),
                findViewById(R.id.bulb2),
                findViewById(R.id.bulb3),
                findViewById(R.id.bulb4),
                findViewById(R.id.bulb5)
        };

        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);
        sw4 = findViewById(R.id.sw4);
        sw5 = findViewById(R.id.sw5);

        setSwitchListeners();
        checkState(sharedPreferences.getString("ip",""));

        btn1 = findViewById(R.id.btn_check);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkState(sharedPreferences.getString("ip",""));
            }
        });

        btn2 = findViewById(R.id.btn_settings);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });

        btn2.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                Toast.makeText(getApplicationContext(), "Version: 0.2.5 -Fixing Clear Text HTTP Error -09.09.19", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void callApi(String ip,String pin, String state){
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(getApplicationContext());
        String url = "";
        if(ip == "") {
            url = "https://api.thingspeak.com/update?api_key=QNZR8LIFIYM1EJM8&field1=" + state;
        }
        else{
            url = "http://" + ip + "/arduino/digital/" + pin +"/" + state;
        }
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        Toast.makeText(getApplicationContext(),"Good!", Toast.LENGTH_SHORT).show();
                        setStateSwitch(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String strerror = error.toString().trim();
                if(strerror.equals("com.android.volley.AuthFailureError")){
                    Toast.makeText(getApplicationContext(),"Wrong Credentials", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String username = sharedPreferences.getString("username","");
                String password = sharedPreferences.getString("password","");
                String login = username + ":" + password;
                //String b64 = java.util.Base64.getEncoder().encodeToString(login.getBytes()); //API Level too Low
                byte[] data;
                String base64 = "";
                try {
                    data = login.getBytes("UTF-8");
                    base64 = Base64.encodeToString(data, Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //params.put("Authorization", "Basic cm9vdDphcmR1aW5v");
                params.put("Authorization", "Basic " + base64);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void checkState(String ip){
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(getApplicationContext());
        String url = "";
        if(ip == "") {
            url = "https://api.thingspeak.com/update?api_key=QNZR8LIFIYM1EJM8&field1=";
        }
        else{
            url = "http://" + ip + "/arduino/state/1"; //the number 1 is used only for the arduino
        }
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));

                        //line below can be used to output the response
                        //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_SHORT).show();

                        setStateSwitch(response);
                        Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();

                    }//end onResponse
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String strerror = error.toString().trim();
                if(strerror.equals("com.android.volley.AuthFailureError")){
                    Toast.makeText(getApplicationContext(),"Wrong Credentials", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String username = sharedPreferences.getString("username","");
                String password = sharedPreferences.getString("password","");
                String login = username + ":" + password;
                //String b64 = java.util.Base64.getEncoder().encodeToString(login.getBytes()); //API Level too Low
                byte[] data;
                String base64 = "";
                try {
                    data = login.getBytes("UTF-8");
                    base64 = Base64.encodeToString(data, Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //params.put("Authorization", "Basic cm9vdDphcmR1aW5v");
                params.put("Authorization", "Basic " + base64);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void setStateSwitch(String response){

        try {
            JSONObject json = new JSONObject(response);
            stateSwitch = new String[]{
                    json.getString("D12"),
                    json.getString("D11"),
                    json.getString("D10"),
                    json.getString("D9"),
                    json.getString("D8")
            };

            for (int i = 0; i < stateSwitch.length; i++) {
                if(stateSwitch[i].equals("1")){
                    bulbs[i].setImageDrawable(getResources().getDrawable(R.drawable.bulbon));
                }else{
                    if(stateSwitch[i].equals("0")){
                        bulbs[i].setImageDrawable(getResources().getDrawable(R.drawable.bulboff));
                    }
                }

            }//end for



            //Remove listener on switches and flip them according to data receive
            sw1.setOnCheckedChangeListener(null);
            sw2.setOnCheckedChangeListener(null);
            sw3.setOnCheckedChangeListener(null);
            sw4.setOnCheckedChangeListener(null);
            sw5.setOnCheckedChangeListener(null);

            if(stateSwitch[0].equals("1")){
                sw1.setChecked(true);
            }else{
                sw1.setChecked(false);
            }

            if(stateSwitch[1].equals("1")){
                sw2.setChecked(true);
            }else{
                sw2.setChecked(false);
            }

            if(stateSwitch[2].equals("1")){
                sw3.setChecked(true);
            }else{
                sw3.setChecked(false);
            }

            if(stateSwitch[3].equals("1")){
                sw4.setChecked(true);
            }else{
                sw4.setChecked(false);
            }

            if(stateSwitch[4].equals("1")){
                sw5.setChecked(true);
            }else{
                sw5.setChecked(false);
            }

            //Assign listeners back on switches
            setSwitchListeners();

            //Toast.makeText(getApplicationContext(),"Done!", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }//end catch
    }

    public void setSwitchListeners(){
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw1.isChecked()){
                    //callApi(edt_ip.getText().toString(), "1"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"12","1");
                }else{
                    //callApi(edt_ip.getText().toString(), "0"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"12","0");
                }
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw2.isChecked()){
                    //callApi(edt_ip.getText().toString(), "1"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"11","1");
                }else{
                    //callApi(edt_ip.getText().toString(), "0"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"11","0");
                }
            }
        });

        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw3.isChecked()){
                    //callApi(edt_ip.getText().toString(), "1"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"10","1");
                }else{
                    //callApi(edt_ip.getText().toString(), "0"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"10","0");
                }
            }
        });

        sw4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw4.isChecked()){
                    //callApi(edt_ip.getText().toString(), "1"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"9","1");
                }else{
                    //callApi(edt_ip.getText().toString(), "0"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"9","0");
                }
            }
        });

        sw5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw5.isChecked()){
                    //callApi(edt_ip.getText().toString(), "1"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"8","1");
                }else{
                    //callApi(edt_ip.getText().toString(), "0"); //For thingspeak
                    callApi(sharedPreferences.getString("ip",""),"8","0");
                }
            }
        });
    }//end function setSwitchListeners
}
