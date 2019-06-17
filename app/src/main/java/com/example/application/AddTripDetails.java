package com.example.application;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTripDetails extends AppCompatActivity{
    EditText etSrcLoc,etDestLoc,etHaltLoc;
    Button btndrawroute;
    Spinner spnStarRating;
    String[] ratingarr = {"Select Hotel Rating", "1", "2", "3", "4", "5", "6", "7"};
    RadioGroup rgrouproutetype;
    TextView tvstartdate,tvBreakfastTime,tvDinnerTime,tvLunchTime;
    String strsrcloc,strdestloc,strhaltloc,strstartdate,strbreakfasttime,strlunchtime,strdinnertime,strhotelrating;
    static final int DIALOG_ID=0;
    int t, hour_x, minute_x;
    String srclat,srclon,haltlat,haltlon,destlat,destlon;
    String createtour=IPsetting.ip+"createtour.php";
    ProgressDialog pDialog;
    private LatLng srcPosition,destPosition,haltPosition;
    private LatLng wayPointPosition[]=null;
    int latloncount;
    int ii=0,whichdateclick=0;
    DatePickerDialog datepicker;
    Calendar myCalendar = Calendar.getInstance();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip_details);

        btndrawroute=(Button) findViewById(R.id.btnATViewRoute);
        rgrouproutetype=(RadioGroup) findViewById(R.id.rgroutetype);
        etSrcLoc=(EditText) findViewById(R.id.edtATSourceLoc);
        etDestLoc=(EditText) findViewById(R.id.edtATDestinationLoc);
        etHaltLoc=(EditText) findViewById(R.id.edtATHaltLoc);
        tvstartdate=(TextView) findViewById(R.id.txtCreateTourStartDate);
        tvBreakfastTime=(TextView) findViewById(R.id.txtCreateTourBreakfastTime);
        tvDinnerTime=(TextView) findViewById(R.id.txtCreateTourDinnerTime);
        tvLunchTime=(TextView) findViewById(R.id.txtCreateTourLunchTime);
        spnStarRating=(Spinner)findViewById(R.id.sprHotelRating);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ratingarr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStarRating.setAdapter(aa);

        spnStarRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strhotelrating=ratingarr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btndrawroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strsrcloc=etSrcLoc.getText().toString().trim();
                strhaltloc=etHaltLoc.getText().toString().trim();
                strdestloc=etDestLoc.getText().toString().trim();
                if(strsrcloc.isEmpty()||strhaltloc.isEmpty()||strdestloc.isEmpty()||strstartdate.isEmpty()||strbreakfasttime.isEmpty()||strlunchtime.isEmpty()||strdinnertime.isEmpty()||strhotelrating.equals("Select Hotel Rating")){
                    Toast.makeText(AddTripDetails.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List addressList1 = geocoder.getFromLocationName(strsrcloc, 1);
                        if (addressList1 != null && addressList1.size() > 0) {
                            Address address = (Address) addressList1.get(0);
                            srcPosition=new LatLng(address.getLatitude(),address.getLongitude());
                        }
                        List addressList2 = geocoder.getFromLocationName(strdestloc, 1);
                        if (addressList2 != null && addressList2.size() > 0) {
                            Address address = (Address) addressList2.get(0);
                            destPosition=new LatLng(address.getLatitude(),address.getLongitude());
                        }
                        List addressList3 = geocoder.getFromLocationName(strhaltloc, 1);
                        if (addressList3 != null && addressList3.size() > 0) {
                            Address address = (Address) addressList3.get(0);
                            haltPosition=new LatLng(address.getLatitude(),address.getLongitude());
                        }
                    }catch (Exception ex){
                        Toast.makeText(getApplicationContext(),"cant get address",Toast.LENGTH_SHORT).show();
                    }
                    if(srcPosition==null||destPosition==null||haltPosition==null){
                        Toast.makeText(getApplicationContext(),"null location coordinates",Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences sharedPref = getSharedPreferences("addnewtrip", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("srclocname", strsrcloc);
                        editor.putString("srclat", Double.toString(srcPosition.latitude));
                        editor.putString("srclon", Double.toString(srcPosition.longitude));
                        editor.putString("haltlocname", strhaltloc);
                        editor.putString("haltlat", Double.toString(haltPosition.latitude));
                        editor.putString("haltlon", Double.toString(haltPosition.longitude));
                        editor.putString("destlocname", strdestloc);
                        editor.putString("destlat", Double.toString(destPosition.latitude));
                        editor.putString("destlon", Double.toString(destPosition.longitude));
                        editor.putString("startdate",strstartdate);
                        editor.putString("breakfasttime", strbreakfasttime);
                        editor.putString("lunchtime", strlunchtime);
                        editor.putString("dinnertime", strdinnertime);
                        editor.putString("hotelrating", strhotelrating);
                        editor.commit();

                        Intent inte=new Intent(AddTripDetails.this,VisitingLocationList.class);
                        startActivity(inte);
                    }
                }
            }
        });
        tvstartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker = new DatePickerDialog(AddTripDetails.this, dat, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datepicker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datepicker.show();
            }
        });

        tvBreakfastTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t=1;
                showDialog(DIALOG_ID);
            }
        });

        tvLunchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t=2;
                showDialog(DIALOG_ID);
            }
        });

        tvDinnerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t=3;
                showDialog(DIALOG_ID);
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
        tvstartdate.setPaintFlags(tvstartdate.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvstartdate.setText("Trip Starting Date: "+sdf.format(myCalendar.getTime()));
        strstartdate = sdf.format(myCalendar.getTime());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==DIALOG_ID)
            return new TimePickerDialog(AddTripDetails.this,kTimePickerListener,hour_x,minute_x,false);
        else
            return null;
    }

    protected TimePickerDialog.OnTimeSetListener kTimePickerListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour1, int minute1) {
            hour_x=hour1;
            minute_x=minute1;
            LetsSetTime();
        }
    };

    public void LetsSetTime(){
        if(t==1){
            strbreakfasttime=hour_x+":"+minute_x;
            String str="Breakfast Time is "+hour_x+":"+minute_x;
            tvBreakfastTime.setText(str);
        }
        if(t==2){
            strlunchtime=hour_x+":"+minute_x;
            String str="Lunch Time is "+hour_x+":"+minute_x;
            tvLunchTime.setText(str);
        }
        if(t==3){
            strdinnertime=hour_x+":"+minute_x;
            String str="Dinner Time is "+hour_x+":"+minute_x;
            tvDinnerTime.setText(str);
        }
    }
}