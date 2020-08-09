package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SnapdisplayActivity extends AppCompatActivity {
    TextView messagedisplayview;
    ImageView snapdisplayview;
    Intent prev;
    private FirebaseAuth mAuth;
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.logout:
                imgdeletion();
                logout();
                return true;
            case R.id.createsnap:
                Intent inte=new Intent(this,CreateSnapActivity.class);
                imgdeletion();
                startActivity(inte);
                finish();
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
        setContentView(R.layout.activity_snapdisplay);
        messagedisplayview=(TextView)findViewById(R.id.messagedisplayview);
        snapdisplayview=(ImageView)findViewById(R.id.snapdisplayview);
        mAuth=FirebaseAuth.getInstance();
        prev=getIntent();
        messagedisplayview.setText(prev.getStringExtra("message"));
        GetBitmapFromFirebase getBitmapFromFirebase = new GetBitmapFromFirebase();
        try {
            Bitmap bitmap = getBitmapFromFirebase.execute(prev.getStringExtra("imageurl")).get();
            if(bitmap==null){
                Log.i("setting imagesuccess","no image found");
            }
            snapdisplayview.setImageBitmap(bitmap);
        }catch(Exception e){
            Toast.makeText(this, "Something went wrong,try again later", Toast.LENGTH_SHORT).show();
            Log.i("getsnap error",e+"");
        }
    }
    public class GetBitmapFromFirebase extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                return BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.i("Async bitmap error",""+e);
            }

            return null;
        }
    }
    protected void logout(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        Intent logout1=new Intent(getApplicationContext(),MainActivity.class);
        logout1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout1);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        imgdeletion();
    }
    public void imgdeletion(){
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("snaps").child(prev.getStringExtra("snapkey")).removeValue();
        FirebaseStorage.getInstance().getReference().child("images").child(prev.getStringExtra("imagename")).delete();
    }
}