package com.example.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class FlashLight extends Service implements SensorEventListener {

    private CameraManager mCameraManager;
    private String mCameraId;
    private ToggleButton toggleButton;
    private boolean isFlashOn=true;
    SensorManager sensorManager;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
         sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this,proximity,SensorManager.SENSOR_DELAY_NORMAL);
        super.onCreate();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void switchFlashLight(boolean status) {
        try {
            mCameraManager.setTorchMode(mCameraId, status);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        //intent du clique notif
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // intent du clique bouton play/pause
        PendingIntent pPPendingIntent = PendingIntent.getBroadcast(this, 0, new
                Intent("PlayPause"), PendingIntent.FLAG_UPDATE_CURRENT);
        // Notification Channel
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new
                    NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);}
        Notification notification =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Lecture en cours")
                        .setContentText("Tahir ve Nafess")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        // .setPriority(Notification.PRIORITY_MAX)
                        .build();
        startForeground(110, notification);return START_STICKY;}

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Flashlight Service")
                    .setContentText("Flashlight is ON")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_PROXIMITY){
            float x = event.values[0];
            if (x==0){
                System.out.println("haithem");
                switchFlashLight(isFlashOn);
                isFlashOn=!isFlashOn;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
