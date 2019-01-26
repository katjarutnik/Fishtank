package com.xd.akvarij;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity implements SensorEventListener {
    // inputs from main menu -> new -> generate
    boolean fresh;
    int population;
    int pickedPrimaryColor;
    int pickedSecondaryColor;
    boolean randomNewGame;
    // plays in background in menus
    VideoView videoView;
    // game view
    GameView gameView;
    FrameLayout gameFrame;
    ConstraintLayout gameOverlay;
    Button btnFeed;
    Button btnClean;
    Button btnStats;
    Button btnOptions;
    TextView txtDays;
    TextView txtInfoTop;
    TextView txtInfoMiddle;
    // stats recycler view
    ConstraintLayout gameOverlayStats;
    RecyclerView recyclerViewStats;
    FishAdapter fishAdapter;
    boolean statsOpen = false;
    public List<Fish> fishData = new ArrayList<>();
    // pause menu view
    ConstraintLayout gameLayoutPause;
    Button btnGodMode;
    Button btnSave;
    Button btnExit;
    // simulation tweaks view
    ConstraintLayout gameLayoutSimSettings;
    Button btnSimSettingsConfirm;
    // sensor stuff
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] gravity;
    private float[] linear_acceleration;
    int currentOrientation;
    // saving and loading
    public static final String myPrefs = "myTankPrefs";
    public static final String myPrefsKey = "tank";
    // other
    MyCallback myCallback;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // gets input from main menu -> new -> generate
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            population = extras.getInt("POPULATION");
            fresh = extras.getBoolean("FRESH");
            randomNewGame = extras.getBoolean("RANDOM");
            pickedPrimaryColor = extras.getInt("COLOR_PRIMARY");
            pickedSecondaryColor = extras.getInt("COLOR_SECONDARY");
        } else {
            // error
        }
        context = this;

        // game view
        gameView = new GameView(this);
        View rootView = getLayoutInflater().inflate(R.layout.activity_game, null, true);
        gameFrame = rootView.findViewById(R.id.gameFrame);
        gameOverlay = rootView.findViewById(R.id.gameOverlay);
        gameOverlay.setVisibility(View.VISIBLE);
        btnFeed = gameOverlay.findViewById(R.id.btnFeed);
        btnClean = gameOverlay.findViewById(R.id.btnClean);
        btnStats = gameOverlay.findViewById(R.id.btnStats);
        btnOptions = gameOverlay.findViewById(R.id.btnOptions);
        txtDays = gameOverlay.findViewById(R.id.txtDays);
        txtInfoTop = gameOverlay.findViewById(R.id.txtInfo);
        txtInfoMiddle = gameOverlay.findViewById(R.id.txtGameOverlayMiddle);

        // game stats
        gameOverlayStats = rootView.findViewById(R.id.gameOverlayStats);
        gameOverlayStats.setVisibility(View.GONE);
        recyclerViewStats = gameOverlayStats.findViewById(R.id.recyclerViewStats);

        // background video
        videoView = rootView.findViewById(R.id.myvideoview2);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fishtank_menu_new);
        videoView.setVideoURI(video);
        videoView.setVisibility(View.INVISIBLE);

        // game menu view
        gameLayoutPause = rootView.findViewById(R.id.gameOverlayPaused);
        gameLayoutPause.setVisibility(View.GONE);
        btnGodMode = gameLayoutPause.findViewById(R.id.btnGameOverlayPausedTweaks);
        btnSave = gameLayoutPause.findViewById(R.id.btnGameOverlayPausedSave);
        btnExit = gameLayoutPause.findViewById(R.id.btnGameOverlayPausedExit);

        // simulation tweaks view
        gameLayoutSimSettings = rootView.findViewById(R.id.gameOverlaySimSettings);
        gameLayoutSimSettings.setVisibility(View.GONE);
        btnSimSettingsConfirm = gameLayoutSimSettings.findViewById(R.id.btnGameOverlaySimSettingsConfirm);

        gameFrame.removeView(gameOverlay);
        gameFrame.removeView(gameOverlayStats);
        gameFrame.removeView(gameLayoutPause);
        gameFrame.removeView(gameLayoutSimSettings);
        gameFrame.addView(gameView);
        gameFrame.addView(gameOverlay);
        gameFrame.addView(gameOverlayStats);
        gameFrame.addView(gameLayoutPause);
        gameFrame.addView(gameLayoutSimSettings);

        // enables interaction from other classes
        myCallback = new MyCallback() {
            @Override
            public void updateTxtDays(final String myString) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtDays.setText(myString);
                    }
                });
            }
            @Override
            public void updateTxtInfoTop(final String myString) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtInfoTop.setText(myString);
                    }
                });
            }
            @Override
            public void updateTxtMiddle() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtInfoMiddle.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void updateAdapter() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fishData.addAll(gameView.tank.fish);
                        fishAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.tank.feedFish();
            }
        });
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.tank.cleanPoop();
            }
        });
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statsOpen) {
                    gameOverlayStats.setVisibility(View.VISIBLE);
                    statsOpen = true;
                } else {
                    gameOverlayStats.setVisibility(View.GONE);
                    statsOpen = false;
                }
            }
        });

        // open pause menu
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    gameView.thread.setRunning(false);
                    gameView.thread.join();
                    gameOverlay.setVisibility(View.GONE);
                    gameOverlayStats.setVisibility(View.GONE);
                    gameLayoutSimSettings.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                    gameLayoutPause.setVisibility(View.VISIBLE);
                    //saveData(context, myPrefs, myPrefsKey, gameView.tank);
                    //Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    //startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // simulation tweaks menu
        btnGodMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLayoutPause.setVisibility(View.GONE);
                gameLayoutSimSettings.setVisibility(View.VISIBLE);
            }
        });
        // simulation tweaks menu go back to pause menu
        btnSimSettingsConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLayoutSimSettings.setVisibility(View.GONE);
                gameLayoutPause.setVisibility(View.VISIBLE);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (videoView.getVisibility() == View.VISIBLE)
                    videoView.start();
            }
        });

        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linear_acceleration = new float[3];

        final OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == 90)
                    currentOrientation = orientation;
                if (orientation == 270)
                    currentOrientation = orientation;
            }
        };

        if (orientationEventListener.canDetectOrientation())
            orientationEventListener.enable();

        fishAdapter = new FishAdapter(fishData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewStats.setLayoutManager(mLayoutManager);
        recyclerViewStats.setItemAnimator(new DefaultItemAnimator());
        recyclerViewStats.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewStats.setAdapter(fishAdapter);

        loadData(population, this);

        setContentView(gameFrame);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            if (linear_acceleration[0] > 0.01 || linear_acceleration[0] < -0.01) {
                if (currentOrientation == 90)
                    gameView.tank.shakingStart(-event.values[1], -event.values[0]);
                else
                    gameView.tank.shakingStart(event.values[1], event.values[0]);

            } else
                gameView.tank.shakingStop();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void saveData(Context context, String preferenceFileName, String serializedObjectKey,
                         Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    private void loadData(int popSize, Context context) {
        if (fresh) {
            gameView.tank = new Tank(popSize, myCallback, context);
            if (randomNewGame)
                gameView.tank.generateRandomNew();
            else
                gameView.tank.generateCustomNew(pickedPrimaryColor, pickedSecondaryColor);
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(myPrefs, 0);
            if (sharedPreferences.contains(myPrefsKey)) {
                final Gson gson = new Gson();
                gameView.tank = gson.fromJson(sharedPreferences.getString(myPrefsKey, ""),
                        Tank.class);
                gameView.daytime = gameView.tank.dayTime;
                gameView.tank.myCallback = myCallback;
                txtDays.setText("DAY " + gameView.tank.dayCounter);
            } else {
                // DISPLAY ERROR
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (gameLayoutPause.getVisibility() == View.VISIBLE) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
            gameLayoutPause.setVisibility(View.GONE);
            gameView.thread = new MainThread(gameView.getHolder(), gameView);
            gameView.thread.setRunning(true);
            gameView.thread.start();
            gameOverlay.setVisibility(View.VISIBLE);
        } else if (gameLayoutSimSettings.getVisibility() == View.VISIBLE) {
            gameLayoutSimSettings.setVisibility(View.GONE);
            gameLayoutPause.setVisibility(View.VISIBLE);
        } else {
            // TODO save before exiting?
            super.onBackPressed();
        }
    }
}
