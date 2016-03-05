package com.example.chowdi.qremind;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

/**
 * Created by chowdi on 2/3/2016.
 */
public class CustomerProfilePageActivity extends AppCompatActivity{

    // Firebase variables
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerprofilepage);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first
        initialiseUIElements();
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
//        emailPhoneNoET = (EditText) findViewById(R.id.email_login_ET);
//        passwordET = (EditText) findViewById(R.id.password_login_ET);
//        custLoginBtn = (Button) findViewById(R.id.cust_login_btn);
//        vendorLoginBtn = (Button) findViewById(R.id.vendor_login_btn);
    }
}
