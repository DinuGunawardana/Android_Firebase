package com.example.android_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText emailEditText;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;

    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read user data", databaseError.toException());
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                selectedUser = userList.get(position);
                nameEditText.setText(selectedUser.getName());
                ageEditText.setText(String.valueOf(selectedUser.getAge()));
                emailEditText.setText(selectedUser.getEmail());
            }

            @Override
            public void onLongClick(View view, int position) {
                // Not needed for this example
            }
        }));
    }

    private void addUser() {
        String name = nameEditText.getText().toString().trim();
        int age = Integer.parseInt(ageEditText.getText().toString().trim());
        String email = emailEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {
            String userId = databaseReference.push().getKey();
            User user = new User(name, age, email);
            databaseReference.child(userId).setValue(user);
            clearFields();
        } else {
            Toast.makeText(this, "Please enter name and email", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser() {
        if (selectedUser != null) {
            String name = nameEditText.getText().toString().trim();
            int age = Integer.parseInt(ageEditText.getText().toString().trim());
            String email = emailEditText.getText().toString().trim();

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {
                selectedUser.setName(name);
                selectedUser.setAge(age);
                selectedUser.setEmail(email);
                databaseReference.child(selectedUser.getName()).setValue(selectedUser);
                clearFields();
            } else {
                Toast.makeText(this, "Please enter name and email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Select a user to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser() {
        if (selectedUser != null) {
            databaseReference.child(selectedUser.getName()).removeValue();
            clearFields();
        } else {
            Toast.makeText(this, "Select a user to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        nameEditText.setText("");
        ageEditText.setText("");
        emailEditText.setText("");
    }

}