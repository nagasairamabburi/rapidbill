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
                layoutUpiOptions.setVisibility(View.VISIBLE);
                layoutCardDetails.setVisibility(View.GONE);
            }
        });

        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutCardDetails.setVisibility(View.VISIBLE);
                layoutUpiOptions.setVisibility(View.GONE);
            }
        });

        buttonNetbanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonCashOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        buttonSubmitCardDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumber = editTextCardNumber.getText().toString();
                String cardholderName = editTextCardholderName.getText().toString();
                String expiryDate = editTextExpiryDate.getText().toString();
                String cvv = editTextCVV.getText().toString();

                if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(cardholderName) || TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(cvv)) {
                    Toast.makeText(PaymentActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Card details submitted", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.google.android.apps.nbu.paisa.user");
                    startActivity(intent);
                }
            }
        });

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

        LinearLayout layoutPhonePay = findViewById(R.id.PhonePay);
        layoutPhonePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.phonepe.app");
                startActivity(intent);
            }
        });

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