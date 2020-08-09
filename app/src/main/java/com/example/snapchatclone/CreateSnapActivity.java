package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity implements View.OnKeyListener{
    ImageView createsnapview;
    EditText messageview;
    String imgname= UUID.randomUUID().toString()+".jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snap);
        createsnapview=(ImageView)findViewById(R.id.createsnapview);
        messageview=(EditText)findViewById(R.id.imagemessageview);
        messageview.setOnKeyListener(this);
    }
    public void chooseimgclicked(View view){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            getphoto();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode== RESULT_OK){
                if(data!=null){
                    try {
                        Uri selectedimage=data.getData();
                        Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedimage);
                        //createsnapview.setBackgroundColor();
                        createsnapview.setImageBitmap(bitmap);
                    }catch (Exception e){
                        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        Log.i("Data error",""+e);
                    }
                }else{
                    Toast.makeText(this, "No Data Passed", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Result not ok", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void getphoto(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getphoto();
            }
        }
    }
    public void nextclicked(View view){
        // Get the data from an ImageView as bytes
        createsnapview.setDrawingCacheEnabled(true);
        createsnapview.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) createsnapview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(bitmap==null){
            Toast.makeText(this, "Please Select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("images").child(imgname);
        //StorageReference uploadTask=FirebaseStorage.getInstance().getReference().child("images").child(imgname);
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateSnapActivity.this, "Something went wrong,try again later", Toast.LENGTH_SHORT).show();
                Log.i("Imageupload error",exception+"");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();
                Toast.makeText(CreateSnapActivity.this, "Image Upload Successful", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CreateSnapActivity.this,ChooseUserActivity.class);
                intent.putExtra("imageurl",url.toString());
                intent.putExtra("imagename", imgname);
                intent.putExtra("message", messageview.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()== KeyEvent.ACTION_DOWN){
            nextclicked(findViewById(R.id.nextbutton));
        }
        return false;
    }
}