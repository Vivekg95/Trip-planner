package com.example.application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Verification extends AppCompatActivity {
    EditText votp,vskey;
    String vsotp,vsskey;
    Button btnvotp,btnvskey;
    ProgressDialog pDialog;
    int emailstatus=0;
    String url= IPsetting.ip+"register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        votp= (EditText) findViewById(R.id.editVerificationOTP);
        btnvotp=(Button) findViewById(R.id.btnVerifyOTP);
        vskey= (EditText) findViewById(R.id.editVerificationSKey);
        btnvskey=(Button) findViewById(R.id.btnVerifySKey);

        btnvskey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSKEY();
                //System.out.println(vsotp+"   "+Register.otp);
                if(vsskey.equals("")){
                    Toast.makeText(Verification.this,"Please Enter Secret key", Toast.LENGTH_LONG).show();
                }
                else{
                    if(vsskey.equals(Register.skey)){
                        Toast.makeText(Verification.this,"Email id Verify Successfully", Toast.LENGTH_LONG).show();
                        emailstatus=1;
                    }
                    else{
                        Toast.makeText(Verification.this,"Incorrect OTP", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnvotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
                System.out.println(vsotp+"   "+Register.otp);
                if(vsotp.equals("")){
                    Toast.makeText(Verification.this,"Please Enter OTP", Toast.LENGTH_LONG).show();
                }
                else{
                    if(emailstatus==1) {
                        if (vsotp.equals(Register.otp)) {
                            Toast.makeText(Verification.this, "Mobile Number Verify Successfully", Toast.LENGTH_LONG).show();
                            registr();
                        } else {
                            Toast.makeText(Verification.this, "Incorrect OTP", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(Verification.this, "Please Verify Email Id", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void registr(){
        RequestParams params= new RequestParams();
        params.put("name", IPsetting.uname);
        params.put("password",IPsetting.upassword);
        params.put("email",IPsetting.uemail);
        params.put("mobile", IPsetting.umobile);
        params.put("address", IPsetting.uaddress);
        params.put("dob", Register.rdob);
        pDialog = new ProgressDialog(Verification.this);
        pDialog.setMessage("Verifing Details..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
        AsyncHttpClient client=new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response= new String(responseBody);
                System.out.print(response);
                Toast.makeText(Verification.this,response, Toast.LENGTH_LONG).show();
                Intent in4 = new Intent(Verification.this, MainActivity.class);
                startActivity(in4);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(Verification.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getOTP(){
        vsotp=votp.getText().toString().trim();
    }
    public void getSKEY(){
        vsskey=vskey.getText().toString().trim();
    }
}
