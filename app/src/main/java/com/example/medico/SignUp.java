package com.example.medico;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    Button BtnSuSignUp;
    EditText FName, LName,ContactNo, Password, Email, CPassword;
    TextView TvLogin;
    boolean twice;
    final String TAG=getClass().getName();
    private FirebaseAuth mAuth;
    DatabaseReference databaseUser;
    List<String> numbers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        BtnSuSignUp = (Button) findViewById(R.id.BtnSuSignUp);
        FName = (EditText) findViewById(R.id.FName);
        LName = (EditText) findViewById(R.id.LName);
        CPassword = (EditText) findViewById(R.id.CPassword);
        ContactNo = (EditText) findViewById(R.id.ContactNo);
        Password = (EditText) findViewById(R.id.Password);
        Email = (EditText) findViewById(R.id.Email);
        TvLogin= (TextView) findViewById(R.id.TvLogin);
        mAuth = FirebaseAuth.getInstance();
        databaseUser = FirebaseDatabase.getInstance().getReference("user_data");

        BtnSuSignUp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (FName.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "FirstName Required..!!", Toast.LENGTH_SHORT).show();
                    FName.setError("FirstName Required");
                    return;
                } else if (LName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LastName Required..!!", Toast.LENGTH_SHORT).show();
                    LName.setError("LastName Required");
                    return;
                }
                else if (ContactNo.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Contact Required..!!", Toast.LENGTH_SHORT).show();
                    ContactNo.setError("Contact Required");

                    return;
                } else if (!isContactNoValid(ContactNo.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "ContactNo invalid..!!", Toast.LENGTH_SHORT).show();
                    ContactNo.setError("Contact Invalid");
                    return;
                } else if (Email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email Required..!!", Toast.LENGTH_SHORT).show();
                    Email.setError("Email Required");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString().trim()).matches()) {
                    Toast.makeText(getApplicationContext(), "Email Invaild..!!", Toast.LENGTH_SHORT).show();
                    Email.setError("Email Invalid");
                    return;
                } else if (Password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password Required..!!", Toast.LENGTH_SHORT).show();
                    Password.setError("Password Required");
                    return;
                }
                else if (CPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password Required..!!", Toast.LENGTH_SHORT).show();
                    Password.setError("Password Required");
                    return;
                }
                else if (!(Password.getText().toString()).equals(CPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_SHORT).show();
                    CPassword.setError("Password not match");
                    return;
                }
                else{
                    String email=Email.getText().toString().trim();
                    String password=Password.getText().toString().trim();
                    String fName= FName.getText().toString().trim();
                    String lName = LName.getText().toString().trim();
                    String mid = ContactNo.getText().toString().trim();
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user= mAuth.getCurrentUser();

                    insert_db(fName,lName,mid,email);
                    auth_user(mAuth,user,email,password);

                }


            }
        });
        TvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this,LogIn.class));
            }
        });


    }


public void auth_user(FirebaseAuth mauth,FirebaseUser user,String email,String password){
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(String.valueOf(SignUp.this), "createUserWithEmail:success");
                        Toast.makeText(SignUp.this, "Successfully Registered",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(SignUp.this,LogIn.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(String.valueOf(SignUp.this), "createUserWithEmail:failure");
                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }

                    // ...
                }
            });
}
public void insert_db(String fName,String lName,String mid,String email){

    ListIterator<String> scanNumbers = numbers.listIterator();
    int flag =0;
    while (scanNumbers.hasNext()){
        if(mid==scanNumbers.next()){
            flag=1;
        }
    }
    if(flag==1){
        String id = databaseUser.push().getKey();
        Users user_db = new Users(fName,lName,mid,email,id);
        databaseUser.child(id).setValue(user_db);
    }
    else{
        Toast.makeText(SignUp.this,"Mobile Number Already Exists !",Toast.LENGTH_LONG).show();
    }

}

    public static boolean isContactNoValid(String ConnNo)
    {
        String regExpn="\\d{10}";//regEx for contact no.

        CharSequence inputStr=ConnNo;//to convert string into character sequence.
        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher= pattern.matcher(inputStr);
        if(matcher.matches())

            return true;

        else
            return false;
    }
    public void CleanEditText()
    {
        FName.setText("");
        LName.setText("");
        ContactNo.setText("");
        Email.setText("");
        Password.setText("");
    }
    @Override
    protected void onStart(){

        SignUp.super.onStart();
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    System.out.println(ds);
//                    System.out.println(ds.getValue());

                    ds.getValue();
                    try {
                        Map<String,String> user_dictionary = dataSnapshot.getValue(Map.class);
                        String mobile_number = user_dictionary.get("mid");
                        numbers.add(mobile_number);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG,"click");
        if(twice==true){
            Intent i=new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            System.exit(0);
        }
        //super.onBackPressed();
        twice=true;
        Log.d(TAG,"twice:"+twice);
        Toast.makeText(SignUp.this,"Press Back Again to Exit.",Toast.LENGTH_SHORT).show();
        Handler h= new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                twice=false;
                Log.d(TAG,"twice:"+ twice);
            }
        },3000);
    }

}