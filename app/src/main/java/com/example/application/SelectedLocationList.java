package com.example.application;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class SelectedLocationList extends AppCompatActivity {

    TextView tvdate,tvnotfound;
    ListView lvlocation;
    String getselectedlocationlistURL=IPsetting.ip+"getselectedlocationlist.php";
    DatePickerDialog datepicker;
    String selecteddate;
    Calendar myCalendar = Calendar.getInstance();
    ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_location_list);
        tvdate=(TextView)findViewById(R.id.txtSelectDate2ViewList);
        tvnotfound=(TextView)findViewById(R.id.txtdatanotfoundheading1);
        lvlocation=(ListView)findViewById(R.id.lvselectedlocation);

        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker = new DatePickerDialog(SelectedLocationList.this, dat, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //datepicker.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                //datepicker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datepicker.show();
            }
        });
    }
    final DatePickerDialog.OnDateSetListener dat = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvdate.setPaintFlags(tvdate.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvdate.setText("Selected Trip Date: "+sdf.format(myCalendar.getTime()));
        selecteddate = sdf.format(myCalendar.getTime());
        fetchdata();
    }

    public void fetchdata(){
        RequestParams params = new RequestParams();
        params.put("userid",IPsetting.uid);
        params.put("selecteddate",selecteddate);

        final ProgressDialog pDialog = ProgressDialog.show(SelectedLocationList.this, "Loading...", "Please Wait", true, true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getselectedlocationlistURL, params, new AsyncHttpResponseHandler() {
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
                        String det1 = object1.getString("slocname");
                        String det2 = object1.getString("sloclat");
                        String det3 = object1.getString("sloclon");

                        //arrayList.add(det1);
                        arrayList.add("Location Name: "+det1 + " \t Latitude: " + det2 + " \t Longitude: " +det3);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(SelectedLocationList.this, android.R.layout.simple_selectable_list_item, arrayList);

                    lvlocation.setAdapter(arrayAdapter);
                    if(array.length()>0){
                    JSONObject obj2= array.getJSONObject(0);
                    String dname=obj2.getString("destname");
                    String dlat=obj2.getString("destlat");
                    String dlon=obj2.getString("destlon");
                    tvnotfound.setText("Destination Name: "+dname+" \t Latitude: "+dlat+" \t Longitude: "+dlon);
                    tvnotfound.setVisibility(View.VISIBLE);
                    }else {
                        tvnotfound.setText("Data not found");
                        tvnotfound.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SelectedLocationList.this, "Exception: " + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(SelectedLocationList.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}