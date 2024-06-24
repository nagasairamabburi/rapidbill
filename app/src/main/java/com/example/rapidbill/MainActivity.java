package com.example.rapidbill;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rapidbill.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText storeNameEditText;
    private static final String STORE_NAME_PREF = "store_name_pref";
    private boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private TextView barcodeTextView;
    private Button scanBarcodeButton;
    private Button checkout;
    private Button increase;
    private Button decrease;
    private TextView numberTextView;
    private int number = 0;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        menuIcon = findViewById(R.id.menuIcon);
        barcodeTextView = findViewById(R.id.barcode);
        numberTextView = findViewById(R.id.number);
        increase = findViewById(R.id.increase);
        decrease = findViewById(R.id.decrease);
        checkout = findViewById(R.id.checkout);
        storeNameEditText = findViewById(R.id.storeNameEditText);
        numberTextView.setText(String.valueOf(number));

        barcodeTextView.setVisibility(View.INVISIBLE);
        increase.setVisibility(View.INVISIBLE);
        decrease.setVisibility(View.INVISIBLE);
        checkout.setVisibility(View.INVISIBLE);
        numberTextView.setVisibility(View.INVISIBLE);

        clearScanResults();
        clearNumberData();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String storedStoreName = sharedPreferences.getString(STORE_NAME_PREF, "");
        if (!storedStoreName.isEmpty()) {
            // Set the stored store name in the EditText
            storeNameEditText.setText(storedStoreName);
            // Disable editing in EditText
            storeNameEditText.setEnabled(false);
            // Prevent focus on EditText
            storeNameEditText.setFocusable(false);
            // Prevent cursor from being visible
            storeNameEditText.setCursorVisible(false);
        }
        // Set click listeners for buttons
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseNumber();
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseNumber();
            }
        });

        if (menuIcon != null) {
            menuIcon.setOnClickListener(view -> {
                // Open the sidebar menu
                drawerLayout.openDrawer(Gravity.LEFT);
            });
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                handleMenuItemClick(item.getItemId());

                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            });
        }
        scanBarcodeButton.setOnClickListener(view -> {
            EditText storeNameEditText = findViewById(R.id.storeNameEditText);
            String storeName = storeNameEditText.getText().toString().trim();

            if (storeName.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter the Store name to Scan barcode", Toast.LENGTH_SHORT).show();
            } else {
                // Start barcode scanner
                scanCode();
            }
        });
    }
    private void increaseNumber() {
        number++;
        numberTextView.setText(String.valueOf(number));
    }

    private void decreaseNumber() {
        if (number > 0) {
            number--;
            numberTextView.setText(String.valueOf(number));
        }
    }
    private void handleMenuItemClick(int itemId) {
        if (itemId == R.id.home) {
            Toast.makeText(MainActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.account) {
            Toast.makeText(MainActivity.this, "Account Selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, menu_account.class);
            startActivity(intent);
        } else if (itemId == R.id.contact) {
            Toast.makeText(MainActivity.this, "Contact Us Selected", Toast.LENGTH_SHORT).show();
            String companyEmail = "ramabburi89@gmail.com";
            composeEmail(companyEmail, "Subject", "Hello, ");
        } else if (itemId == R.id.history) {
            Toast.makeText(MainActivity.this, "Billing History Selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, menu_history.class);
            startActivity(intent);
        } else if (itemId == R.id.compliment) {
            Toast.makeText(MainActivity.this, "Rate the App", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.share) {
            Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.logout) {
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

            // Open LoginActivity
            Intent intent = new Intent(MainActivity.this, com.example.rapidbill.Login.class);
            startActivity(intent);

            // Finish the current activity
            finish();
        }
    }
    private void composeEmail(String emailAddress, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        Intent chooser = Intent.createChooser(intent, "Choose an email client");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            // Handle the case where no email app is available
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }
    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to Flash on and down to Flash off");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            // Update the barcodeTextView with the scanned result and additional text
            String scanResult = result.getContents();
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

            // Check if the scanned product is already in the list
            List<String> scanResults = getScanResults();
            if (scanResults.contains(scanResult)) {
                // Product already scanned
                barcodeTextView.setText(scanResult + " - This product has been added to cart already ");
                increase.setVisibility(View.INVISIBLE);
                decrease.setVisibility(View.INVISIBLE);
                numberTextView.setVisibility(View.INVISIBLE);
            }
            else {
                String additionalText = " has been added to cart"; // Additional text you want to add
                String resultWithAdditionalText = scanResult + additionalText;
                barcodeTextView.setText(resultWithAdditionalText);

                int currentNumber = sharedPreferences.getInt(scanResult, 0);
                // Increment the number for the scanned product
                currentNumber++;
                // Save the updated number in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(scanResult, currentNumber);
                editor.apply();

                // Update the UI with the new number
                numberTextView.setText(String.valueOf(currentNumber));

                saveScanResult(scanResult);

                barcodeTextView.setVisibility(View.VISIBLE);
                increase.setVisibility(View.VISIBLE);
                decrease.setVisibility(View.VISIBLE);
                checkout.setVisibility(View.VISIBLE);
                numberTextView.setVisibility(View.VISIBLE);

                // Create an Intent to start the TableActivity
                //Intent intent = new Intent(MainActivity.this, TableActivity.class);

                // Put the scanned result as an extra in the Intent
                //intent.putExtra("SCAN_RESULT", scanResult);

                // Start the TableActivity
                //startActivity(intent);
            }
        }
    });
    // Method to save the scanned result in shared preferences
    private void saveScanResult(String scanResult) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        List<String> scanResults = getScanResults();
        scanResults.add(scanResult);
        String json = gson.toJson(scanResults);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SCAN_RESULTS", json);
        editor.apply();
    }
    protected void onStop() {
        super.onStop();

        // Save the store name in SharedPreferences when the activity is stopped
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORE_NAME_PREF, storeNameEditText.getText().toString());
        editor.apply();
    }
    // Method to retrieve the list of scanned results from shared preferences
    private List<String> getScanResults() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SCAN_RESULTS", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> scanResults = gson.fromJson(json, type);
        if (scanResults == null) {
            scanResults = new ArrayList<>();
        }
        return scanResults;
    }

    public void checkout(View view) {
        // Start the TableActivity
        Intent intent = new Intent(this, TableActivity.class);
        startActivity(intent);
    }
    private void clearScanResults() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("SCAN_RESULTS");
        editor.apply();
    }
    private void clearNumberData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This clears all data in the SharedPreferences
        editor.apply();
    }
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            // If the user presses back again within a short time, close the app
            super.onBackPressed();
            finishAffinity(); // Close all activities
        } else {
            // Inform the user to press back again to exit
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            // Reset the flag after a short time (e.g., 2 seconds)
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    },
                    2000
            );
        }
    }
}