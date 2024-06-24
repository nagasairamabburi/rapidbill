package com.example.rapidbill;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends Activity {

    private LinearLayout layoutCardDetails;
    private LinearLayout layoutUpiOptions;
    private EditText editTextCardNumber;
    private EditText editTextCardholderName;
    private EditText editTextExpiryDate;
    private EditText editTextCVV;
    private Button buttonSubmitCardDetails;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        TextView textViewPaymentHeading = findViewById(R.id.textViewPaymentHeading);
        LinearLayout layoutPaymentOptions = findViewById(R.id.layoutPaymentOptions);
        layoutCardDetails = findViewById(R.id.layoutCardDetails);
        layoutUpiOptions = findViewById(R.id.layoutUpiOptions);
        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextCardholderName = findViewById(R.id.editTextCardholderName);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextCVV = findViewById(R.id.editTextCVV);
        buttonSubmitCardDetails = findViewById(R.id.buttonSubmitCardDetails);

        // Set up click listeners for payment options buttons
        Button buttonUPI = findViewById(R.id.buttonUPI);
        Button buttonCard = findViewById(R.id.buttonCard);
        Button buttonNetbanking = findViewById(R.id.buttonNetbanking);
        Button buttonCashOnDelivery = findViewById(R.id.buttonCashOnDelivery);

        buttonUPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show UPI options layout
                layoutUpiOptions.setVisibility(View.VISIBLE);
                // Hide card details layout
                layoutCardDetails.setVisibility(View.GONE);
            }
        });

        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show card details layout
                layoutCardDetails.setVisibility(View.VISIBLE);
                // Hide UPI options layout
                layoutUpiOptions.setVisibility(View.GONE);
            }
        });

        buttonNetbanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle netbanking payment
                // Implement your logic here
            }
        });

        buttonCashOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cash on delivery payment
                // Implement your logic here
            }
        });

        // Set up click listener for submitting card details
        buttonSubmitCardDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve card details from EditText fields
                String cardNumber = editTextCardNumber.getText().toString();
                String cardholderName = editTextCardholderName.getText().toString();
                String expiryDate = editTextExpiryDate.getText().toString();
                String cvv = editTextCVV.getText().toString();

                // Validate card details
                if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(cardholderName) || TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(cvv)) {
                    // Display a toast message indicating that all fields are required
                    Toast.makeText(PaymentActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    // Display a toast message indicating successful submission
                    Toast.makeText(PaymentActivity.this, "Card details submitted", Toast.LENGTH_SHORT).show();

                    // Open the app associated with the UPI payment
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    // Replace "com.google.android.apps.nbu.paisa.user" with the package name of Google Pay app
                    intent.setPackage("com.google.android.apps.nbu.paisa.user");
                    startActivity(intent);
                }
            }
        });

        // Set up click listener for Google Pay button
        LinearLayout layoutGPay = findViewById(R.id.GPay);
        layoutGPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Google Pay app
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.nbu.paisa.user");
                startActivity(intent);
            }
        });

        // Set up click listener for PhonePe button
        LinearLayout layoutPhonePay = findViewById(R.id.PhonePay);
        layoutPhonePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open PhonePe app
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.phonepe.app");
                startActivity(intent);
            }
        });

        // Set up click listener for Paytm button
        LinearLayout layoutPaytm = findViewById(R.id.Paytm);
        layoutPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Paytm app
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("net.one97.paytm");
                startActivity(intent);
            }
        });
    }
}