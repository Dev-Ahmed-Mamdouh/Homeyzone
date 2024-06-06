package com.example.smarthomesecurity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;

import com.example.smarthomesecurity.helpers.DatabaseHelper;



public class SensorsFragment extends ParentFragment implements View.OnClickListener {

    View view;
    private TextView txt_status, txt_sensors_flame, txt_sensors_gas, txt_sensors_humidity, txt_sensors_temp, txt_sensors_soil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_sensor, container, false);

        view.findViewById(R.id.btn_back).setOnClickListener((View.OnClickListener) this);
        txt_status = view.findViewById(R.id.txt_status);
        txt_sensors_flame = view.findViewById(R.id.txt_flame);
        txt_sensors_gas = view.findViewById(R.id.txt_gas);
        txt_sensors_humidity = view.findViewById(R.id.txt_hdy);
        txt_sensors_temp = view.findViewById(R.id.txt_temp);
        txt_sensors_soil = view.findViewById(R.id.txt_soil);
        getSensorsStatus();
        return view;
    }

    private void getSensorsStatus() {
        ((MainScreen) requireActivity()).getDbHelper().listen(new DatabaseHelper.onGetListener() {
            @Override
            public void onSuccessful(String[] values) {
                txt_status.setText("Connected");
                txt_sensors_flame.setText(values[0] + " %");
                txt_sensors_gas.setText(values[1] + " %");
                txt_sensors_humidity.setText(values[2] + " %");
                txt_sensors_soil.setText(values[3] + " %");
                txt_sensors_temp.setText(values[4] + " °C");
                if (Integer.parseInt(values[2])> 83 || Integer.parseInt(values[4])> 39){
                    ((MainScreen) getActivity()).getDbHelper().setValue("Fans" ,"Fan", "open");
                    return;
                }else {
                    ((MainScreen) getActivity()).getDbHelper().setValue("Fans" ,"Fan", "close");
                }
                if (Integer.parseInt(values[3])> 99 || Integer.parseInt(values[0]) < 10){
                    ((MainScreen) getActivity()).getDbHelper().setValue("Water Pump" ,"", "open");
                    return;
                }else {
                    ((MainScreen) getActivity()).getDbHelper().setValue("Water Pump" ,"", "close");

                }
//                setZoneColor(getResources().getDrawable(R.id.), Integer.parseInt(values[0]), getResources().getInteger(R.integer.flame_alert), getResources().getInteger(R.integer.flame_danger));
//                setZoneColor(btn_gas, Integer.parseInt(values[1]), getResources().getInteger(R.integer.gas_alert), getResources().getInteger(R.integer.gas_danger));
//                setZoneColor(btn_humidity, Integer.parseInt(values[2]), getResources().getInteger(R.integer.humidity_alert), getResources().getInteger(R.integer.humidity_danger));
//                setZoneColor(btn_soil, Integer.parseInt(values[3]), getResources().getInteger(R.integer.soil_alert), getResources().getInteger(R.integer.soil_danger));
//                setZoneColor(btn_tempt, Integer.parseInt(values[4]), getResources().getInteger(R.integer.temp_alert), getResources().getInteger(R.integer.temp_danger));
            }

            @Override
            public void onFailed() {
                txt_status.setText("Failed to connect to DB, retrying");
            }

            @Override
            public void onKeyNotFound(String value) {
                txt_status.setText(value + " attribute not found on DB");
            }
        }, this,"Sensors","Flame", "Gas", "Humidity", "Soil Moisture", "Temperature");

    }

//    private void setZoneColor(ImageButton zoneBtn, int value, int alert, int danger) {
//        if (value < alert) {
//            zoneBtn.setBackground(getResources().getDrawable(R.drawable.bg_button_sensor_safe_zone));
//        } else if (value < danger) {
//            zoneBtn.setBackground(getResources().getDrawable(R.drawable.bg_button_sensor_alert_zone));
//        } else {
//            zoneBtn.setBackground(getResources().getDrawable(R.drawable.bg_button_sensor_danger_zone));
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBack();
        }
    }

    @Override
    public void onConnected() {
        txt_status.setText("connecting to DB");
        getSensorsStatus();
    }

    @Override
    public void onDisconnected() {
        txt_status.setText("Connection lost");
    }

    @Override
    public boolean onBack() {
        ((MainScreen) getActivity()).changeFragment(new HomeFragment());
        return true;
    }
}
