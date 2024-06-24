package com.example.rapidbill;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {

    Button madd, mpay;
    TableLayout tableLayout;
    int serialNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        madd = findViewById(R.id.add);
        mpay = findViewById(R.id.pay);
        tableLayout = findViewById(R.id.table);


        mpay.setOnClickListener(v -> {
            Intent intent = new Intent(TableActivity.this, PaymentActivity.class);
            startActivity(intent);
        });
        madd.setOnClickListener(v -> {
            Intent intent = new Intent(TableActivity.this, MainActivity.class);
            startActivity(intent);
        });

        List<String> scanResults = getScanResults();
        if (scanResults != null && !scanResults.isEmpty()) {
            // Add each scanned result to the table
            for (String scanResult : scanResults) {
                addProductToTable(scanResult);
            }
        }
    }
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

    private void addProductToTable(String productName) {
        // Create a new row
        TableRow newRow = new TableRow(this);

        // Set padding to create a gap between rows
        int paddingInDp = 8; // Adjust as needed
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (paddingInDp * scale + 0.5f);
        newRow.setPadding(0, paddingInPx, 0, paddingInPx);

        // Add serial number to the row
        TextView serialNumberTextView = createTextView(String.valueOf(serialNumber++), 2,30, Gravity.CENTER);
        newRow.addView(serialNumberTextView);

        // Add product name to the row
        TextView productNameTextView = createTextView(productName, 5,30, Gravity.CENTER);
        newRow.addView(productNameTextView);

        // Add EditText for Quantity
        TextView quantityTextView = createTextView("40", 1.8,30, Gravity.CENTER);
        newRow.addView(quantityTextView);

        // Add buttons for MRP with "+" and "-" functionality
        TableRow mrpRow = new TableRow(this);
        Button decreaseButton = createButton("-", 30,30,0, Gravity.CENTER);
        TextView mrpTextView = createTextView("1", 1.0,30, Gravity.CENTER);
        Button increaseButton = createButton("+", 30,30,0, Gravity.CENTER);
        decreaseButton.setOnClickListener(view -> {
            int currentMRP = Integer.parseInt(mrpTextView.getText().toString());
            if (currentMRP > 0) {
                mrpTextView.setText(String.valueOf(currentMRP - 1));
                updateAmount(newRow);
            }
        });
        increaseButton.setOnClickListener(view -> {
            int currentMRP = Integer.parseInt(mrpTextView.getText().toString());
            mrpTextView.setText(String.valueOf(currentMRP + 1));
            updateAmount(newRow);
        });
        mrpRow.addView(decreaseButton);
        mrpRow.addView(mrpTextView);
        mrpRow.addView(increaseButton);
        newRow.addView(mrpRow);

        // Add TextView for Amount
        TextView amountTextView = createTextView("40", 2.6,300, Gravity.CENTER);
        newRow.addView(amountTextView);

        // Add the new row to the table
        tableLayout.addView(newRow);
    }

    private Button createButton(String text, int widthInDp, int heightInDp, double weight, int gravity) {
        Button button = new Button(this);
        button.setText(text);
        button.setLayoutParams(new TableRow.LayoutParams(convertDpToPx(widthInDp), convertDpToPx(heightInDp), (float) weight));
        button.setGravity(gravity);
        button.setPadding(0, 0, 0, 0); // Adjust padding as needed
        return button;
    }

    // Method to convert dp to pixels
    private int convertDpToPx(int dp){
        return (int) (dp * getResources().getDisplayMetrics().density);
    }


    private TextView createTextView(String text, double weight, int heightInDp, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) weight));
        textView.setGravity(gravity);
        textView.setTextColor(getResources().getColor(R.color.black)); // Change to your desired color
        textView.setTextSize(14); // Change to your desired text size
        return textView;
    }

    private void updateAmount(TableRow row) {
        TextView quantityTextView = (TextView) row.getChildAt(2);
        TextView mrpTextView = (TextView) ((TableRow)row.getChildAt(3)).getChildAt(1);
        TextView amountTextView = (TextView) row.getChildAt(4);

        try {
            double quantity = Double.parseDouble(quantityTextView.getText().toString());
            double mrp = Double.parseDouble(mrpTextView.getText().toString());

            double amount = quantity * mrp;
            amountTextView.setText(String.valueOf(amount));
        } catch (NumberFormatException e) {
            // Handle the case where input is not a valid number
            amountTextView.setText("Error");
        }
    }
}