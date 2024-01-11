package edu.northeastern.team21.StickIt;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.team21.R;

public class HistoryScreen extends AppCompatActivity {
    private String username;
    private TextView usernameTV;
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private FirebaseDatabase database;
    private Map<Integer, Integer> stickerId2Resource = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);

        username = getIntent().getStringExtra("username");
        usernameTV = findViewById(R.id.historyText);
        usernameTV.setText("Sticker received history of: " + username + " !");
        database = FirebaseDatabase.getInstance();
        createRecycleView();
    }

    private void createRecycleView() {
        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter();

        getStickerResourceMap();

        database.getReference("messagesReceived").child(username).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Message message = snapshot.getValue(Message.class);
                    message.setStickerResourceId(stickerId2Resource.get(message.getStickerId()));
                    historyAdapter.insertToHead(message);
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

        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getStickerResourceMap() {
        TypedArray stickerArray = getResources().obtainTypedArray(R.array.sticker_array);

        for (int i = 0; i < stickerArray.length(); i++) {
            int resId = stickerArray.getResourceId(i, -1);
            if (resId != -1) {
                stickerId2Resource.put(i, resId);
            }
        }

        stickerArray.recycle();
    }

}