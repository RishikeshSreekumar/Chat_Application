package com.android.dgsignchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatHomeActivity extends AppCompatActivity {
    
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        sharedPreferences = getApplicationContext().getSharedPreferences("digisign.data", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getBoolean("login", false)){
            Intent i = new Intent(ChatHomeActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            RecyclerView recyclerView = findViewById(R.id.rv_room);
            ArrayList<Room> rooms = new ArrayList<>();
            RoomAdapter roomAdapter = new RoomAdapter(rooms, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(roomAdapter);

            getSupportActionBar().setTitle("Rooms");
            databaseReference.child("digisign").child("room").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    rooms.clear();
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        rooms.add(ds.getValue(Room.class));
                    }
                    roomAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}