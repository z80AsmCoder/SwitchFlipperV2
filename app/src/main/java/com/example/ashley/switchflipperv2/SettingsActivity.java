package com.example.ashley.switchflipperv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private EditText edt_ip, edt_username, edt_password;
    private Button btn_save;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("switchflipperpref", Context.MODE_PRIVATE);

        edt_ip = findViewById(R.id.edt_ip);
        edt_ip.setText(sharedPreferences.getString("ip",""));

        edt_username = findViewById(R.id.edt_username);
        edt_username.setText(sharedPreferences.getString("username",""));

        edt_password = findViewById(R.id.edt_password);
        edt_password.setText(sharedPreferences.getString("password",""));

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ip", edt_ip.getText().toString().trim());
                editor.putString("username", edt_username.getText().toString());
                editor.putString("password", edt_password.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
