package com.example.application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    EditText logname, logpass;
    Button login, logreg;
    String lognm, logpas;
    String url=IPsetting.ip+"login.php";
    ProgressDialog pDialog;
    InitializeSharedPreferences ispObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logname=(EditText) findViewById(R.id.editLogName);
        logpass= (EditText) findViewById(R.id.editLogPass);
        login= (Button) findViewById(R.id.btnLogin);
        logreg= (Button) findViewById(R.id.btnLogRegister);
        //isp = new InitializeSharedPreferences(MainActivity.this);
        ispObj = new InitializeSharedPreferences(this);

        lognm = ispObj.sharedPreferences.getString("username","0");
        logpas = ispObj.sharedPreferences.getString("password","0");
        if(!(lognm.equals("0")) && !(logpas.equals("0"))){
            login();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lognm= logname.getText().toString().trim();
                logpas= logpass.getText().toString().trim();
                login();
            }
        });
        logreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2 =new Intent(MainActivity.this, Register.class);
                startActivity(in2);
            }
        });
    }

    public void login()
    {
        RequestParams params= new RequestParams();
        params.put("name", lognm);
        params.put("password", logpas);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Verifing Details..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient client= new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response =new String(responseBody);
                System.out.print(response);
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    if(obj.getString("success").equals("200"))
                    {
                        IPsetting.uid=obj.getString("uid");
                        IPsetting.uname=obj.getString("name");
                        IPsetting.upassword=obj.getString("password");
                        IPsetting.umobile=obj.getString("mobile");
                        IPsetting.uemail=obj.getString("email");
                        IPsetting.uaddress=obj.getString("address");
                        Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                        ispObj.editor.putString("username",lognm);
                        ispObj.editor.putString("password",logpas);
                        ispObj.editor.commit();
                        Intent inte=new Intent(MainActivity.this,UserHome.class);
                        startActivity(inte);
                        finish();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Exception: "+e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Connection Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }
}