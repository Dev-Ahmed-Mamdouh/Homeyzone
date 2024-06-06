package com.example.smarthomesecurity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthomesecurity.activity.MainScreen;

abstract public class ParentFragment extends Fragment implements
        MainScreen.OnPhysicalKeyClicks, MainScreen.onConnectionChange  {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainScreen) getActivity()).setOnClicks(this);
        ((MainScreen) getActivity()).setOnConnectionChange(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainScreen) getActivity()).getDbHelper().removeListener();
    }

}
