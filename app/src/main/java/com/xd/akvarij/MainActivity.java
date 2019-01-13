package com.xd.akvarij;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    public int population;
    public boolean fresh;

    Button btnGenerateNew;
    Button btnLoad;
    Button btnSettings;

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        btnGenerateNew = findViewById(R.id.btnGenerate);
        btnLoad = findViewById(R.id.btnLoad);
        btnSettings = findViewById(R.id.btnSettings);

        MediaPlayer player = MediaPlayer.create(this, R.raw.sound);
        player.setLooping(true);
        player.start();

        btnGenerateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewButtonClick(v);
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fresh = false;
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("FRESH", fresh);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (btnGenerateNew.getVisibility() == View.GONE) {
            btnGenerateNew.setVisibility(View.VISIBLE);
            btnLoad.setVisibility(View.VISIBLE);
            btnSettings.setVisibility(View.VISIBLE);
        }
    }

    private void onNewButtonClick(View v) {
        btnGenerateNew.setVisibility(View.GONE);
        btnLoad.setVisibility(View.GONE);
        btnSettings.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.dialog_new, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOutsideTouchable(true);

        RelativeLayout relativeLayout = popupView.findViewById(R.id.relativeLayout);
        final EditText txtPopSize = popupView.findViewById(R.id.txtPopSize);
        final Button btnGenerate = popupView.findViewById(R.id.btnGenerate);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fresh = true;
                btnGenerate.setSelected(true);
                popupWindow.dismiss();
                population = !txtPopSize.getText().toString().equals("") ?
                        Integer.valueOf(txtPopSize.getText().toString()) : 10;
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("POPULATION", population);
                intent.putExtra("FRESH", fresh);
                startActivity(intent);
            }
        });
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        relativeLayout.setFocusableInTouchMode(true);
        relativeLayout.requestFocus();
        relativeLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(!btnGenerate.isSelected()) {
                    btnGenerateNew.setVisibility(View.VISIBLE);
                    btnLoad.setVisibility(View.VISIBLE);
                    btnSettings.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
