package com.example.smarthomesecurity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;

public class DevicesFragment extends ParentFragment {

    private Button OvenEntryButton;
    private Button FridgeEntryButton;
    private Button RouterEntryButton;
    private Button AirConditionEntryButton;
    private Button CoffeeMachineEntryButton;
    private Button DishWasherEntryButton;
    private Button MicrowaveEntryButton;
    View view;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_devices, container, false);

        OvenEntryButton = view.findViewById(R.id.oven_button_enter);
        OvenEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        FridgeEntryButton = view.findViewById(R.id.fridge_button_enter);
        FridgeEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        RouterEntryButton = view.findViewById(R.id.router_button_enter);
        RouterEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        AirConditionEntryButton = view.findViewById(R.id.air_condition_button_enter);
        AirConditionEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        CoffeeMachineEntryButton = view.findViewById(R.id.coffee_machine_button_enter);
        CoffeeMachineEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        DishWasherEntryButton = view.findViewById(R.id.dish_washer_button_enter);
        DishWasherEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });

        MicrowaveEntryButton = view.findViewById(R.id.microwave_button_enter);
        MicrowaveEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainScreen)getActivity()).changeFragment(new FanFragment());
            }
        });
        return view;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public boolean onBack() {
        ((MainScreen) getActivity()).changeFragment(new HomeFragment());
        return true;
    }
}
