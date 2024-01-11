package edu.northeastern.team21.Foggy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import edu.northeastern.team21.Foggy.ImageRV.ImageAdapter;
import edu.northeastern.team21.Foggy.ImageRV.ImageItem;
import edu.northeastern.team21.R;
import edu.northeastern.team21.StickIt.SendStickerDialog;
import edu.northeastern.team21.StickIt.StickerRV.StickerAdapter;
import edu.northeastern.team21.StickIt.StickerRV.StickerCard;
import edu.northeastern.team21.SystemBarHiddenAbstractActivity;

public class userClickedPhotos extends SystemBarHiddenAbstractActivity {

    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    ArrayList<ImageItem> imagelist;
    Button sharebtn;
    BitmapDrawable drawable;
    Bitmap bitmap;
    private DatabaseReference userRef;
    private TextView noImagesTextView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_clicked_photos);
        recyclerView = findViewById(R.id.recyclerviewImage);
        noImagesTextView = findViewById(R.id.noImagesTextView);
        progressBar = findViewById(R.id.imageretival_progresBar);
        progressBar.setVisibility(View.VISIBLE);
//        sharebtn = findViewById(R.id.bu); // get reference to share button

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imagelist = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imagelist);
        recyclerView.setAdapter(imageAdapter);

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        String userID = authProfile.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(userID).child("images");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.VISIBLE);

                    noImagesTextView.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String imageURL = dataSnapshot.getValue(String.class);
                        String imageID = dataSnapshot.getKey();
                        DatabaseReference imageLocationRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(userID).child("imageLocations").child(imageID);
                        imageLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot locationSnapshot) {
                                String imageLocation = locationSnapshot.getValue(String.class);
                                ImageItem imageItem = new ImageItem(imageURL, imageLocation);
                                imagelist.add(imageItem);
                                imageAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(userClickedPhotos.this, "Failed to retrieve image location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    noImagesTextView.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(userClickedPhotos.this, "Failed to retrieve images", Toast.LENGTH_SHORT).show();
            }
        });


        ImageAdapter.ShareButtonClickListener shareButtonClickListener = new ImageAdapter.ShareButtonClickListener() {
            @Override
            public void onShareClick(int position) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                // Get the clicked image bitmap from its corresponding ImageView in the RecyclerView
                ImageAdapter.ImageViewHolder holder = (ImageAdapter.ImageViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                Bitmap bitmap = ((BitmapDrawable) holder.recyclerImage.getDrawable()).getBitmap();

                // Save the bitmap to a file
                File file = new File(getExternalCacheDir(), "image.jpg");
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
//                    Toast.makeText(this, "Failed to share image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a share intent and start an activity to share the file
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(intent, "Share Image via:"));

            }
        };
        imageAdapter.setOnItemClickListener(shareButtonClickListener);

    }

}
