package com.android.dgsignchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ChatRoomActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    EditText editText;
    Button bt_send;
    String id, pubkey;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        sharedPreferences = getApplicationContext().getSharedPreferences("digisign.data", MODE_PRIVATE);

        editText = findViewById(R.id.et_message);
        bt_send = findViewById(R.id.bt_send);

        RecyclerView recyclerView = findViewById(R.id.rv_chat);
        ArrayList<Chat> chats = new ArrayList<>();
        chatAdapter = new ChatAdapter(chats, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        getSupportActionBar().setTitle(name);

        databaseReference.child("digisign").child("chat").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Chat c = ds.getValue(Chat.class);
                    byte[] publickey, signedmess;
                    assert c != null;
                    publickey = Base64.decode(c.getPubkey().getBytes(StandardCharsets.UTF_16), Base64.DEFAULT);
                    signedmess = Base64.decode(c.getSignedMessage().getBytes(StandardCharsets.UTF_16), Base64.DEFAULT);

                    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publickey);
                    KeyFactory keyFactory;
                    try {
                        keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
                        Signature sig = Signature.getInstance("SHA256withRSA");
                        sig.initVerify(pubKey);
                        sig.update(c.getMessage().getBytes());
                        boolean verifies = sig.verify(signedmess);
                        c.setAlert(!verifies);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
                        e.printStackTrace();
                    }
                    chats.add(c);
                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chats.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bt_send.setOnClickListener(view -> {
            if(editText.getText().toString().length() > 0){
                sendMessage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.demo) {
            item.setChecked(!item.isChecked());
            editor = sharedPreferences.edit();
            editor.putBoolean("demo", item.isChecked());
            editor.apply();
            chatAdapter.notifyDataSetChanged();
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;

    }

    void sendMessage() {
        String message = editText.getText().toString();
        String signed = signMessage(message);
        Chat chat = new Chat(sharedPreferences.getString("name", ""), message, signed);
        chat.setPubkey(pubkey);

        String key = databaseReference.child("digisign").child("chat").child(id).push().getKey();
        assert key != null;
        databaseReference.child("digisign").child("chat").child(id).child(key).setValue(chat);
        editText.setText("");
    }

    String signMessage(String message){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            keyPairGenerator.initialize(2048, random);
            KeyPair pair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initSign(privateKey);
            dsa.update(message.getBytes());
            byte[] realSig = dsa.sign();
            String signed = new String(Base64.encode(realSig, Base64.DEFAULT), StandardCharsets.UTF_16);
            byte[] key = publicKey.getEncoded();
            pubkey = new String(Base64.encode(key, Base64.DEFAULT), StandardCharsets.UTF_16);
            return signed;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return message;
    }

    Boolean checkMessage(String original, String signed){
        return true;
    }

}