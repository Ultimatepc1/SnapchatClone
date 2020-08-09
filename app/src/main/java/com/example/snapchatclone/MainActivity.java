package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener{
    EditText passwordview;
    AutoCompleteTextView emailview;
    SharedPreferences snappreference;
    List<String> emaillist;
    ArrayAdapter<String> arrayAdapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordview=(EditText)findViewById(R.id.passwordview);
        emailview=findViewById(R.id.emailview);
        snappreference=this.getSharedPreferences("com.example.snapchatclone",MODE_PRIVATE);
        Set<String> set = snappreference.getStringSet("emaillist", null);
        emaillist=new ArrayList<String>();
        if(set!=null)
            emaillist.addAll(set);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emaillist);
        emailview.setAdapter(arrayAdapter);
        emailview.setThreshold(1);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() !=null){
            login();
        }
        passwordview.setOnKeyListener(this);
    }
    public void goclicked(View view){
        String email=emailview.getText().toString();
        String password=passwordview.getText().toString();
        if(password.length()<8){
            Toast.makeText(this, "Minimum Password Length Should be 8", Toast.LENGTH_SHORT).show();
            return;
        }
        //check to login user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("login ", "signInWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            addautofilllist(emailview.getText().toString());
                            login();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("error", "signInWithEmail:failure", task.getException());
                            signup();
                        }

                        // ...
                    }
                });
        //check signinuser
    }
    public void login(){
        Intent intent=new Intent(this,Snapsactivity.class);
        Toast.makeText(this, mAuth.getCurrentUser().getEmail()+" logged in", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
    public void signup(){
        mAuth.createUserWithEmailAndPassword(emailview.getText().toString(), passwordview.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task2) {
                        if (task2.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.i(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            addautofilllist(emailview.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("users").child(task2.getResult().getUser().getUid()).child("email").setValue(emailview.getText().toString());
                            login();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("createUserWithEmail:failure", ""+task2.getException());
                            Toast.makeText(MainActivity.this, "Something went wrong,Try again later", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        // ...
    }
    private void addautofilllist(String username){
        emaillist.add(username);
        Set<String> set = new HashSet<String>();
        set.addAll(emaillist);
        snappreference.edit().putStringSet("emaillist", set).apply();
        arrayAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()== KeyEvent.ACTION_DOWN){
            goclicked(findViewById(R.id.gobutton));
        }
        return false;
    }
}