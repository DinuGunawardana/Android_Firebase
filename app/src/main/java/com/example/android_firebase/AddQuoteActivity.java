package com.example.android_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddQuoteActivity extends AppCompatActivity {

    private EditText quotEditText;
    private EditText authorEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        //bindviews
        quotEditText = findViewById(R.id.editTextQuote);
        authorEditText = findViewById(R.id.editTextAuthor);
        addButton = findViewById(R.id.button);

        //listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text
                String quote = quotEditText.getText().toString();
                String author = authorEditText.getText().toString();

                //check if empty
                if (quote.isEmpty()) {
                    quotEditText.setError("Cannot be Empty");
                    return;
                }

                if (author.isEmpty()) {
                    authorEditText.setError("Cannot be Empty");
                    return;
                }

                //add to database
                addQuoteToDB(quote, author);
            }
        });

        //create in database
    }

    private void addQuoteToDB(String quote, String author) {

        //create a hashmap
        HashMap<String, Object> quoteHashmap = new HashMap<>();
        quoteHashmap.put("quote", quote);
        quoteHashmap.put("author", author);

        //instantiate database connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference quotesRef = database.getReference("quotes");

        String key = quotesRef.push().getKey();
        quoteHashmap.put("key", key);

        quotesRef.child(key).setValue(quoteHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddQuoteActivity.this, "Added", Toast.LENGTH_SHORT).show();
                quotEditText.getText().clear();
                authorEditText.getText().clear();
            }
        });

    }
}