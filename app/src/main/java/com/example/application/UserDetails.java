package com.example.application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UserDetails extends AppCompatActivity {
TextView txtname,txtpassword, txtmobile, txtemail, txtaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        txtname=(TextView) findViewById(R.id.textName);
        txtpassword=(TextView) findViewById(R.id.textPassword);
        txtmobile=(TextView) findViewById(R.id.textMobile);
        txtemail=(TextView) findViewById(R.id.textEmail);
        txtaddress=(TextView) findViewById(R.id.textAddress);
        ready();
    }
    public void ready()
    {
        txtname.setText(IPsetting.uname);
        txtpassword.setText(IPsetting.upassword);
        txtmobile.setText(IPsetting.umobile);
        txtemail.setText(IPsetting.uemail);
        txtaddress.setText(IPsetting.uaddress);
    }
}
