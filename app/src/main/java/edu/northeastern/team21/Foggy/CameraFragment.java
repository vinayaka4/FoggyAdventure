package edu.northeastern.team21.Foggy;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.team21.R;

public class CameraFragment extends Fragment {
    public static final int GALLERY_REQUEST = 200;
    private static final int CAMERA_PREM_CODE = 101;
    public static final int REQUEST_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    ImageView captured_image;
    String currentPhotoPath;
    FirebaseAuth authProfile;
    StorageReference storageReference;
    private static String TAG = "CAMERA ACTIVITY";
    ProgressBar progressbar;
    Button camerabtn, gallerybtn;

    String city, country;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);


        captured_image = view.findViewById(R.id.camera_image);
        camerabtn = view.findViewById(R.id.camerabtn);
        gallerybtn = view.findViewById(R.id.gallery);
        progressbar = view.findViewById(R.id.camera_progressBar);
        progressbar.setVisibility(View.INVISIBLE);
        storageReference = FirebaseStorage.getInstance().getReference();

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST);

            }
        });

        return view;

    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PREM_CODE);

        } else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PREM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                captured_image.setImageURI(Uri.fromFile(f));
                Log.d(TAG, "image url" + Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getContext().sendBroadcast(mediaScanIntent);
                progressbar.setVisibility(View.VISIBLE);
                uploadImageTodb(f.getName(), contentUri);
            }
        }
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                captured_image.setImageURI(contentUri);
                Log.d(TAG, "image url" + imageFileName);
                progressbar.setVisibility(View.VISIBLE);
                uploadImageTodb(imageFileName, contentUri);

            }
        }
    }

    private void uploadImageTodb(String name, Uri contentUri) {
        StorageReference image = storageReference.child("images/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressbar.setVisibility(View.GONE);
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        authProfile = FirebaseAuth.getInstance();
                        String userID = authProfile.getCurrentUser().getUid();
                        if (authProfile.getCurrentUser() != null) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(userID);
                            String imageKey = userRef.child("images").push().getKey();
                            userRef.child("images").child(imageKey).setValue(uri.toString());
                            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                if (location != null) {
                                                    double latitude = location.getLatitude();
                                                    double longitude = location.getLongitude();
                                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                                    progressbar.setVisibility(View.GONE);
                                                    try {
                                                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                                        String currentCity = addresses.get(0).getLocality();
                                                        Log.d(TAG, "onSuccess: upload image uri is" + currentCity);
                                                        userRef.child("imageLocations").child(imageKey).setValue(currentCity);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                progressbar.setVisibility(View.GONE);
                                                Log.d(TAG, "onSuccess: upload image uri is" + uri.toString());
                                            }
                                        });
                            }
                        }
                    }
                });
                Toast.makeText(getContext(), "Upload Image successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressbar.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private String getFileExt(Uri contentUri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(contentUri));

    }


    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "edu.northeastern.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}