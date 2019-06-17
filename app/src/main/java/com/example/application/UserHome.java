package com.example.application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserHome extends AppCompatActivity {

    Button btnlogout,btnuserinfo,btnaddnewtrip,btnviewmap,btnviewselectedlocation;
    InitializeSharedPreferences ispObj;
    int count =0;
    BootReceiver br=new BootReceiver();
    TimeReceiver tm=new TimeReceiver();
    KesriTripDB ktdb;
    ProgressDialog pDialog;
    String getusertripdetailsURL=IPsetting.ip+"getusertripdetails.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        btnlogout=(Button)findViewById(R.id.btnLogout);
        btnuserinfo=(Button)findViewById(R.id.btnViewUserInfo);
        btnaddnewtrip=(Button)findViewById(R.id.btnAddNewTripDetails);
        btnviewselectedlocation=(Button)findViewById(R.id.btnViewSelectedLocation);
        btnviewmap=(Button)findViewById(R.id.btnViewMap);
        ispObj=new InitializeSharedPreferences(this);

        ktdb=new KesriTripDB(this,"usertripdetails",null,1);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.application.RestartSensor");
        getBaseContext().registerReceiver(br, intentFilter);

        pDialog=new ProgressDialog(UserHome.this);
        pDialog.setMessage("Processing...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

        getusertripdetails();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        getBaseContext().registerReceiver(tm,filter);

        Intent myService=new Intent(UserHome.this,ReminderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(myService);
        } else {
            startService(myService);
        }

        btnviewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHome.this,NearestPlacesMap.class);
                startActivity(intent);
            }
        });

        btnviewselectedlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHome.this,SelectedLocationList.class);
                startActivity(intent);
            }
        });

        btnaddnewtrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHome.this,AddTripDetails.class);
                startActivity(intent);
            }
        });
        btnuserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHome.this,UserDetails.class);
                startActivity(intent);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ispObj.editor.clear();
                ispObj.editor.commit();
                Intent intent=new Intent(UserHome.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        count ++;
        if(count==1) {
            final CountDownTimer t = new CountDownTimer(1000,100){
                @Override
                public void onTick(long millisUntilFinished) {
                    if(count>1){
                        onFinish();
                    }
                }
                @Override
                public void onFinish() {
                    if(count>1) {
                        finish();
                    }else{
                        count=0;
                        Toast.makeText(UserHome.this, "Continuously press back button two times to close application!", Toast.LENGTH_SHORT).show();
                    }
                }
            }.start();
        }
    }

    public void getusertripdetails(){
        RequestParams params = new RequestParams();
        params.put("uid",IPsetting.uid);

        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getusertripdetailsURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                try{
                    JSONObject object = new JSONObject(res);
                    if(object.getString("success").equals("200")){
                        JSONArray locrem = object.getJSONArray("locationrem");
                        for(int i=0;i<locrem.length();i++){
                            JSONObject obj = locrem.getJSONObject(i);
                            ktdb.storetripdetails(obj.getString("tid"),obj.getString("srclocname"),obj.getString("srclat"),obj.getString("srclon"),obj.getString("haltlocname"),obj.getString("haltlat"), obj.getString("haltlon"),obj.getString("destlocname"),obj.getString("destlat"),obj.getString("destlon"),obj.getString("startdate"),obj.getString("breakfasttime"),obj.getString("lunchtime"),obj.getString("dinnertime"),obj.getString("hotelrating"));
                        }
                    }
                    else {
                        Toast toast = Toast.makeText(UserHome.this, "No data found in database for trip", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }catch(Exception e){
                    Toast toast = Toast.makeText(UserHome.this, res, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast toast = Toast.makeText(UserHome.this, "Connectivity failed!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }
}