package com.xd.akvarij;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

public class MainActivity extends Activity {
    // main menu
    VideoView videoView;
    LinearLayout layoutMainMenu;
    Button btnNew, btnLoad, btnSettings;
    // new game generator
    ConstraintLayout layoutNewGameMenu;
    RadioGroup radioGroup;
    LinearLayout linearLayoutColorPicker;
    EditText txtPopSize;
    ColorSeekBar colorSeekBar1, colorSeekBar2;
    Button btnGenerate, btnSimDetails;
    // simulation details
    ConstraintLayout layoutSimDetails;
    Button btnSimDetailsConfirm;
    TextView txtSettingDays, txtSettingAgeInfant, txtSettingAgeTeen, txtSettingAgeAdult,
            txtSettingAgeElder, txtSettingSpeedH, txtSettingSpeedV, txtSettingPregnancyDays,
            txtSettingPregnancyChance, txtSettingTwinsChance, txtSettingMutationChance;
    // new game parameters
    public int popSize = 10;
    int pickedPrimaryColor = 0;
    int pickedSecondaryColor = 0;
    boolean alreadyPlayed = false;
    boolean randomNewGame = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        // background
        videoView = findViewById(R.id.myvideoview);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fishtank_menu_new);
        videoView.setVideoURI(video);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
        });
        // main menu blocks
        layoutMainMenu = findViewById(R.id.layoutMainMenu);
        btnNew = findViewById(R.id.btnNew);
        btnLoad = findViewById(R.id.btnLoad);
        btnSettings = findViewById(R.id.btnSettings);
        // new game generator blocks
        layoutNewGameMenu = findViewById(R.id.layoutNewGameMenu);
        radioGroup = findViewById(R.id.radioGroup);
        linearLayoutColorPicker = findViewById(R.id.layoutCustomizeFishColor);
        colorSeekBar1 = findViewById(R.id.colorSliderPrimary);
        colorSeekBar2 = findViewById(R.id.colorSliderSecondary);
        txtPopSize = findViewById(R.id.txtPopSize);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnSimDetails = findViewById(R.id.btnSimDetails);
        // simulation details blocks
        layoutSimDetails = findViewById(R.id.layoutSimDetails);
        btnSimDetailsConfirm = findViewById(R.id.btnSimDetailsConfirm);
        txtSettingDays = findViewById(R.id.txtEditDayLength);
        txtSettingAgeInfant = findViewById(R.id.txtEditMaxInfantAge);
        txtSettingAgeTeen = findViewById(R.id.txtEditMaxTeenAge);
        txtSettingAgeAdult = findViewById(R.id.txtEditMaxAdultAge);
        txtSettingAgeElder = findViewById(R.id.txtEditMaxElderAge);
        txtSettingSpeedH = findViewById(R.id.txtEditMaxHorizontalSpeed);
        txtSettingSpeedV = findViewById(R.id.txtEditMaxVerticalSpeed);
        txtSettingPregnancyDays = findViewById(R.id.txtEditBreeding);
        txtSettingPregnancyChance = findViewById(R.id.txtEditBreeding1);
        txtSettingTwinsChance = findViewById(R.id.txtEditBreeding2);
        txtSettingMutationChance = findViewById(R.id.txtEditBreeding3);
        txtSettingDays.setText(Integer.toString(Constants.DAY_LENGTH_IN_SECONDS));
        txtSettingAgeInfant.setText(Integer.toString(Constants.AGE_MAX_INFANT));
        txtSettingAgeTeen.setText(Integer.toString(Constants.AGE_MAX_TEEN));
        txtSettingAgeAdult.setText(Integer.toString(Constants.AGE_MAX_ADULT));
        txtSettingAgeElder.setText(Integer.toString(Constants.AGE_MAX));
        txtSettingSpeedH.setText(Integer.toString(Constants.MAX_HORIZONTAL_SPEED));
        txtSettingSpeedV.setText(Integer.toString(Constants.MAX_VERTICAL_SPEED));
        txtSettingPregnancyDays.setText(Integer.toString(Constants.PREGNANCY_DAYS));
        txtSettingPregnancyChance.setText(Integer.toString(Constants.PREGNANCY_CHANCE));
        txtSettingTwinsChance.setText(Integer.toString(Constants.PREGNANCY_TWINS_CHANCE));
        txtSettingMutationChance.setText(Integer.toString(Constants.MUTATION_CHANCE));
        // main menu fun
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMainMenu.setVisibility(View.GONE);
                layoutNewGameMenu.setAlpha(0.0f);
                layoutNewGameMenu.setVisibility(View.VISIBLE);
                layoutNewGameMenu.animate().alpha(1.0f);
            }
        });
        btnLoad.setEnabled(false);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("FRESH", false);
                startActivity(intent);
            }
        });
        btnSettings.setEnabled(false);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        // new game generator fun
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioBtnRandom) {
                    linearLayoutColorPicker.setVisibility(View.GONE);
                    randomNewGame = true;
                }
                if (checkedId == R.id.radioBtnCustom) {
                    linearLayoutColorPicker.setVisibility(View.VISIBLE);
                    randomNewGame = false;
                }
            }
        });
        colorSeekBar1.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                pickedPrimaryColor = color;
            }
        });
        colorSeekBar2.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                pickedSecondaryColor = color;
            }
        });
        btnSimDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutNewGameMenu.setVisibility(View.GONE);
                layoutSimDetails.setAlpha(0.0f);
                layoutSimDetails.setVisibility(View.VISIBLE);
                layoutSimDetails.animate().alpha(1.0f);
            }
        });
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alreadyPlayed = true;
                popSize = !txtPopSize.getText().toString().equals("") ?
                        Integer.valueOf(txtPopSize.getText().toString()) : popSize;
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("POPULATION", popSize);
                intent.putExtra("FRESH", true);
                intent.putExtra("RANDOM", randomNewGame);
                intent.putExtra("COLOR_PRIMARY", pickedPrimaryColor);
                intent.putExtra("COLOR_SECONDARY", pickedSecondaryColor);
                startActivity(intent);
            }
        });
        // sim details fun
        btnSimDetailsConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.DAY_LENGTH_IN_SECONDS = Integer.parseInt(txtSettingDays.getText().toString());
                Constants.AGE_MAX_INFANT = Integer.parseInt(txtSettingAgeInfant.getText().toString());
                Constants.AGE_MAX_TEEN = Integer.parseInt(txtSettingAgeTeen.getText().toString());
                Constants.AGE_MAX_ADULT = Integer.parseInt(txtSettingAgeAdult.getText().toString());
                Constants.AGE_MAX = Integer.parseInt(txtSettingAgeElder.getText().toString());
                Constants.MAX_HORIZONTAL_SPEED = Integer.parseInt(txtSettingSpeedH.getText().toString());
                Constants.MAX_VERTICAL_SPEED = Integer.parseInt(txtSettingSpeedV.getText().toString());
                Constants.PREGNANCY_DAYS = Integer.parseInt(txtSettingPregnancyDays.getText().toString());
                Constants.PREGNANCY_TWINS_CHANCE = Integer.parseInt(txtSettingTwinsChance.getText().toString());
                Constants.MUTATION_CHANCE = Integer.parseInt(txtSettingMutationChance.getText().toString());
                layoutSimDetails.setVisibility(View.GONE);
                layoutNewGameMenu.setAlpha(0.0f);
                layoutNewGameMenu.setVisibility(View.VISIBLE);
                layoutNewGameMenu.animate().alpha(1.0f);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
        if (alreadyPlayed && layoutNewGameMenu.getVisibility() == View.VISIBLE) {
            layoutNewGameMenu.setVisibility(View.GONE);
            layoutMainMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutNewGameMenu.getVisibility() == View.VISIBLE) {
            layoutNewGameMenu.setVisibility(View.GONE);
            layoutMainMenu.setVisibility(View.VISIBLE);
        } else if (layoutSimDetails.getVisibility() == View.VISIBLE) {
            layoutSimDetails.setVisibility(View.GONE);
            layoutNewGameMenu.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }
}
