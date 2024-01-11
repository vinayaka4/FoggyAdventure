package edu.northeastern.team21.Foggy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.northeastern.team21.Foggy.ProfileRV.ProfileAdapter;
import edu.northeastern.team21.Foggy.ProfileRV.ProfileItem;
import edu.northeastern.team21.R;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private ImageView imageViewProfileBg;
    private String profileImageURL;
    //public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private TextView nameTextView, personalDescriptionTextView;
    private Button signOutButton,imageActivitybtn;
    private List<ProfileItem> profileItems = new ArrayList<>();

    private RecyclerView recyclerView;

    private ProfileAdapter profileAdapter;

    private FirebaseAuth authProfile;

    private String name = "loading...",
                   personalDescription = "loading...",
                   userName = "loading...",
                   exploredArea = "loading...",
                   dob = "loading...",
                   phoneNumber = "loading...",
                   email = "loading...",
                   ins = "loading...";

    private ProgressBar progressBar;

    public final static String TAG = "__PROFILE__";

    private final int REQUEST_CODE_EDIT_PROFILE_IMAGE = 1;

    private boolean isAttached = false;

    // this variable is used for the onDataChange() method
    // since it runs in the backgound, sometimes it will crash by calling getContext(),
    // using this variable to replay getContext() can prevent this Fragment from crashing when changing orientation
    private Context currentContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentContext = getContext();

        Log.d(TAG, "profile fragment created");

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(TAG, "now in Portrait mode");
            this.imageViewProfileBg = view.findViewById(R.id.imageView_profile_bg);
            Drawable profileBg = ContextCompat.getDrawable(getContext(), R.drawable.image_pofile_bg);
            this.imageViewProfileBg.setImageDrawable(profileBg);
//            backgroundImageImageView = findViewById(R.id.imageView_login_bg);
//            backgroundImageImageView.setImageResource(R.drawable.image_loginsignup_bgimage);
        }else {
            Log.d(TAG, "now in Landscape mode");
            this.imageViewProfileBg = view.findViewById(R.id.imageView_profile_bg_landscape);
            Drawable profileBg_landscape = ContextCompat.getDrawable(getContext(), R.drawable.image_pofile_bg_landscape);
            this.imageViewProfileBg.setImageDrawable(profileBg_landscape);
//            backgroundImageImageView = findViewById(R.id.imageView_login_bg_land);
//            backgroundImageImageView.setImageResource(R.drawable.image_loginsignup_bgimage_landscape);
        }


        this.progressBar = view.findViewById(R.id.progressBar_profile);
        this.progressBar.bringToFront();
        this.progressBar.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        this.profileImage = view.findViewById(R.id.imageView_profileImage);

        this.nameTextView = view.findViewById(R.id.textView_name);
        this.nameTextView.setText(this.name);
        this.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile(-2);
            }
        });

        this.personalDescriptionTextView = view.findViewById(R.id.textView_description);
        this.personalDescriptionTextView.setText(this.personalDescription);
        this.personalDescriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile(-1);
            }
        });

        this.signOutButton = view.findViewById(R.id.button_profile_signout);
        this.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut(view);
            }
        });

        this.imageActivitybtn=view.findViewById(R.id.btn_view_pictures);

        this.imageActivitybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageview =  new Intent(getActivity(),userClickedPhotos.class);
                startActivity(imageview);

            }
        });

        this.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass the profile image (grab from cache) to the EditProfile Activity
                profileImage.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(profileImage.getDrawingCache());
                profileImage.setDrawingCacheEnabled(false);


                Intent intent =  new Intent(getActivity(), EditProfileImage.class);
                intent.putExtra("profileImage", bitmap);

                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE_IMAGE);
            }
        });

        this.initializeProfileItems();

        this.profileAdapter = new ProfileAdapter(getContext());
        this.profileAdapter.setProfileItems(this.profileItems);

        /***
         * Click the profile items in recyclerView to call a dialog which can update the informations
         * */
        this.profileAdapter.setClickListner(new ProfileAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editProfile(position);
            }
        });



        this.recyclerView = view.findViewById(R.id.recyclerProfileItems);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.profileAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        /***
         * Initialize the Firebase instance and fetch data
         * */
        this.fetchDataFromDB();

        return view;
    }




    /***
     * there are 6 profile info in the recyclerView for a user:
     * 0.username, 1.areaExplored, 2.dataOfBirth, 3.phoneNumber, 4.mail, 5.insAccount
     */
    private void initializeProfileItems(){
        this.profileItems.clear();

        Drawable iconDrawable0 = ContextCompat.getDrawable(getContext(), R.drawable.icon_profile_username);
        this.profileItems.add(new ProfileItem(iconDrawable0, this.userName));

        Drawable iconDrawable1 = ContextCompat.getDrawable(getContext(), R.drawable.icon_profile_explored_area);
        this.profileItems.add(new ProfileItem(iconDrawable1, this.exploredArea));

        Drawable iconDrawable2 = ContextCompat.getDrawable(getContext(), R.drawable.icon_profile_birthday);
        this.profileItems.add(new ProfileItem(iconDrawable2, this.dob));

        Drawable iconDrawable3 = ContextCompat.getDrawable(getContext(), R.drawable.icon_profile_phone);
        this.profileItems.add(new ProfileItem(iconDrawable3, this.phoneNumber));

        Drawable iconDrawable4 = ContextCompat.getDrawable(getContext(), R.drawable.icon_profile_email);
        this.profileItems.add(new ProfileItem(iconDrawable4, this.email));

        Drawable iconDrawable5 = ContextCompat.getDrawable(getContext(), R.drawable.icon_profile_instagram);
        this.profileItems.add(new ProfileItem(iconDrawable5, this.ins));


    }


    /***
     * calling this method, the profile image and user information will be fetched from Firebase,
     * and than will be displayed on the screen
     */
    private void fetchDataFromDB(){


        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "start connecting to Google Firebase");
        this.authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = this.authProfile.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(getContext(), "failed to load user profile from Firebase", Toast.LENGTH_SHORT).show();
        }else{
            String userID = firebaseUser.getUid();
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("UserProfile");

            // this addListenerForSingleValueEvent will fetch the data only once.
            referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.d(TAG, "fetching data...");
                    //fetching profilePic
                    profileImageURL = snapshot.child("profileImage").getValue(String.class);
                    if(profileImageURL==null){
                        profileImage.setImageResource(R.drawable.image_profile_default_image_round);
                    }else{
                        if(isAttached){
                            Glide.with(currentContext).load(profileImageURL).into(profileImage);
                        }

                    }



                    //fetching name, which is displayed beneath the profile image
                    name = snapshot.child("name").getValue(String.class);
                    nameTextView.setText(name);

                    //fetching personaldescription, which may be empty on Firabase
                    personalDescription = snapshot.child("personaldescription").getValue(String.class);
                    if(personalDescription==null){
                        personalDescription = "Personal Description...";
                    }
                    personalDescriptionTextView.setText(personalDescription);

                    //fetching username for recyclerView
                    userName = snapshot.child("username").getValue(String.class);
                   Drawable iconDrawable0 = ContextCompat.getDrawable(currentContext, R.drawable.icon_profile_username);
                   profileItems.set(0, new ProfileItem(iconDrawable0, userName));

                    //fetching explored Area for recyclerView
                    exploredArea = snapshot.child("explorearea").getValue(String.class);
                    if(exploredArea==null){
                        exploredArea = "no data";
                    }
                    Drawable iconDrawable1 = ContextCompat.getDrawable(currentContext, R.drawable.icon_profile_explored_area);
                    profileItems.set(1, new ProfileItem(iconDrawable1, exploredArea));

                    //fetching date of birth for recyclerView
                    dob = snapshot.child("dob").getValue(String.class);
                    Drawable iconDrawable2 = ContextCompat.getDrawable(currentContext, R.drawable.icon_profile_birthday);
                    profileItems.set(2, new ProfileItem(iconDrawable2, dob));

                    //fetching phone number for recyclerView
                    phoneNumber = snapshot.child("phone").getValue(String.class);
                    ins = snapshot.child("ins").getValue(String.class);
                    if(phoneNumber==null){
                        phoneNumber = "no data";
                    }
                    Drawable iconDrawable3 = ContextCompat.getDrawable(currentContext, R.drawable.icon_profile_phone);
                    profileItems.set(3, new ProfileItem(iconDrawable3, phoneNumber));

                    //fetching useremail for recyclerView
                    email = snapshot.child("useremail").getValue(String.class);
                    Drawable iconDrawable4 = ContextCompat.getDrawable(currentContext, R.drawable.icon_profile_email);
                    profileItems.set(4, new ProfileItem(iconDrawable4, email));

                    //fetching instagram for recyclerView
                    ins = snapshot.child("ins").getValue(String.class);
                    if(ins==null){
                        ins = "no data";
                    }
                    Drawable iconDrawable5 = ContextCompat.getDrawable(currentContext, R.drawable.icon_profile_instagram);
                    profileItems.set(5, new ProfileItem(iconDrawable5, ins));

                    profileAdapter.setProfileItems(profileItems);

                    //Toast.makeText(getContext(), "profile refreshed", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "failed to fetch data from Firebase", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent goBackIntent) {
        super.onActivityResult(requestCode, resultCode, goBackIntent);
        Log.d(TAG, "onActivityResult");
        if(requestCode==this.REQUEST_CODE_EDIT_PROFILE_IMAGE){
            Log.d(TAG, "back from EditProfileImage Activity ");
            this.fetchDataFromDB();
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }




    /***
     * This method will be called when the LOGOUT bottom is clicked
     * Will logout and end this Activity
     */
    private void signOut(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setMessage("Are you sure you want to signout and close this activity?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                authProfile.signOut();
                if(getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editProfile(int position){
        ProfileItemsEditDialog profileItemsEditDialog = new ProfileItemsEditDialog();
        Bundle args = new Bundle();

        String content = this.getContent(position);

        args.putInt("position",position);
        args.putString("content", content);

        if(position!=1 && position!=4){
            profileItemsEditDialog.setArguments(args);
            profileItemsEditDialog.setTargetFragment(this, this.REQUEST_CODE_EDIT_PROFILE_IMAGE);
            profileItemsEditDialog.show(getParentFragmentManager().beginTransaction(), "profileItemsEditDialog");
        }else if(position==1){
            Toast.makeText(currentContext, "You cannot change Explored Area", Toast.LENGTH_SHORT).show();
        }else if(position==4){
            Toast.makeText(currentContext, "You cannot change Email ", Toast.LENGTH_SHORT).show();
        }

    }

    private String getContent(int position){
        if(position==-2) return this.name;
        if(position==-1) return this.personalDescription;
        if(position==0) return this.userName;
        // position==1, explored area cannot be edited
        if(position==2) return this.dob;
        if(position==3) return this.phoneNumber;
        //position==4, email cannot be edited
        if(position==5) return this.ins;
        Log.d(TAG, "Wrong Content");
        return "Wong Content";
    }













}