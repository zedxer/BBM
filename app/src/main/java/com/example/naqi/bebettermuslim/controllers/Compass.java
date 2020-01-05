package com.example.naqi.bebettermuslim.controllers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.naqi.bebettermuslim.R;

public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuthForNorth = 0f;
    private float azimuthForKaba = 0f;
    private float currectAzimuthForNorth = 0;
    private float currectAzimuthForKaba = 0;
    public double latitude;
    public double longitude;
    private Context context;
    private Vibrator vibrator;
    boolean isVibrated = false;


    public TextView headingTextView = null;
    public TextView heading = null;
    public TextView displacementTextView = null;

    // compass arrow to rotate
    public ImageView arrowView = null;
    public ImageView dialView = null;
    public ImageView kabaImageView = null;

    public Compass(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void start() {
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
        displacementTextView.setVisibility(View.VISIBLE);
        headingTextView.setVisibility(View.VISIBLE);
        heading.setVisibility(View.VISIBLE);
        System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void adjustArrow() {
        if (arrowView == null || dialView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }

        Log.i(TAG, "will set rotation from " + currectAzimuthForNorth + " to " + azimuthForNorth);
        Log.i(TAG, "will set rotation from " + currectAzimuthForKaba + " to " + azimuthForKaba);

        Animation an = new RotateAnimation(-currectAzimuthForNorth, -currectAzimuthForNorth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation an2 = new RotateAnimation(-currectAzimuthForKaba, -azimuthForKaba, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        currectAzimuthForNorth = azimuthForNorth;
        currectAzimuthForKaba = azimuthForKaba;

        an.setDuration(50);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        an2.setDuration(50);
        an2.setRepeatCount(0);
        an2.setFillAfter(true);

        arrowView.startAnimation(an);
        dialView.startAnimation(an2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
            }

            float Ret[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(Ret, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(Ret, orientation);

                azimuthForNorth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuthForNorth = (azimuthForNorth + 360) % 360;

                azimuthForKaba = (float) Math.toDegrees(orientation[0]); // orientation
                azimuthForKaba = (azimuthForKaba + 360) % 360;
                azimuthForKaba -= bearing(latitude, longitude, 21.4225, 39.8262);

                if (azimuthForKaba < 5 && azimuthForKaba > -5) {
                    dialView.setImageResource(R.drawable.gray_color);
                    kabaImageView.setImageResource(R.drawable.kaba_color);
                    if (!isVibrated) {
                        vibrator.vibrate(50);
                    }
                    isVibrated = true;
                } else {
                    dialView.setImageResource(R.drawable.dail_gray);
                    kabaImageView.setImageResource(R.drawable.kaba_gray);
                    isVibrated = false;
                }

                headingTextView.setText(String.format("%.2f", azimuthForNorth - azimuthForKaba));
                displacementTextView.setText(String.format("Displacement: %.2f km", distanceBtwPositions(latitude, longitude, 21.4225, 39.8262)));

                adjustArrow();
            }
        }
    }

    private double bearing(double startLat, double startLng, double endLat, double endLng) {
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff = Math.toRadians(endLng - startLng);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
//        degree -= bearing(latitude, longitude, 21.4225, 39.8262);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    private float distanceBtwPositions(double startLat, double startLng, double endLat, double endLng) {
        Location locationA = new Location("point A");
        locationA.setLatitude(startLat);
        locationA.setLongitude(startLng);

        Location locationB = new Location("point B");
        locationB.setLatitude(endLat);
        locationB.setLongitude(endLng);

        return locationA.distanceTo(locationB) / 1000;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
