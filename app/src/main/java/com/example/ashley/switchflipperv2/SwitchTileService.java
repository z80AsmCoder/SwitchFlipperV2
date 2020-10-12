package com.example.ashley.switchflipperv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Base64;
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

public class SwitchTileService extends TileService {

    //SharedPreferences sharedPreferences = getSharedPreferences("switchflipperpref",Context.MODE_PRIVATE);
    RequestQueue queue;
    String ip = "192.168.100.128";
    String pin = "12";
    Tile tile = getQsTile();
    @Override
    public void onClick(){
//        Toast.makeText(getApplicationContext(),"Hello World - New",Toast.LENGTH_SHORT).show();
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + ip + "/arduino/flip/" + pin;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        Toast.makeText(getApplicationContext(),"Success!", Toast.LENGTH_SHORT).show();
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
                String username = "root";
                // String username = sharedPreferences.getString("username","");
                // String password = sharedPreferences.getString("password","");
                String password = "arduino1";
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

    @Override
    public void onStartListening(){
       // checkState(ip);
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
                        setState(response);
                        //setStateSwitch(response);
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
                String username = "root";
               // String username = sharedPreferences.getString("username","");
               // String password = sharedPreferences.getString("password","");
                String password = "arduino1";
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

                        setState(response);
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
                String username = "root";
                //String username = sharedPreferences.getString("username","");
                //String password = sharedPreferences.getString("password","");
                String password = "arduino1";
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

    public void setState(String response){

        try {
            JSONObject json = new JSONObject(response);
            if(json.getString("D"+pin) == "1"){
                tile.setState(2); //tile.STATE_ACTIVE
            }else{
                tile.setState(0);
            }
            getQsTile().updateTile();
            //Toast.makeText(getApplicationContext(),json.getString("D"+pin),Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }//end catch
    }

}
