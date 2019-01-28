package com.xd.akvarij;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.LinearLayout;
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
    // stats
    ConstraintLayout gameOverlayStats;
    LinearLayout gameOverlayStatsSummary;
    RecyclerView recyclerViewStats;
    FishAdapter fishAdapter;
    boolean statsOpen = false;
    public List<Fish> fishData = new ArrayList<>();
    TextView txtCounterStartingPopulation;
    TextView txtCounterFishAlive;
    TextView txtCounterFishDied;
    TextView txtCounterFishBabies;
    TextView txtCounterFishFeeding;
    TextView txtCounterTankCleaning;
    Button btnStatsSummary;
    Button btnStatsFish;
    // pause menu view
    ConstraintLayout gameLayoutPause;
    Button btnSimSettings;
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
        gameView = new GameView(this);
        View rootView = getLayoutInflater().inflate(R.layout.activity_game, null, true);
        // background video
        videoView = rootView.findViewById(R.id.myvideoview2);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fishtank_menu_new);
        videoView.setVideoURI(video);
        videoView.setVisibility(View.INVISIBLE);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (videoView.getVisibility() == View.VISIBLE)
                    videoView.start();
            }
        });
        // main game view
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
        gameOverlayStatsSummary = gameOverlayStats.findViewById(R.id.layoutGameOverlayStatsSummary);
        recyclerViewStats = gameOverlayStats.findViewById(R.id.recyclerViewStats);
        btnStatsSummary = gameOverlayStats.findViewById(R.id.btnGameOverlayStatsOverall);
        btnStatsFish = gameOverlayStats.findViewById(R.id.btnGameOverlayStatsFish);
        // game stats summary
        txtCounterStartingPopulation = gameOverlayStats.findViewById(R.id.textView14);
        txtCounterFishAlive = gameOverlayStats.findViewById(R.id.textView15);
        txtCounterFishDied = gameOverlayStats.findViewById(R.id.textView16);
        txtCounterFishBabies = gameOverlayStats.findViewById(R.id.textView17);
        txtCounterFishFeeding = gameOverlayStats.findViewById(R.id.textView19);
        txtCounterTankCleaning = gameOverlayStats.findViewById(R.id.textView20);
        // game menu view
        gameLayoutPause = rootView.findViewById(R.id.gameOverlayPaused);
        gameLayoutPause.setVisibility(View.GONE);
        btnSimSettings = gameLayoutPause.findViewById(R.id.btnGameOverlayPausedTweaks);
        btnSave = gameLayoutPause.findViewById(R.id.btnGameOverlayPausedSave);
        btnExit = gameLayoutPause.findViewById(R.id.btnGameOverlayPausedExit);
        // simulation tweaks view
        gameLayoutSimSettings = rootView.findViewById(R.id.gameOverlaySimSettings);
        gameLayoutSimSettings.setVisibility(View.GONE);
        btnSimSettingsConfirm = gameLayoutSimSettings.findViewById(R.id.btnGameOverlaySimSettingsConfirm);
        // set up the views
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
            // main game overlay
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
            // stats game overlay
            @Override
            public void updateAdapter() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fishData.clear();
                        fishData.addAll(gameView.tank.fish);
                        fishAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void statsUpdateStartingPopulation(final int counter) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCounterStartingPopulation.setText("Your starting population was the size of " + counter + ".");
                    }
                });
            }
            @Override
            public void statsUpdateCurrentlyAlive() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.tank.countFishAlive = gameView.tank.countAlive();
                        txtCounterFishAlive.setText("There are currently " + gameView.tank.countFishAlive +" fish swimming around in your tank.");
                    }
                });
            }
            @Override
            public void statsUpdateFishDeaths() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.tank.countFishDeaths++;
                        txtCounterFishDied.setText(gameView.tank.countFishDeaths + " fish have given up on their life.");
                    }
                });
            }
            @Override
            public void statsUpdateFishOffspring() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.tank.countFishBabies++;
                        txtCounterFishBabies.setText(gameView.tank.countFishBabies + " fish babies have been spawned by your fish.");
                    }
                });
            }
            @Override
            public void statsUpdateGenerationReached(int counter) {
                // TODO
            }
        };
        // button feed fish
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameView.thread.isAlive()) {
                    gameView.tank.feedFish();
                    gameView.tank.countFishFeeding++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtCounterFishFeeding.setText("You fed your fish " + gameView.tank.countFishFeeding + " times.");
                        }
                    });
                }
            }
        });
        // button clean tank
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameView.tank.poop.size() > 0) {
                    gameView.tank.cleanPoop();
                    gameView.tank.countTankCleaning++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtCounterTankCleaning.setText("You cleaned the tank " + gameView.tank.countTankCleaning + " times.");
                        }
                    });
                }
            }
        });
        // toggle stats
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statsOpen) {
                    gameOverlayStats.setAlpha(0.0f);
                    gameOverlayStats.setVisibility(View.VISIBLE);
                    gameOverlayStats.animate().alpha(1.0f);
                    statsOpen = true;
                } else {
                    statsOpen = false;
                    gameOverlayStats.setAlpha(1.0f);
                    gameOverlayStats.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (!statsOpen) {
                                super.onAnimationEnd(animation);
                                gameOverlayStats.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
        btnStatsSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewStats.getVisibility() == View.VISIBLE) {
                    recyclerViewStats.setVisibility(View.GONE);
                }
                gameOverlayStatsSummary.setAlpha(0.0f);
                gameOverlayStatsSummary.setVisibility(View.VISIBLE);
                gameOverlayStatsSummary.animate().alpha(1.0f);
            }
        });
        btnStatsFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameOverlayStatsSummary.getVisibility() == View.VISIBLE) {
                    gameOverlayStatsSummary.setVisibility(View.GONE);
                }
                recyclerViewStats.setAlpha(0.0f);
                recyclerViewStats.setVisibility(View.VISIBLE);
                recyclerViewStats.animate().alpha(1.0f);
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
                    gameLayoutPause.setAlpha(0.0f);
                    gameLayoutPause.setVisibility(View.VISIBLE);
                    Thread.sleep(200);
                    gameLayoutPause.animate().alpha(1.0f);
                    //saveData(context, myPrefs, myPrefsKey, gameView.tank);
                    //Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    //startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // simulation tweaks menu
        btnSimSettings.setOnClickListener(new View.OnClickListener() {
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
        // sensor stuff
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linear_acceleration = new float[3];
        // landscape
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
        // stats recycler view
        fishAdapter = new FishAdapter(fishData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewStats.setLayoutManager(mLayoutManager);
        recyclerViewStats.setItemAnimator(new DefaultItemAnimator());
        recyclerViewStats.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewStats.setAdapter(fishAdapter);
        // prepare for trouble
        loadData(population, this);
        // and make it double
        setContentView(gameFrame);
        // to protect the world from devastation
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
            txtCounterFishAlive.setText("There are currently " + popSize +" fish swimming around in your tank.");
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
