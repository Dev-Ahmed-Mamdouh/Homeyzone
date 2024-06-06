package com.example.smarthomesecurity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;
import com.example.smarthomesecurity.adapter.NotificationAdapter;
import com.example.smarthomesecurity.adapter.SavedColorsAdapter;
import com.example.smarthomesecurity.dialogs.ColorPickerDialog;
import com.example.smarthomesecurity.enums.POWER_STATUS;
import com.example.smarthomesecurity.helpers.DatabaseHelper;

import java.util.HashSet;
import java.util.Set;

public class LightFragment extends ParentFragment implements View.OnClickListener {

    ColorPickerDialog colorPickerDialog;
    ImageView img_bulb;
    SharedPreferences sp;
    View view;

    ImageButton lightPower;

    private String[] spinnerLights;
    private Spinner spinnerLight, spinnerColorsHistory;
    private final int powered_off_bulb = 0x55FFFFFF;

    POWER_STATUS power_status = POWER_STATUS.UNDETERMINED;
    private TextView txt_status;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_light, container, false);

        view.findViewById(R.id.btn_back).setOnClickListener((View.OnClickListener) this);
        img_bulb = view.findViewById(R.id.img_bulb_back);
        img_bulb.setOnClickListener(this);
        lightPower = view.findViewById(R.id.light_power);
        spinnerLight = view.findViewById(R.id.spinnerLight);
        txt_status = view.findViewById(R.id.txt_status);
        spinnerColorsHistory = view.findViewById(R.id.colors_history);

        spinnerLights = getContext().getResources().getStringArray(R.array.Spinner_light);
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerLights);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLight.setAdapter(aa);
        sp = getActivity().getSharedPreferences("APP", Context.MODE_PRIVATE);




        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLight.setSelection(sp.getInt("selected_led",0));
        spinnerLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sp.edit().putInt("selected_led", i).apply();
                power_status = POWER_STATUS.UNDETERMINED;
                txt_status.setText("Connecting to DB");
                lightPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_undetermined));
                getPowerStatus((String) adapterView.getAdapter().getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lightPower.setOnLongClickListener(new View.OnLongClickListener() {
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

        setColorsSpinner(true);
        colorPickerDialog = new ColorPickerDialog(sp.getInt("bulbColor" + sp.getInt("selected_led",0), -1));
        colorPickerDialog.setOnColorChangeListener(new ColorPickerDialog.onColorChange() {
            @Override
            public void onChange(int newColor) {
                if (power_status == POWER_STATUS.ON) {
                    setBulbColor(newColor);
                }
                sp.edit().putInt("bulbColor" + sp.getInt("selected_led",0), newColor).apply();
            }
        });

        view.findViewById(R.id.btn_pick_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerDialog.show(getParentFragmentManager(), "");
            }
        });

        lightPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (power_status == POWER_STATUS.UNDETERMINED)
                    return;
                if (power_status == POWER_STATUS.ON) {
                    power_status = POWER_STATUS.OFF;
                    ((MainScreen) getActivity()).getDbHelper().setValue("Lights" ,(String) spinnerLight.getSelectedItem(), "close");
                } else {
                    power_status = POWER_STATUS.ON;
                    ((MainScreen) getActivity()).getDbHelper().setValue("Lights" ,(String) spinnerLight.getSelectedItem(), "open");
                }
                turnOnOff(power_status == POWER_STATUS.ON);
            }
        });

        if (power_status == POWER_STATUS.ON && sp.getInt("bulbColor" + sp.getInt("selected_led",0), -1) != -1) {
            setBulbColor(sp.getInt("bulbColor" + sp.getInt("selected_led",0), -1));
        } else {
            setBulbColor(powered_off_bulb);
        }

        return view;
    }

    private void getPowerStatus(String led) {
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
        },this,"Lights", led);

    }

    private void setColorsSpinner(boolean fromOnCreate) {
        Integer[] h = {0xFF00FF00, 0xFF505050};
        String colors = sp.getString("leds_history", "0xFF00FF00,0xFF00FF00");
        if (colors.isEmpty()) {
            return;
        }
        String[] gfhg = colors.split(",");
        SavedColorsAdapter aa2 = new SavedColorsAdapter(getContext(), h);
        spinnerColorsHistory.setAdapter(aa2);
        spinnerColorsHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (fromOnCreate)
                    return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void turnOnOff(boolean onOrOff) {
        if (onOrOff) {
            lightPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_on));
            setBulbColor(sp.getInt("bulbColor" + sp.getInt("selected_led",0), -1));
        } else {
            lightPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_off));
            setBulbColor(powered_off_bulb);
        }
    }

    public void setBulbColor(int color) {
        Drawable mIcon= ContextCompat.getDrawable(getActivity(), R.drawable.bulb_back);
        mIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        img_bulb.setImageDrawable(mIcon);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBack();
        }
    }

    @Override
    public void onConnected() {
        txt_status.setText("connecting to DB");
        getPowerStatus((String) spinnerLight.getSelectedItem());
    }

    @Override
    public void onDisconnected() {
        txt_status.setText("Connection lost");
        power_status = POWER_STATUS.UNDETERMINED;
        turnOnOff(false);
        lightPower.setImageDrawable(getResources().getDrawable(R.drawable.btn_undetermined));
    }

    @Override
    public boolean onBack() {
        ((MainScreen) getActivity()).changeFragment(new HomeFragment());
        return true;
    }
}
