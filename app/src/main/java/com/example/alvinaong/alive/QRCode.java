package com.example.alvinaong.alive;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCode extends AppCompatActivity {
    FirebaseUser user;
    String uid;
    ImageView qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display_vol);
        qrcode = (ImageView) findViewById(R.id.gen_img);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
        } catch(WriterException e) {
            e.printStackTrace();
        }
    }
}
