package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Snapsactivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ListView snapslistview;
    List<String> emailslist;
    List<DataSnapshot> snaparraylist;
    ArrayAdapter<String> snapsadapter;
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.logout:
                logout();
                return true;
            case R.id.createsnap:
                Intent inte=new Intent(this,CreateSnapActivity.class);
                startActivity(inte);
                return true;
            default:return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.snapmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapsactivity);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() ==null){
            logout();
        }
        snapslistview=(ListView)findViewById(R.id.snapslistview);
        emailslist=new ArrayList<String>();
        snaparraylist=new ArrayList<DataSnapshot>();
        snapsadapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emailslist);
        snapslistview.setAdapter(snapsadapter);
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                emailslist.add(snapshot.child("from").getValue().toString());
                snaparraylist.add(snapshot);
                snapsadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int j=0;
                for(DataSnapshot snap: snaparraylist){
                    if(snap.getKey().equals(snapshot.getKey())){
                      snaparraylist.remove(j);
                      emailslist.remove(j);
                      snapsadapter.notifyDataSetChanged();
                    }
                    j++;
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        snapslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(Snapsactivity.this,SnapdisplayActivity.class);
                intent.putExtra("imagename",snaparraylist.get(i).child("imagename").getValue().toString());
                intent.putExtra("imageurl",snaparraylist.get(i).child("imageurl").getValue().toString());
                intent.putExtra("message",snaparraylist.get(i).child("message").getValue().toString());
                intent.putExtra("snapkey",snaparraylist.get(i).getKey().toString());
                startActivity(intent);
            }
        });
    }
    protected void logout(){
        mAuth.signOut();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        Intent logout1=new Intent(getApplicationContext(),MainActivity.class);
        logout1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout1);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logout();
    }
}