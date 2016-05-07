package com.example2.parth.smart_tagging;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.HashMap;
import java.util.Map;


public class AdminLogInActivity extends AppCompatActivity{


    private Intent intent;
    private EditText ET1,ET2,ET3;
    private String username,password,environment,new_username,new_password,new_environment;
    private Firebase reference,reference_environment,reference_username,reference_password;
    private Map<String,Object> fetcher1,fetcher2,fetcher3;
    private long counter1,counter2,counter3,counter4 = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Firebase.setAndroidContext(this);
        ET1 = (EditText)findViewById(R.id.AdminLogInActivityET1);
        ET2 = (EditText)findViewById(R.id.AdminLogInActivityET2);
        ET3 = (EditText)findViewById(R.id.AdminLogInActivityET3);
    }


    public void onClickAdminSignUpActivity(View view){
        intent = new Intent(AdminLogInActivity.this,AdminSignUpActivity.class);
        startActivity(intent);
    }


    public void onClickAdminHomeActivity(View view){
        environment = (ET1.getText()).toString();
        username = (ET2.getText()).toString();
        password = (ET3.getText()).toString();
        reference = new Firebase("https://amber-inferno-6557.firebaseio.com/Smart_Tagging/");
        reference_environment = reference.child("Applications_List");
        fetcher1 = new HashMap<String,Object>();
        fetcher1.put("fetcher","fetch");
        reference_environment.updateChildren(fetcher1);
        reference_environment.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot adataSnapshot){
                counter2 = adataSnapshot.getChildrenCount();
                for(DataSnapshot resultDataSnapshot: adataSnapshot.getChildren()){
                    new_environment = resultDataSnapshot.getKey();
                    if(new_environment.equals(environment)){
                        String latest_path = environment + "/Users_List/";
                        reference_username = reference.child(latest_path);
                        fetcher2 = new HashMap<String,Object>();
                        fetcher2.put("fetcher"," ");
                        reference_username.updateChildren(fetcher2);
                        reference_username.addValueEventListener(new ValueEventListener(){
                            @Override
                            public void onDataChange(DataSnapshot bdataSnapshot){
                                counter3 = bdataSnapshot.getChildrenCount();
                                for (DataSnapshot resultDataSnapshot : bdataSnapshot.getChildren()){
                                    new_username = resultDataSnapshot.getKey();
                                    if(new_username.equals(username)){
                                        String new_latest_path = environment + "/Users_Credential/" + username + "/";
                                        reference_password = reference.child(new_latest_path);
                                        fetcher3 = new HashMap<String, Object>();
                                        fetcher3.put("fetcher"," ");
                                        reference_password.updateChildren(fetcher3);
                                        reference_password.addValueEventListener(new ValueEventListener(){
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot){
                                                new_password = dataSnapshot.child("password").getValue(String.class);
                                                if(new_password.equals(password)){
                                                    Toast.makeText(AdminLogInActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                                                    sharedPreferences = getSharedPreferences("Smart_Tagging",Context.MODE_PRIVATE);
                                                    editor = sharedPreferences.edit();
                                                    editor.putString("environment",environment);
                                                    editor.putString("username",username);
                                                    editor.apply();
                                                    intent = new Intent(AdminLogInActivity.this,UsersLoginService.class);
                                                    startService(intent);
                                                }
                                                else{
                                                    Toast.makeText(AdminLogInActivity.this,"Login Failed...Incorrect Password",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(FirebaseError firebaseError){
                                                //nothing to do here . . .
                                            }
                                        });
                                    }
                                    else{
                                        counter4++;
                                        if(counter4 == counter3){
                                            Toast.makeText(AdminLogInActivity.this,"Login Failed...Incorrect Username",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError){
                                //nothing to do here . . .
                            }
                        });
                    }
                    else{
                        counter1++;
                        if(counter1 == counter2){
                            Toast.makeText(AdminLogInActivity.this,"Login Failed...Incorrect Environment",Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError){
                //nothing to do here . . .
            }
        });
    }


    public void onClickAdminLogInExitActivity(View view){
        finish();
        System.exit(0);
    }


    public void onClickAdminForgotCredentialsActivity(View view){
        intent = new Intent(AdminLogInActivity.this,AdminForgotCredentialsActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_log_in, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}