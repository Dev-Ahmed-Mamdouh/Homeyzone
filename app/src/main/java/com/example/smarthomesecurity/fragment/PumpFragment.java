package com.example.smarthomesecurity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;
import com.example.smarthomesecurity.enums.POWER_STATUS;
import com.example.smarthomesecurity.helpers.DatabaseHelper;
import com.example.smarthomesecurity.views.SeekArc;

public class PumpFragment extends ParentFragment implements View.OnClickListener {

    View view;
    ImageView image;
    SeekArc fanSeekArc;
    int fanSpeed = 50;
    ImageButton pumpPower;
    Button fanIncrease, fanDecrease;

    POWER_STATUS power_status = POWER_STATUS.UNDETERMINED;

    private TextView txt_status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_pump, container, false);

        view.findViewById(R.id.btn_back).setOnClickListener(this);
        pumpPower = view.findViewById(R.id.pump_power);
        pumpPower.setOnClickListener(this);
        txt_status = view.findViewById(R.id.txt_status);
        image = (ImageView) view.findViewById(R.id.pump);

        pumpPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (power_status == POWER_STATUS.UNDETERMINED)
                    return;
                if (power_status == POWER_STATUS.ON) {
                    power_status = POWER_STATUS.OFF;
                    ((MainScreen) getActivity()).getDbHelper().setValue("Water Pump" , "","close");
                } else {
                    power_status = POWER_STATUS.ON;
                    ((MainScreen) getActivity()).getDbHelper().setValue("Water Pump" ,"", "open");
                }
                turnOnOff(power_status == POWER_STATUS.ON);
            }
        });

        pumpPower.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (power_status == POWER_STATUS.UNDETERMINED) {
                    power_status = POWER_STATUS.OFF;
                    txt_status.setText("Test mode enabled");
                    turnOnOff(power_status == POWER_STATUS.ON);
                }
                return true;
            }
        });
        getSensorsStatus();
        getPowerStatus();
        return view;
    }

    private void getSensorsStatus() {
        ((MainScreen) requireActivity()).getDbHelper().listen(new DatabaseHelper.onGetListener() {
            @Override
            public void onSuccessful(String[] values) {
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

    private void turnOnOff(boolean onOrOff) {
        if (onOrOff) {
            pumpPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_on));
        } else {
            pumpPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_off));
        }
        setFanSpeed(onOrOff ? fanSpeed : 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBack();
        } else if (v.getId() == R.id.fan_increase) {
            if (fanSpeed == 100)
                return;
            fanSpeed += 25;
            setFanSpeed(fanSpeed);
        } else if (v.getId() == R.id.fan_decrease) {
            if (fanSpeed == 0)
                return;
            fanSpeed -= 25;
            setFanSpeed(fanSpeed);
        }
    }

    private void getPowerStatus() {
        ((MainScreen) requireActivity()).getDbHelper().listen(new DatabaseHelper.onGetListener() {
            @Override
            public void onSuccessful(String[] value) {
                txt_status.setText("Connected");
                power_status = value[0].compareTo("open") == 0 ? POWER_STATUS.ON : POWER_STATUS.OFF;
                turnOnOff(power_status == POWER_STATUS.ON);
            }

            @Override
            public void onFailed() {
                txt_status.setText("Failed to connect to DB, retrying");
            }

            @Override
            public void onKeyNotFound(String value) {
                power_status = POWER_STATUS.OFF;
                txt_status.setText(value + " attribute not found on DB");
                pumpPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_undetermined));
                turnOnOff(false);

            }
        }, this, "Water Pump", "");

    }

    private void setFanSpeed(int newFanSpeed) {
        Animation rotate;
        switch (newFanSpeed) {
            case 25:
                rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_level_quarter);
                break;
            case 50:
                rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_level_half);
                break;
            case 75:
                rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_level_3);
                break;
            case 100:
                rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_max);
                break;
            default:
                rotate = AnimationUtils.loadAnimation(getContext(), R.anim.non_rotate);
                break;
        }
        image.startAnimation(rotate);
    }

    @Override
    public boolean onBack() {
        ((MainScreen) getActivity()).changeFragment(new HomeFragment());
        return true;
    }

    @Override
    public void onConnected() {
        txt_status.setText("connecting to DB");
        getPowerStatus();
        getSensorsStatus();
    }

    @Override
    public void onDisconnected() {
        txt_status.setText("Connection lost");
        power_status = POWER_STATUS.UNDETERMINED;
        turnOnOff(false);
        pumpPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_undetermined));
    }

}
