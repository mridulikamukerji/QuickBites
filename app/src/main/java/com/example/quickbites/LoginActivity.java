package com.example.quickbites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class LoginActivity extends AppCompatActivity {

    EditText enterEmail, enterPassword;
    Button btnLogin, btnSignup;
    MaterialButton btnCustomer, btnCaterer;
    MaterialButtonToggleGroup toggleGroup;

    boolean isCustomer = true; // default selection
    InlineDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        enterEmail = findViewById(R.id.enterEmail);
        enterPassword = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnCustomer = findViewById(R.id.btnCustomer);
        btnCaterer = findViewById(R.id.btnCaterer);
        toggleGroup = findViewById(R.id.customerCatererToggle);

        dbHelper = new InlineDatabaseHelper(this);

        // Toggle button logic
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnCustomer) {
                    isCustomer = true;
                } else if (checkedId == R.id.btnCaterer) {
                    isCustomer = false;
                }
            }
        });

        // Default selection
        toggleGroup.check(R.id.btnCustomer);

        btnLogin.setOnClickListener(v -> {
            String email = enterEmail.getText().toString().trim();
            String password = enterPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isCustomer) {
                if (isValidCustomer(email, password)) {
                    Toast.makeText(this, "Customer Login Successful!", Toast.LENGTH_SHORT).show();
                    saveLoggedInUser(email);
                    startActivity(new Intent(LoginActivity.this, CustomerDashboard.class));
                } else {
                    Toast.makeText(this, "Invalid customer credentials!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isValidCaterer(email, password)) {
                    Toast.makeText(this, "Caterer Login Successful!", Toast.LENGTH_SHORT).show();
                    saveLoggedInUser(email);
                    startActivity(new Intent(LoginActivity.this, CatererDashboard.class));
                } else {
                    Toast.makeText(this, "Invalid caterer credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void saveLoggedInUser(String email) {
        SharedPreferences prefs = getSharedPreferences("QuickBitesPrefs", MODE_PRIVATE);
        prefs.edit().putString("loggedInUserEmail", email).apply();
    }

    // ========== Inline SQLite Database ==========
    private class InlineDatabaseHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "QuickBitesDB";
        private static final int DB_VERSION = 1;

        public InlineDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "phone TEXT," +
                    "password TEXT," +
                    "imageUri TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS caterers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "phone TEXT," +
                    "password TEXT," +
                    "businessName TEXT," +
                    "businessAddress TEXT," +
                    "imageUri TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS customers");
            db.execSQL("DROP TABLE IF EXISTS caterers");
            onCreate(db);
        }
    }

    // ========== Validation Functions ==========

    private boolean isValidCustomer(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("customers",
                null,
                "email=? AND password=?",
                new String[]{email, password},
                null, null, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    private boolean isValidCaterer(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("caterers",
                null,
                "email=? AND password=?",
                new String[]{email, password},
                null, null, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }
}
