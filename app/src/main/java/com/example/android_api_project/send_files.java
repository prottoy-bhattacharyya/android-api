package com.example.android_api_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.concurrent.atomic.AtomicBoolean;

public class send_files extends AppCompatActivity {
    Button send_btn, scan_btn;
    TextView test_data;
    String rawValue;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_files);

        test_data = findViewById(R.id.test_data);
        send_btn = findViewById(R.id.send_btn);
        scan_btn = findViewById(R.id.scan_btn);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qr_scanner();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rawValue == null){
                    test_data.setText("Please scan the QR code first");
                    return;
                }
                share_qr_text();
            }
        });
    }
    void qr_scanner() {
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC
                ).enableAutoZoom()
                .build();

        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this);

        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            //complete
                            vibrator.vibrate(100);
                            rawValue = barcode.getRawValue();
                            test_data.setText(rawValue);
                        })
                .addOnCanceledListener(
                        () -> {
                            test_data.setText("canceled");
                            rawValue = null;
                        })
                .addOnFailureListener(
                        e -> {
                            test_data.setText("failed");
                            rawValue = null;
                        });
    }

    void share_qr_text(){
        Intent share_intent = new Intent(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_TEXT, rawValue);
        startActivity(Intent.createChooser(share_intent, "Share QR Code"));
    }
}