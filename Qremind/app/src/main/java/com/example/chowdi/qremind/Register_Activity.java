package com.example.chowdi.qremind;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chowdi on 2/3/2016.
 */
public class Register_Activity extends AppCompatActivity{

    // Variable for Firebase
    private Firebase fbRef;

    // Variable for all relevant UI elements
    private EditText fNameET;
    private EditText lNameET;
    private EditText emailET;
    private EditText phoneNoET;
    private EditText pwdET;
    private EditText cPwdET;
    private RadioGroup roleRG;
    private Button registerBtn;

    // Others variables
    private boolean anyETErrors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first
        initialiseUIElements();

        // Set listener to register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentFocus().clearFocus(); // Clear all focuses in the view
                Boolean editTextErr = isEmptyField(fNameET) | isEmptyField(lNameET) | isEmptyField(emailET) |
                        isEmptyField(phoneNoET) | isEmptyField(pwdET) | isEmptyField(cPwdET);

                // To hide the soft keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if(editTextErr) return;
                if(anyETErrors) return;

                setEnableAllElements(false);
                fbRef = new Firebase(getFBLinkForSelectedRole());
                registerUser();
            }
        });
        // Set on focus changed listener to password EditText
        pwdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pwd = pwdET.getText().toString();
                    String cPwd = cPwdET.getText().toString();
                    if(!isEmptyField(pwdET))
                    {
                        if(!pwd.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"))
                        {
                            pwdET.setError("Must contains minimum 8 characters at least 1 uppercase, 1 lowercase and 1 Number.");
                            anyETErrors = true;
                        }
                        return;
                    }
                    else
                    {
                        pwdET.setError(null);
                    }
                    if (pwd.equals(cPwd)) {
                        anyETErrors = false;
                        cPwdET.setError(null);
                    }
                }
            }
        });
        // Set on focus changed listener to confirmed password EditText
        cPwdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String pwd = pwdET.getText().toString();
                    String cPwd = cPwdET.getText().toString();
                    if(!pwd.equals(cPwd))
                    {
                        cPwdET.setError("Confirm password must match with Password!");
                        anyETErrors = true;
                    }
                    else
                    {
                        anyETErrors = false;
                        cPwdET.setError(null);
                    }
                }
            }
        });
        // Set on focus changed listener to email EditText
        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(isEmptyField(emailET))
                    {
                        emailET.setError(null);
                        return;
                    }
                    String email = emailET.getText().toString();
                    if(!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
                    {
                        emailET.setError("Please provide valid email!");
                        anyETErrors = true;
                    }
                    else
                    {
                        emailET.setError(null);
                        anyETErrors = false;
                    }
                }
            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        fNameET = (EditText) findViewById(R.id.FirstName_editText);
        lNameET = (EditText) findViewById(R.id.LastName_editText);
        emailET = (EditText) findViewById(R.id.Email_editText);
        phoneNoET = (EditText) findViewById(R.id.Phone_editText);
        pwdET = (EditText) findViewById(R.id.Password_editText);
        cPwdET = (EditText) findViewById(R.id.ConfirmPassword_editText);
        roleRG = (RadioGroup) findViewById(R.id.Role_RadioGroup);
        registerBtn = (Button) findViewById(R.id.Register_button);
    }

    /**
     * Check if str is empty
     * @return true if empty, else false for non-empty
     */
    private Boolean isEmptyField(EditText editText)
    {
        if(TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("This field is mandatory!");
            return true;
        }
        editText.setError(null);
        return false;
    }

    /**
     * Get selected role from radio group
     * @return selected role
     */
    private String getFBLinkForSelectedRole()
    {
        switch (roleRG.getCheckedRadioButtonId())
        {
            case R.id.customer_radiobtn:
                return getString(R.string.fb_appuseracc_cust);
            case R.id.vendor_radiobtn:
                return getString(R.string.fb_appuseracc_vendor);
            default:
                return "";
        }
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        fNameET.setEnabled(value);
        lNameET.setEnabled(value);
        emailET.setEnabled(value);
        phoneNoET.setEnabled(value);
        pwdET.setEnabled(value);
        cPwdET.setEnabled(value);
        roleRG.setEnabled(value);
        registerBtn.setEnabled(value);
    }

    /**
     * Create a new user account in Firebase
     */
    private void registerUser()
    {
        final String fname = fNameET.getText().toString();
        final String lname = lNameET.getText().toString();
        final String email = emailET.getText().toString();
        final String phoneNo = phoneNoET.getText().toString();
        final String pwd = pwdET.getText().toString();

        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(phoneNo)) {
                    fbRef.createUser(email, pwd, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("provider", "password");
                            map.put("firstname", fname);
                            map.put("lastname", lname);
                            map.put("phoneno", phoneNo);
                            map.put("email", email);
                            map.put("uid", result.get("uid").toString());

                            fbRef.child(phoneNoET.getText().toString()).setValue(map);
                            showToastMessage("Registration Successful");
                            Register_Activity.this.finish();
                            //System.out.println("Successfully created user account with uid: " + result.get("uid"));
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // there was an error
                            switch (firebaseError.getCode()) {
                                case FirebaseError.EMAIL_TAKEN: {
                                    showToastMessage("Email is already taken");
                                    emailET.setError("Email is already taken.");
                                    break;
                                }
                            }
                        }
                    });
                }
                else
                {
                    showToastMessage("Phone already exists");
                    phoneNoET.setError("Phone already exists");
                }
                // Enable all the UI elements after this process done
                setEnableAllElements(true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Show any messages on Toast
     * @param message - message string
     */
    private void showToastMessage(String message)
    {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
