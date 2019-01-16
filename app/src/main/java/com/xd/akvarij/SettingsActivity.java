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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);



        final ImageView imgFishy = findViewById(R.id.imgFishy);
        Bitmap img = BitmapFactory.decodeResource(this.getResources(), R.drawable.fishy_bmp);
        img = ImageManipulator.resize(img, 128, 128);
        img = ImageManipulator.setTransparentBackground(img);

        imgFishy.setImageBitmap(img);

        final TextView txtQuality = findViewById(R.id.txtCurrentQuality);

        final Button btnLowGraphicQuality = findViewById(R.id.btnLowGraphicQuality);
        final Button btnMediumGraphicQuality = findViewById(R.id.btnMediumGraphicQuality);
        final Button btnHighGraphicQuality = findViewById(R.id.btnHighGraphicQuality);

        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        btnLowGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] binaryFile = DecompressImage.readBinaryFile(context,
                        R.raw.fishy);
                //img = DecompressImage.decompress(binaryFile);
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
                byte[] binaryFile = DecompressImage.readBinaryFile(context,
                        R.raw.fishy);
                //img = DecompressImage.decompress(binaryFile);
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
                byte[] binaryFile = DecompressImage.readBinaryFile(context,
                        R.raw.fishy);
                //img = DecompressImage.decompress(binaryFile);
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
