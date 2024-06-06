package com.example.smarthomesecurity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.activity.MainScreen;
import com.example.smarthomesecurity.adapter.NotificationAdapter;
import com.example.smarthomesecurity.models.Notification;

import java.util.List;

public class AlarmFragment extends ParentFragment {

    private View view;
    private ListView list_notifications;
    private TextView txt_no_notifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        list_notifications = view.findViewById(R.id.list_notifications);
        txt_no_notifications = view.findViewById(R.id.txt_notifications);
        txt_no_notifications.setText("no alarms");
        setNotificationsList(((MainScreen)getActivity()).getAlarms());
        return view;
    }

    public void setNotificationsList(List notifications) {
        if (!notifications.isEmpty()) {
            list_notifications.setAdapter(new NotificationAdapter(notifications,R.layout.adapter_alarm, getContext()));
            txt_no_notifications.setVisibility(View.GONE);
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
        ((MainScreen) getActivity()).changeFragment(new HomeFragment());
        return true;
    }
}
