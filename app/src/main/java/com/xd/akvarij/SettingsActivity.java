package com.xd.akvarij;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    Bitmap img;
    ImageDecompression dcmpr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        dcmpr = new ImageDecompression();

        final ImageView imgBackgroundView = findViewById(R.id.imgBackground);
        img = BitmapFactory.decodeResource(this.getResources(), R.drawable.bikini_bottom_day);
        imgBackgroundView.setImageBitmap(img);

        final TextView txtQuality = findViewById(R.id.txtCurrentQuality);

        final Button btnLowGraphicQuality = findViewById(R.id.btnLowGraphicQuality);
        final Button btnMediumGraphicQuality = findViewById(R.id.btnMediumGraphicQuality);
        final Button btnHighGraphicQuality = findViewById(R.id.btnHighGraphicQuality);

        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        btnLowGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //byte[] binaryFile = dcmpr.readBinaryFile(R.raw.binary_img_low);
                //img = dcmpr.decompress(binaryFile);
                //imgBackgroundView.setImageBitmap(img);
                Constants.GRAPHIC_QUALITY = 0;
                txtQuality.setText("LOW");
                CharSequence text = "Graphics set to low";
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        });

        btnMediumGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //byte[] binaryFile = readBinaryFile(R.raw.binary_img_medium);
                //img = decompress(binaryFile);
                //imgBackgroundView.setImageBitmap(img);
                Constants.GRAPHIC_QUALITY = 1;
                txtQuality.setText("MEDIUM");
                CharSequence text = "Graphics set to medium";
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        });

        btnHighGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //byte[] binaryFile = readBinaryFile(R.raw.binary_img_high);
                //img = decompress(binaryFile);
                //imgBackgroundView.setImageBitmap(img);
                Constants.GRAPHIC_QUALITY = 2;
                txtQuality.setText("HIGH");
                CharSequence text = "Graphics set to high";
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        });
    }
}
