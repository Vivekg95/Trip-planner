package com.example.application;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.mail.MessagingException;

public class Register extends AppCompatActivity {
    EditText regname, regpass, regemail, regmobno, regadd;
    static String rdob;
    static String otp,skey;
    Button btnreg, btnclear;
    TextView txtdob;
    SmsManager smsManager;
    DatePickerDialog datepicker;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regname= (EditText) findViewById(R.id.editRegName);
        regpass= (EditText) findViewById(R.id.editRegPass);
        regemail= (EditText) findViewById(R.id.editRegEmail);
        regmobno= (EditText) findViewById(R.id.editRegMobileNo);
        regadd= (EditText) findViewById(R.id.editRegAddress);
        btnreg= (Button) findViewById(R.id.btnRegRegister);
        btnclear= (Button) findViewById(R.id.btnRegClear);
        txtdob=(TextView)findViewById(R.id.txtRegDob);
        smsManager= SmsManager.getDefault();

        final DatePickerDialog.OnDateSetListener dat = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        txtdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datepicker = new DatePickerDialog(Register.this, dat, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datepicker.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datepicker.show();
            }
        });

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clr();
            }
        });

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registr();
            }
        });
    }

    public void clr(){
        regname.setText("");
        regpass.setText("");
        regemail.setText("");
        regmobno.setText("");
        regadd.setText("");
    }

    public void registr(){
        IPsetting.uname=regname.getText().toString().trim();
        IPsetting.upassword=regpass.getText().toString().trim();
        IPsetting.umobile=regmobno.getText().toString().trim();
        IPsetting.uemail=(regemail.getText().toString().trim());
        IPsetting.uaddress=(regadd.getText().toString().trim());
        skeygeneration();
        otpgeneration();

    }

    public void otpgeneration(){
        int randomPIN = (int)(Math.random()*9000)+1000;
        otp = ""+randomPIN;
        sendotp();
    }

    public void sendotp(){
        smsManager.sendTextMessage(IPsetting.umobile, null, otp, null, null);
        Intent in3=new Intent(Register.this,Verification.class);
        startActivity(in3);
    }

    public void skeygeneration(){
        int randomPIN = (int)(Math.random()*9000)+1000;
        skey = ""+randomPIN;
        sendEmail();
    }

    public void sendEmail(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "Thank you for registering with Trip Planning App!\n\rYour verification pin is "+skey+"\n\rEnter this secret key into mobile app to complete your verification.";
                String[] files= {};
                SendMailSSL sm = new SendMailSSL();
                try {
                    sm.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", IPsetting.uemail, "Registration authentication for Trip Planning Application!",message,files);
                    //cd.start();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtdob.setPaintFlags(txtdob.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        txtdob.setText("Date of Birth: "+sdf.format(myCalendar.getTime()));
        rdob = sdf.format(myCalendar.getTime());
    }
}