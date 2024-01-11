package edu.northeastern.team21.Foggy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.northeastern.team21.R;
import edu.northeastern.team21.SystemBarHiddenAbstractActivity;

public class EditProfileImage extends SystemBarHiddenAbstractActivity {

    private CircleImageView profileImageEdit;

    private Button buttonSelectImage, buttonUpdate;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri selectedImageUri = null;

    private final String TAG = "__EDIT_PROFILE_IMAGE__";

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.progressBar = findViewById(R.id.progressBar_profileImageEdit);
        this.progressBar.setVisibility(View.GONE);

        // load the image which is passed from profile fragment
        this.profileImageEdit = findViewById(R.id.profileImage_edit);
        Bitmap bitmap = getIntent().getParcelableExtra("profileImage");
        this.profileImageEdit.setImageBitmap(bitmap);

        // initialize the buttons
        this.buttonSelectImage = findViewById(R.id.btn_select);
        this.buttonUpdate = findViewById(R.id.btn_update_profile_image);

    }

    /**
     * onClickListener for the SELECT IMAGE button
     * open the gallery to select a pic
     * */
    public void selectPhotoFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), this.PICK_IMAGE_REQUEST);
    }


    /**
     * onClickListener for the UPDATE button
     * open the gallery to select a pic
     * */
    public void updateProfileImage(View view){
        if(this.selectedImageUri==null){
            Toast.makeText(this, "No picture chose", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        StorageReference image = FirebaseStorage.getInstance().getReference("profileImage/"+this.selectedImageUri.toString());
        image.putFile(this.selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseAuth authProfile = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = authProfile.getCurrentUser();
                        String userID = firebaseUser.getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(userID);
                        userRef.child("profileImage").setValue(uri.toString());

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "profile image uploaded", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "failed to upload profile image", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "failed to upload profile image", Toast.LENGTH_SHORT).show();
            }
        });
        //progressBar.setVisibility(View.GONE);
    }




    /**
     * handling the data returned by from the gallery
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        // if a picture has been return
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Log.d(TAG, "return with something");
            this.selectedImageUri = data.getData();
            //update the displayed profile image in this page. do not upload to Firebase at this moment
            Glide.with(this).load(selectedImageUri).into(this.profileImageEdit);
        }
    }



}