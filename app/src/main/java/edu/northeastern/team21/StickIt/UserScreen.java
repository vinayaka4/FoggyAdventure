package edu.northeastern.team21.StickIt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.team21.R;
import edu.northeastern.team21.StickIt.StickerRV.StickerAdapter;
import edu.northeastern.team21.StickIt.StickerRV.StickerCard;

public class UserScreen extends AppCompatActivity implements SendStickerDialog.SendStickerDialogListener {
    private String username;
    private TextView usernameTV;
    private List<StickerCard> stickerList = new ArrayList<>();
    private Map<String, StickerCard> stickerMap = new HashMap<>();
    private RecyclerView recyclerView;
    private StickerAdapter stickerAdapter;
    private FirebaseDatabase database;
    private int curStickerIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userscreen);

        usernameTV = findViewById(R.id.userid);
        username = getIntent().getStringExtra("username");
        usernameTV.setText("Welcome: " + username + " !");

        database = FirebaseDatabase.getInstance();

        createRecycleView();

        createNotificationChannel();
        listenSticker();
    }

    // Handle history Screen
    public void historyScreen(View view) {
        Intent intent = new Intent(this, HistoryScreen.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(String receiver) {
        // if receiver  is empty
        if (receiver.isEmpty()) {
            Toast.makeText(UserScreen.this, "RECEIVER CANNOT BE EMPTY", Toast.LENGTH_SHORT).show();
            return;
        }
        //  send to DB
        DatabaseReference userRef = database.getReference("users");
        userRef.child(receiver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check receiver
                if (!snapshot.exists()) {
                    Toast.makeText(UserScreen.this, "Receiver not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StickerCard stickerCard = stickerList.get(curStickerIdx);
                // add history
                long timeMillis = System.currentTimeMillis();
                Message message = new Message(username, receiver, LocalDateTime.now().toString(), stickerCard.getStickerId(), stickerCard.getStickerName());
                Map<String, Object> messageValues = message.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/messagesSent/" + username + "/" + timeMillis, messageValues);
                childUpdates.put("/messagesReceived/" + receiver + "/" + timeMillis, messageValues);
                database.getReference().updateChildren(childUpdates);

                addCount(stickerCard.getStickerId());

                Toast.makeText(UserScreen.this, "Send " + stickerCard.getStickerName() + " to " + receiver + " successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createRecycleView() {
        recyclerView = findViewById(R.id.recyclerSticker);
        recyclerView.setHasFixedSize(true);

        stickerAdapter = new StickerAdapter();

        // get the stickers list map
        getStickerMap();
        // get the sticker sent history array from FRDB
        DatabaseReference historyRef = database.getReference("stickersCount");
        historyRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getChildren().forEach((data -> {
                        if (stickerMap.containsKey(data.getKey())) {
                            stickerMap.get(data.getKey()).setSentCount(data.getValue(Integer.class));
                        }
                    }));
                }
                stickerList.clear();
                stickerList.addAll(stickerMap.values());
                stickerAdapter.setStickerList(stickerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StickerAdapter.StickerClickListener stickerClickListener = new StickerAdapter.StickerClickListener() {
            @Override
            public void onStickerClick(int position) {
                // open dialog
                curStickerIdx = position;
                StickerCard sticker = stickerList.get(position);

                Bundle bundle = new Bundle();
                bundle.putInt("stickerResourceId", sticker.getStickerResourceId());
                SendStickerDialog stickerDialog = new SendStickerDialog();
                stickerDialog.setArguments(bundle);
                stickerDialog.show(getSupportFragmentManager(), "Send Sticker");

            }
        };
        stickerAdapter.setOnStickerClickListener(stickerClickListener);

        recyclerView.setAdapter(stickerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void getStickerMap() {
        TypedArray stickerArray = getResources().obtainTypedArray(R.array.sticker_array);

        for (int i = 0; i < stickerArray.length(); i++) {
            int resId = stickerArray.getResourceId(i, -1);
            if (resId != -1) {
                String entryName = stickerArray.getResources().getResourceEntryName(resId);
                stickerMap.put(String.valueOf(i), new StickerCard(i, resId, entryName.split("_")[1]));
            }
        }

        stickerArray.recycle();
    }

    private void addCount(int stickerId) {
        // update count
        DatabaseReference stickersCount = database.getReference("stickersCount");
        stickersCount.child(username).child(String.valueOf(stickerId)).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer value = currentData.getValue(Integer.class);
                if (value == null) {
                    value = 0;
                }
                value++;
                currentData.setValue(value);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    stickerList.get(curStickerIdx).setSentCount(currentData.getValue(int.class));
                    stickerAdapter.notifyItemChanged(curStickerIdx);
                }
            }
        });
    }

    private void listenSticker() {
        database.getReference("messagesReceived").child(username).orderByKey().startAt(String.valueOf(System.currentTimeMillis())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Message message = snapshot.getValue(Message.class);
                    int resourceId = stickerMap.get(String.valueOf(message.getStickerId())).getStickerResourceId();
                    if (resourceId != -1) {
                        message.setStickerResourceId(resourceId);
                        sendNotification(message);
                    }
                }
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
    }

    private void sendNotification(Message message) {
        Bitmap sticker = BitmapFactory.decodeResource(getResources(), message.getStickerResourceId());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sticker Received")
                .setContentText("Hi, " + message.getReceiver() + ", you got a sticker from " + message.getSender() + " at " + message.getDateTime().toString())
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(sticker))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}