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

    Context context;
    Bitmap bm;
    ImageView imgFish;
    TextView txtQuality;
    Button btnLowGraphicQuality;
    Button btnMediumGraphicQuality;
    Button btnHighGraphicQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        context = getApplicationContext();
        txtQuality = findViewById(R.id.txtCurrentQuality);
        btnLowGraphicQuality = findViewById(R.id.btnLowGraphicQuality);
        btnMediumGraphicQuality = findViewById(R.id.btnMediumGraphicQuality);
        btnHighGraphicQuality = findViewById(R.id.btnHighGraphicQuality);
        imgFish = findViewById(R.id.imgFishy);
        bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.fishy_bmp);
        bm = ImageManager.resize(bm, 128, 128);
        bm = ImageManager.setWhitePixelsToTransparent(bm);
        imgFish.setImageBitmap(bm);

        btnLowGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bm = ImageManager.decompress(context, R.raw.fishy);
                imgFish.setImageBitmap(bm);
                Constants.GRAPHIC_QUALITY = 0;
                txtQuality.setText("LOW");
                CharSequence text = "Graphics set to low";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        });

        btnMediumGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bm = ImageManager.decompress(context, R.raw.fishy);
                imgFish.setImageBitmap(bm);
                Constants.GRAPHIC_QUALITY = 1;
                txtQuality.setText("MEDIUM");
                CharSequence text = "Graphics set to medium";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        });

        btnHighGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bm = ImageManager.decompress(context, R.raw.fishy);
                imgFish.setImageBitmap(bm);
                Constants.GRAPHIC_QUALITY = 2;
                txtQuality.setText("HIGH");
                CharSequence text = "Graphics set to high";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        });
    }
}
