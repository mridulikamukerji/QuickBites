package com.example.quickbites;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {

    private MaterialButton btnCustomer, btnCaterer;
    private LinearLayout customerFields, catererFields;
    private ImageView imagePreview;
    private Button btnSelectPhoto, btnRegister;
    private Uri selectedImageUri;

    private EditText customerName, customerEmail, customerPhone, customerPassword;
    private EditText catererName, catererEmail, catererPhone, catererPassword, catererBusinessName, catererBusinessAddress;

    private InlineDatabaseHelper inlineDbHelper;

    private static final int PICK_IMAGE_REQUEST = 1;

    private boolean isCustomerSelected = true; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inlineDbHelper = new InlineDatabaseHelper(this);

        // Initialize Views
        btnCustomer = findViewById(R.id.btnCustomer);
        btnCaterer = findViewById(R.id.btnCaterer);
        customerFields = findViewById(R.id.customerFields);
        catererFields = findViewById(R.id.catererFields);
        imagePreview = findViewById(R.id.imagePreview);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnRegister = findViewById(R.id.btnRegister);

        // Customer Inputs
        customerName = findViewById(R.id.customerName);
        customerEmail = findViewById(R.id.customerEmail);
        customerPhone = findViewById(R.id.customerPhone);
        customerPassword = findViewById(R.id.customerPassword);

        // Caterer Inputs
        catererName = findViewById(R.id.catererName);
        catererEmail = findViewById(R.id.catererEmail);
        catererPhone = findViewById(R.id.catererPhone);
        catererPassword = findViewById(R.id.catererPassword);
        catererBusinessName = findViewById(R.id.catererBusinessName);
        catererBusinessAddress = findViewById(R.id.catererBusinessAddress);

        // Toggle logic
        btnCustomer.setOnClickListener(v -> toggleUserType(true));
        btnCaterer.setOnClickListener(v -> toggleUserType(false));

        btnSelectPhoto.setOnClickListener(v -> selectImage());

        btnRegister.setOnClickListener(v -> handleSignUp());

        toggleUserType(true); // Set default toggle
    }

    private void toggleUserType(boolean isCustomer) {
        isCustomerSelected = isCustomer;
        btnCustomer.setEnabled(!isCustomer);
        btnCaterer.setEnabled(isCustomer);
        customerFields.setVisibility(isCustomer ? View.VISIBLE : View.GONE);
        catererFields.setVisibility(isCustomer ? View.GONE : View.VISIBLE);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
        }
    }

    private void handleSignUp() {
        if (isCustomerSelected) {
            String name = customerName.getText().toString().trim();
            String email = customerEmail.getText().toString().trim();
            String phone = customerPhone.getText().toString().trim();
            String password = customerPassword.getText().toString().trim();

            if (!email.endsWith("@nmims.in") && !email.endsWith("@nmims.edu")) {
                Toast.makeText(this, "Customer email must end with @nmims.in or @nmims.edu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateCommonFields(name, email, phone, password)) return;

            long id = insertCustomer(name, email, phone, password, selectedImageUri != null ? selectedImageUri.toString() : null);
            if (id != -1) {
                Toast.makeText(this, "Customer registered successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.putExtra("userType", "customer");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            }

        } else {
            String name = catererName.getText().toString().trim();
            String email = catererEmail.getText().toString().trim();
            String phone = catererPhone.getText().toString().trim();
            String password = catererPassword.getText().toString().trim();
            String businessName = catererBusinessName.getText().toString().trim();
            String businessAddress = catererBusinessAddress.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateCommonFields(name, email, phone, password)) return;

            long id = insertCaterer(name, email, phone, password, businessName, businessAddress, selectedImageUri != null ? selectedImageUri.toString() : null);
            if (id != -1) {
                Toast.makeText(this, "Caterer registered successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.putExtra("userType", "caterer");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateCommonFields(String name, String email, String phone, String password) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clearFields() {
        customerName.setText("");
        customerEmail.setText("");
        customerPhone.setText("");
        customerPassword.setText("");
        catererName.setText("");
        catererEmail.setText("");
        catererPhone.setText("");
        catererPassword.setText("");
        catererBusinessName.setText("");
        catererBusinessAddress.setText("");
        imagePreview.setImageResource(R.drawable.default_profile_picture);
        selectedImageUri = null;
    }

    // ================== SQLite Code ===================

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

    private long insertCustomer(String name, String email, String phone, String password, String imageUri) {
        SQLiteDatabase db = inlineDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("password", password);
        values.put("imageUri", imageUri);

        long id = db.insert("customers", null, values);
        db.close();
        return id;
    }

    private long insertCaterer(String name, String email, String phone, String password, String businessName, String businessAddress, String imageUri) {
        SQLiteDatabase db = inlineDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("password", password);
        values.put("businessName", businessName);
        values.put("businessAddress", businessAddress);
        values.put("imageUri", imageUri);

        long id = db.insert("caterers", null, values);
        db.close();
        return id;
    }
}
