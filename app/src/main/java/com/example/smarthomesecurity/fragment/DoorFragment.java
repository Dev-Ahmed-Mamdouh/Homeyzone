package com.example.smarthomesecurity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;
import com.example.smarthomesecurity.enums.POWER_STATUS;
import com.example.smarthomesecurity.helpers.DatabaseHelper;

public class DoorFragment extends ParentFragment implements View.OnClickListener {

    ImageView imageViewLogin;
    View view;
    SharedPreferences sp;
    ImageButton doorPower;
    private Spinner spParent, spChild;
    private String[] spinnerParent, spinnerChild;
    POWER_STATUS power_status = POWER_STATUS.UNDETERMINED;
    private TextView txt_status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_door, container, false);

        view.findViewById(R.id.btn_back).setOnClickListener((View.OnClickListener) this);
        doorPower = view.findViewById(R.id.door_power);
        imageViewLogin = view.findViewById(R.id.ImageDoors);
        spParent = view.findViewById(R.id.spinnerParent);
        spChild = view.findViewById(R.id.spinnerChild);
        txt_status = view.findViewById(R.id.txt_status);

        spinnerParent = getContext().getResources().getStringArray(R.array.Spinner_Door_Parent);
        ArrayAdapter parent = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerParent);
        parent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spParent.setAdapter(parent);

        sp = getActivity().getSharedPreferences("APP", Context.MODE_PRIVATE);

        spParent.setSelection(sp.getInt("selected_led", 0));
        spParent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    imageViewLogin.setImageDrawable(getResources().getDrawable(R.drawable.door_closed));
                    spinnerChild = getContext().getResources().getStringArray(R.array.Spinner_door_child);
                    ArrayAdapter child = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerChild);
                    child.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spChild.setAdapter(child);
                } else if (i == 1) {
                    imageViewLogin.setImageDrawable(getResources().getDrawable(R.drawable.window_closed));
                    spinnerChild = getContext().getResources().getStringArray(R.array.Spinner_window_child);
                    ArrayAdapter child = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerChild);
                    child.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spChild.setAdapter(child);
                } else if (i == 2) {
                    imageViewLogin.setImageDrawable(getResources().getDrawable(R.drawable.garage_closed));
                    spinnerChild = getContext().getResources().getStringArray(R.array.Spinner_garage_child);
                    ArrayAdapter child = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerChild);
                    child.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spChild.setAdapter(child);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                power_status = POWER_STATUS.UNDETERMINED;
                txt_status.setText("Connecting to DB");
                doorPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_undetermined));
                getPowerStatus((String) spChild.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        doorPower.setOnLongClickListener(new View.OnLongClickListener() {
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

        doorPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (power_status == POWER_STATUS.UNDETERMINED)
                    return;
                if (power_status == POWER_STATUS.ON) {
                    power_status = POWER_STATUS.OFF;
                    ((MainScreen) getActivity()).getDbHelper().setValue("Doors", (String) spChild.getSelectedItem(), "close");
                } else {
                    power_status = POWER_STATUS.ON;
                    ((MainScreen) getActivity()).getDbHelper().setValue("Doors", (String) spChild.getSelectedItem(), "open");
                }
                turnOnOff(power_status == POWER_STATUS.ON);
            }
        });

        return view;
    }


    private void getPowerStatus(String key) {
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
                txt_status.setText(value + " attribute not found on DB");
            }
        }, this, "Doors", key);

    }

    private void turnOnOff(boolean onOrOff) {
        if (onOrOff) {
            doorPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_on));
        } else {
            doorPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_off));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBack();
        }
    }

    @Override
    public boolean onBack() {
        ((MainScreen) getActivity()).changeFragment(new HomeFragment());
        return true;
    }

    @Override
    public void onConnected() {
        txt_status.setText("connecting to DB");
        getPowerStatus((String) spChild.getSelectedItem());
    }

    @Override
    public void onDisconnected() {
        txt_status.setText("Connection lost");
        power_status = POWER_STATUS.UNDETERMINED;
        turnOnOff(false);
        doorPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_undetermined));
    }
}
