package com.example.application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VisitingLocationList extends AppCompatActivity {

    ListView lvallnearestlocation;
    Button btnaddselectedlocation;
    ArrayList arrayList;
    String loadnearestlocationURL=IPsetting.ip+"loadnearestlocations.php";
    SharedPreferences sharedpref;
    static JSONArray json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiting_location_list);

        sharedpref = getSharedPreferences("addnewtrip", Context.MODE_PRIVATE);

        btnaddselectedlocation=(Button) findViewById(R.id.btnaddplace2list);
        lvallnearestlocation=(ListView)findViewById(R.id.lvvisitinglocationlist);

        loadnearestlocations();

        btnaddselectedlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray choices = lvallnearestlocation.getCheckedItemPositions();
                json = new JSONArray();
                for (int i = 0; i < choices.size(); i++)
                {
                    if(choices.valueAt(i) == true) {
                        int key=choices.keyAt(i);
                        JSONObject jsonobj=new JSONObject();
                        try{
                            jsonobj.put("iteminfo",arrayList.get(key));
                            json.put(jsonobj);
                        }catch (Exception ex){
                            Toast.makeText(VisitingLocationList.this,"JsonException: "+ex,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Intent inte=new Intent(VisitingLocationList.this,StartTripMap.class);
                startActivity(inte);
            }
        });
    }

    public void loadnearestlocations(){
        RequestParams params = new RequestParams();
        params.put("clat",sharedpref.getString("destlat","18.0"));
        params.put("clon",sharedpref.getString("destlon","18.0"));
        params.put("rating",sharedpref.getString("hotelrating","defaultval"));

        final ProgressDialog pDialog = ProgressDialog.show(VisitingLocationList.this, "Loading...", "Please Wait", true, true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(loadnearestlocationURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response = new String(responseBody);
                try {
                    JSONObject object=new JSONObject(response);
                    System.out.println(object);
                    JSONArray array=object.getJSONArray("result");
                    arrayList=new ArrayList();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        String det1 = object1.getString("Name");
                        String det2 = object1.getString("Type");
                        String det3 = object1.getString("plat");
                        String det4 = object1.getString("plon");

                        //arrayList.add(det1);
                         arrayList.add("Name: "+det1 + " \t Type: " + det2 + " \t Latitude: " +det3 + " \t Longitude: " +det4);
                    }
                    lvallnearestlocation.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(VisitingLocationList.this, android.R.layout.simple_list_item_multiple_choice, arrayList);

                    lvallnearestlocation.setAdapter(arrayAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VisitingLocationList.this, "Exception: " + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(VisitingLocationList.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
