package com.xd.akvarij;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class SettingsActivity extends Activity {

    Context context;
    Bitmap bm;
    ImageView imgFish;
    TextView txtQuality;
    Button btnLowGraphicQuality;
    Button btnMediumGraphicQuality;
    Button btnHighGraphicQuality;
    VideoView videoView;

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
        videoView = findViewById(R.id.myvideoview);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fishtank_menu_new);
        videoView.setVideoURI(video);
        videoView.start();

        imgFish = findViewById(R.id.imgFishy);
        bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.fish_128);
        bm = ImageManager.resize(bm, 128, 128);
        imgFish.setImageBitmap(bm);

        videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        videoView.start();
                    }
                });


        btnLowGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_8);
                bm = ImageManager.resize(bm, 128, 128);
                imgFish.setImageBitmap(bm);
                Constants.GRAPHIC_QUALITY = 0;
                txtQuality.setText("LOW");
            }
        });

        btnMediumGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_16);
                bm = ImageManager.resize(bm, 128, 128);
                imgFish.setImageBitmap(bm);
                Constants.GRAPHIC_QUALITY = 1;
                txtQuality.setText("MEDIUM");
            }
        });

        btnHighGraphicQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_128);
                bm = ImageManager.resize(bm, 128, 128);
                imgFish.setImageBitmap(bm);
                Constants.GRAPHIC_QUALITY = 2;
                txtQuality.setText("HIGH");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }
}
