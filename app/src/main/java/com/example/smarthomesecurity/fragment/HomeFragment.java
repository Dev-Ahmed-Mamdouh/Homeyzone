package com.example.smarthomesecurity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;
import com.example.smarthomesecurity.helpers.DatabaseHelper;

public class HomeFragment extends ParentFragment {

    private View view;
    private Button FanEntryButton;
    private Button LightEntryButton;
    private Button SensorEntryButton;
    private Button AlarmEntryButton;
    private Button CameraEntryButton;
    private Button DoorEntryButton;
    private Button WaterPumpEntryButton;
    private Button SoundSystemEntryButton;
    private Button DevicesEntryButton;
    private ImageButton btn_flame, btn_gas, btn_tempt, btn_humidity, btn_soil;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        btn_flame = view.findViewById(R.id.Flame);
        btn_gas = view.findViewById(R.id.Gas);
        btn_tempt = view.findViewById(R.id.Temperature);
        btn_soil = view.findViewById(R.id.Soil);
        btn_humidity = view.findViewById(R.id.Humidity);
        FanEntryButton = view.findViewById(R.id.fan_button_enter);
        FanEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        LightEntryButton = view.findViewById(R.id.light_button_enter);
        LightEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new LightFragment());
            }
        });

        SensorEntryButton = view.findViewById(R.id.sensor_button_enter);
        SensorEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new SensorsFragment());
            }
        });

        AlarmEntryButton = view.findViewById(R.id.alarm_button_enter);
        AlarmEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new AlarmFragment());
            }
        });

        CameraEntryButton = view.findViewById(R.id.camera_button_enter);
        CameraEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        DoorEntryButton = view.findViewById(R.id.door_button_enter);
        DoorEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new DoorFragment());
            }
        });

        WaterPumpEntryButton = view.findViewById(R.id.water_pump_button_enter);
        WaterPumpEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new PumpFragment());
            }
        });

        SoundSystemEntryButton = view.findViewById(R.id.sound_system_button_enter);
        SoundSystemEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((MainScreen)getActivity()).changeFragment(new LightFragment());
            }
        });

        DevicesEntryButton = view.findViewById(R.id.devices_button_enter);
        DevicesEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new DevicesFragment());
            }
        });
        getSensorsStatus();
        return view;
    }

    private void getSensorsStatus() {
        ((MainScreen) requireActivity()).getDbHelper().listen(new DatabaseHelper.onGetListener() {
            @Override
            public void onSuccessful(String[] values) {
                setZoneColor(btn_flame, Integer.parseInt(values[0]), getResources().getInteger(R.integer.flame_alert), getResources().getInteger(R.integer.flame_danger));
                setZoneColor(btn_gas, Integer.parseInt(values[1]), getResources().getInteger(R.integer.gas_alert), getResources().getInteger(R.integer.gas_danger));
                setZoneColor(btn_humidity, Integer.parseInt(values[2]), getResources().getInteger(R.integer.humidity_alert), getResources().getInteger(R.integer.humidity_danger));
                setZoneColor(btn_soil, Integer.parseInt(values[3]), getResources().getInteger(R.integer.soil_alert), getResources().getInteger(R.integer.soil_danger));
                setZoneColor(btn_tempt, Integer.parseInt(values[4]), getResources().getInteger(R.integer.temp_alert), getResources().getInteger(R.integer.temp_danger));
                if (Integer.parseInt(values[2])> 83 || Integer.parseInt(values[4])> 39){
                    ((MainScreen) getActivity()).getDbHelper().setValue("Fans" ,"Fan", "open");
                    return;
                }else {
                    ((MainScreen) getActivity()).getDbHelper().setValue("Fans" ,"Fan", "close");
                }
                if (Integer.parseInt(values[3])> 99 || Integer.parseInt(values[0]) < 10){
                    ((MainScreen) getActivity()).getDbHelper().setValue("Water Pump" ,"", "open");
                    ;
                }else {
                    ((MainScreen) getActivity()).getDbHelper().setValue("Water Pump" ,"", "close");

                }
            }

            @Override
            public void onFailed() {
            }

            @Override
            public void onKeyNotFound(String value) {
            }
        }, this, "Sensors","Flame", "Gas", "Humidity", "Soil Moisture", "Temperature");


    }

    private void setZoneColor(ImageButton zoneBtn, int value, int alert, int danger) {
        if (value < alert) {
            zoneBtn.setBackground(getResources().getDrawable(R.drawable.bg_button_sensor_safe_zone));
        } else if (value < danger) {
            zoneBtn.setBackground(getResources().getDrawable(R.drawable.bg_button_sensor_alert_zone));
        } else {
            zoneBtn.setBackground(getResources().getDrawable(R.drawable.bg_button_sensor_danger_zone));
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public boolean onBack() {
    return false;
    }
}
