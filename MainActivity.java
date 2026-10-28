
package com.example.favouritequotesapp1;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText etQuote, etAuthor;
    Button btnAdd, btnUpdate, btnDelete, btnClear;
    ListView listQuotes;

    DBHelper dbHelper;

    ArrayList<Quote> quoteList;
    ArrayAdapter<Quote> adapter;

    int selectedQuoteId = -1;

    // Model class for a quote
    static class Quote {
        int id;
        String quote;
        String author;

        Quote(int id, String quote, String author) {
            this.id = id;
            this.quote = quote;
            this.author = author;
        }

        @Override
        public String toString() {
            return "\"" + quote + "\"\n— " + author;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etQuote = findViewById(R.id.etQuote);
        etAuthor = findViewById(R.id.etAuthor);

        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnClear = findViewById(R.id.btnClear);

        listQuotes = findViewById(R.id.listQuotes);

        dbHelper = new DBHelper(this);
        quoteList = new ArrayList<>();

        // Adapter displays quotes in the ListView
        adapter = new ArrayAdapter<Quote>(
                this,
                android.R.layout.simple_list_item_1,
                quoteList
        ) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {

                TextView textView = (TextView) super.getView(
                        position, convertView, parent);

                textView.setSingleLine(false);
                textView.setTextSize(16);
                textView.setPadding(16, 16, 16, 16);

                return textView;
            }
        };

        listQuotes.setAdapter(adapter);

        // CREATE
        btnAdd.setOnClickListener(v -> addQuote());

        // UPDATE
        btnUpdate.setOnClickListener(v -> updateQuote());

        // DELETE
        btnDelete.setOnClickListener(v -> confirmDelete());

        // CLEAR INPUT FIELDS
        btnClear.setOnClickListener(v -> clearFields());

        // READ: Select a quote from the list
        listQuotes.setOnItemClickListener((parent, view,
                                           position, id) -> {

            Quote selected = quoteList.get(position);

            selectedQuoteId = selected.id;

            etQuote.setText(selected.quote);
            etAuthor.setText(selected.author);

            Toast.makeText(
                    MainActivity.this,
                    "Quote selected for editing",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Display existing quotes
        loadQuotes();
    }

    // CREATE
    private void addQuote() {
        String quote = etQuote.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (quote.isEmpty() || author.isEmpty()) {
            Toast.makeText(this,
                    "Please enter both quote and author",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.insertQuote(quote, author);

        if (result != -1) {
            Toast.makeText(this,
                    "Quote added successfully",
                    Toast.LENGTH_SHORT).show();

            clearFields();
            loadQuotes();
        } else {
            Toast.makeText(this,
                    "Failed to add quote",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // READ
    private void loadQuotes() {
        quoteList.clear();

        Cursor cursor = dbHelper.getAllQuotes();

        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow("id"));

                    String quote = cursor.getString(
                            cursor.getColumnIndexOrThrow("quote"));

                    String author = cursor.getString(
                            cursor.getColumnIndexOrThrow("author"));

                    quoteList.add(new Quote(id, quote, author));

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    // UPDATE
    private void updateQuote() {
        if (selectedQuoteId == -1) {
            Toast.makeText(this,
                    "Please select a quote to update",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String quote = etQuote.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (quote.isEmpty() || author.isEmpty()) {
            Toast.makeText(this,
                    "Please enter both quote and author",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateQuote(
                selectedQuoteId, quote, author);

        if (result > 0) {
            Toast.makeText(this,
                    "Quote updated successfully",
                    Toast.LENGTH_SHORT).show();

            clearFields();
            loadQuotes();
        } else {
            Toast.makeText(this,
                    "Update failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // DELETE with confirmation
    private void confirmDelete() {
        if (selectedQuoteId == -1) {
            Toast.makeText(this,
                    "Please select a quote to delete",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Quote")
                .setMessage("Are you sure you want to delete this quote?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int result = dbHelper.deleteQuote(selectedQuoteId);

                    if (result > 0) {
                        Toast.makeText(this,
                                "Quote deleted successfully",
                                Toast.LENGTH_SHORT).show();

                        clearFields();
                        loadQuotes();
                    } else {
                        Toast.makeText(this,
                                "Delete failed",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Clear the input fields and selection
    private void clearFields() {
        etQuote.setText("");
        etAuthor.setText("");

        selectedQuoteId = -1;

        etQuote.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
