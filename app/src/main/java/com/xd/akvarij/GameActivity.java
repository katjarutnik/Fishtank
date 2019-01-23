package com.xd.akvarij;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class GameActivity extends Activity implements SensorEventListener {

    boolean fresh;
    int population;
    int pickedPrimaryColor;
    int pickedSecondaryColor;

    GameView gameView;

    FrameLayout gameFrame;
    ConstraintLayout gameOverlay;

    public Button btnFeed;
    public Button btnClean;
    public Button btnSaveAndExit;
    public TextView txtDays;
    public TextView txtInfoTop;
    public TextView txtInfoBottom;

    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] gravity;
    private float[] linear_acceleration;

    public static final String myPrefs = "myTankPrefs";
    public static final String myPrefsKey = "tank";

    MyCallback myCallback;
    Context context;

    int orientacija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            population = extras.getInt("POPULATION");
            fresh = extras.getBoolean("FRESH");
            pickedPrimaryColor = extras.getInt("COLOR_PRIMARY");
            pickedSecondaryColor = extras.getInt("COLOR_SECONDARY");
        } else {
            population = 10;
            fresh = true;
        }

        context = this;

        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linear_acceleration = new float[3];

        final OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == 90) {
                    orientacija = orientation;
                }
                if (orientation == 270) {
                    orientacija = orientation;
                }
            }
        };

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        }

        View rootView = getLayoutInflater().inflate(R.layout.activity_game, null, true);

        gameView = new GameView(this);

        gameFrame = new FrameLayout(this);
        gameOverlay = new ConstraintLayout(this);

        gameFrame = rootView.findViewById(R.id.gameFrame);
        gameOverlay = rootView.findViewById(R.id.gameOverlay);

        btnFeed = new Button(this);
        btnFeed = gameOverlay.findViewById(R.id.btnFeed);
        btnClean = new Button(this);
        btnClean = gameOverlay.findViewById(R.id.btnClean);
        btnSaveAndExit = new Button(this);
        btnSaveAndExit = gameOverlay.findViewById(R.id.btnSaveExit);
        txtDays = new TextView(this);
        txtDays = gameOverlay.findViewById(R.id.txtDays);
        txtInfoTop = new TextView(this);
        txtInfoTop = gameOverlay.findViewById(R.id.txtInfo);
        txtInfoBottom = new TextView(this);
        txtInfoBottom = gameOverlay.findViewById(R.id.txtInfo2);

        gameFrame.removeView(gameOverlay);
        gameFrame.addView(gameView);
        gameFrame.addView(gameOverlay);

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
            public void updateTxtInfoBottom(final String myString) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtInfoBottom.setText(myString);
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

        btnSaveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    gameView.thread.setRunning(false);
                    gameView.thread.join();
                    saveData(context, myPrefs, myPrefsKey, gameView.tank);
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loadData(population, fresh, this);
        setContentView(gameFrame);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
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
                if (orientacija == 90)
                    gameView.tank.shakingStart(-event.values[1], -event.values[0]);
                else
                    gameView.tank.shakingStart(event.values[1], event.values[0]);

            } else {
                gameView.tank.shakingStop();
            }
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

    private void loadData(int popSize, Boolean fresh, Context context) {
        if (fresh) {
            gameView.tank = new Tank(popSize, BitmapFactory.decodeResource(getResources(), Constants.FISH_IMAGE), myCallback, context);
            gameView.tank.generateFirstGeneration(pickedPrimaryColor, pickedSecondaryColor);
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
}
