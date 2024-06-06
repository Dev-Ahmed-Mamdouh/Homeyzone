package com.example.smarthomesecurity.activity;

import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.fragment.AlarmFragment;
import com.example.smarthomesecurity.fragment.HomeFragment;
import com.example.smarthomesecurity.fragment.NotificationsFragment;
import com.example.smarthomesecurity.fragment.ParentFragment;
import com.example.smarthomesecurity.fragment.TabsFragment;
import com.example.smarthomesecurity.helpers.DatabaseHelper;
import com.example.smarthomesecurity.models.Notification;
import com.example.smarthomesecurity.utilities.ConnectionStateMonitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainScreen extends AppCompatActivity implements ConnectionStateMonitor.onConnectionChangeListener {

    TabsFragment tabs;
    OnPhysicalKeyClicks onClicks;
    onConnectionChange onChange;
    DatabaseHelper dbHelper;
    List<Notification> notifications = new ArrayList<>();
    List<Notification> alarms = new ArrayList<>();
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mp = MediaPlayer.create(this, R.raw.danger_notification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dbHelper = new DatabaseHelper();
        dbHelper.listenForSensors(new DatabaseHelper.onGetListener() {
            @Override
            public void onSuccessful(String[] values) {
                notifications.clear();
                checkSensors("Flame", Integer.parseInt(values[0]), getResources().getInteger(R.integer.flame_alert), getResources().getInteger(R.integer.flame_danger));
                checkSensors("Gas", Integer.parseInt(values[1]), getResources().getInteger(R.integer.gas_alert), getResources().getInteger(R.integer.gas_danger));
                checkSensors("Humidity", Integer.parseInt(values[2]), getResources().getInteger(R.integer.humidity_alert), getResources().getInteger(R.integer.humidity_danger));
                checkSensors("Soil Moisture", Integer.parseInt(values[3]), getResources().getInteger(R.integer.soil_alert), getResources().getInteger(R.integer.soil_danger));
                checkSensors("Temperature", Integer.parseInt(values[4]), getResources().getInteger(R.integer.temp_alert), getResources().getInteger(R.integer.temp_danger));
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_content);
                if (f instanceof NotificationsFragment) {
                    ((NotificationsFragment) f).setNotificationsList(notifications);
                } else if (f instanceof AlarmFragment) {
                    ((AlarmFragment) f).setNotificationsList(alarms);
                }
                for (int  i = 0 ; i < notifications.size() ; i++) {
                    if (notifications.get(i).isDanger() && !mp.isPlaying()) {
                        mp.start();
                        return;
                    }
                }
                if (!mp.isPlaying()) {
                    for (int  i = 0 ; i < notifications.size() ; i++) {
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onKeyNotFound(String value) {

            }
        });
        super.onCreate(savedInstanceState);
        new ConnectionStateMonitor().enable(this, this);
        setContentView(R.layout.activity_main_screen);
        tabs = (TabsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tabs);
        tabs.setOnTabClickListener(new TabsFragment.OnTabsClick() {
            @Override
            public void OnClick(int index) {
                if (index == 2) {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment f = fm.findFragmentById(R.id.fragment_content);
                    if (!(f instanceof HomeFragment))
                        changeFragment(new HomeFragment());
                } else if (index == 3) {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment f = fm.findFragmentById(R.id.fragment_content);
                    if (!(f instanceof NotificationsFragment))
                        changeFragment(new NotificationsFragment());
                }
            }
        });
        changeFragment(new HomeFragment());
    }

    public void changeFragment(ParentFragment f) {
        changeFragment(f, null);
    }

    private void checkSensors(String key, int value, int alert, int danger) {
        String time = new SimpleDateFormat("yyyy/MM/dd                hh:mm",
                Locale.getDefault()).format(Calendar.getInstance().getTime());
        if (value >= danger){
            notifications.add(new Notification(key, time, value, true, "Danger situation, Action will be taken now"));
            alarms.add(new Notification(key, time, value, true, "Danger situation, Action will be taken now"));
        } else if (value >= alert) {
            notifications.add(new Notification(key, time, value, false, "Alarm situation, Please take action"));
        }
    }

    public void changeFragment(ParentFragment f, Bundle b) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
        f.setArguments(b);
        ft.replace(R.id.fragment_content, f);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (onClicks != null) {
            if (!onClicks.onBack())
                super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void setOnClicks(OnPhysicalKeyClicks onClicks) {
        this.onClicks = onClicks;
    }

    public void setOnConnectionChange(onConnectionChange onChange) {
        this.onChange = onChange;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<Notification> getAlarms() {
        return alarms;
    }

    @Override
    public void onConnectionAvailable() {
        if (onChange != null) {
            onChange.onConnected();
        }
    }

    @Override
    public void onConnectionUnavailable() {
        if (onChange != null) {
            onChange.onDisconnected();
        }
    }

    public interface onConnectionChange {
        void onConnected();
        void onDisconnected();
    }

    public interface OnPhysicalKeyClicks {
        boolean onBack();
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }
}
