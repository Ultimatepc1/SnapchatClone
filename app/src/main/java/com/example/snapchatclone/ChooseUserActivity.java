package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChooseUserActivity extends AppCompatActivity {
    List<String> userlist,keys;
    ListView chooseuserview;
    ArrayAdapter<String> useradapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        userlist=new ArrayList<String>();
        keys=new ArrayList<String>();
        chooseuserview=(ListView)findViewById(R.id.chooseuserview);
        useradapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userlist);
        chooseuserview.setAdapter(useradapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String emaile= (String) snapshot.child("email").getValue();
                userlist.add(emaile);
                keys.add((String)snapshot.getKey());
                useradapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        chooseuserview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String,String> snapmap=new HashMap<String, String>();
                snapmap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                Intent prev=getIntent();
                snapmap.put("imagename",prev.getStringExtra("imagename"));
                snapmap.put("imageurl",prev.getStringExtra("imageurl"));
                snapmap.put("message",prev.getStringExtra("message"));
                FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(i)).child("snaps").push().setValue(snapmap);
                Intent intent=new Intent(ChooseUserActivity.this,Snapsactivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}