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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GenerateNewActivity extends Activity implements SensorEventListener {

    int population;
    boolean fresh;
    GameView gameView;
    FrameLayout gameBase;
    ConstraintLayout gameOverlay;
    public Button btnFeed;
    public Button btnSaveAndExit;

    public TextView txtDays;

    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] gravity;
    private float[] linear_acceleration;

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "myprefs";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            population = extras.getInt("POPULATION");
            fresh = extras.getBoolean("FRESH");
        } else {
            population = 10;
            fresh = true;
        }
        context = this;
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linear_acceleration = new float[3];

        View rootView = getLayoutInflater().inflate(R.layout.game, null, true);
        gameView = new GameView(this);
        gameBase = new FrameLayout(this);
        gameOverlay = new ConstraintLayout(this);

        gameBase = rootView.findViewById(R.id.gameFrame);
        gameOverlay = rootView.findViewById(R.id.gameButtons);

        btnFeed = new Button(this);
        btnFeed = gameOverlay.findViewById(R.id.btnFeed);
        btnSaveAndExit = new Button(this);
        btnSaveAndExit = gameOverlay.findViewById(R.id.btnSaveExit);

        gameBase.removeView(gameOverlay);

        gameBase.addView(gameView);
        gameBase.addView(gameOverlay);
        setContentView(gameBase);

        loadData(population, fresh);

        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.tank.feedFish();
            }
        });

        btnSaveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    gameView.thread.setRunning(false);
                    gameView.thread.join();
                    saveData();
                    Intent intent = new Intent(GenerateNewActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                gameView.tank.shakingStart(-event.values[0], event.values[1]);
            } else {
                gameView.tank.shakingStop();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(gameView.tank);
        editor.putString("tank", json);
        editor.apply();
    }

    private void loadData(int popSize, Boolean fresh) {
        if (fresh) {
            gameView.tank = new Tank(popSize, BitmapFactory.decodeResource(getResources(), R.drawable.fish));
            gameView.tank.generateFirstGeneration();
        } else {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("tank", null);
            Type type = new TypeToken<Tank>() {}.getType();
            gameView.tank = gson.fromJson(json, type);

            if (gameView.tank == null) {
                Intent intent = new Intent(GenerateNewActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                gameView.daytime = gameView.tank.dayTime;
            }
        }
    }
}
