package com.example.smarthomesecurity.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DatabaseHelper {

    private DatabaseReference mDatabase;
    private ValueEventListener eventListener;

    public DatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ESP32_APP");
    }

    public void setValue(String parent, String child, Object value) {
        mDatabase.child(parent).child(child).setValue(value);
    }

    public void listenForSensors(onGetListener listener) {
        mDatabase.child("Sensors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] childs = {"Flame", "Gas", "Humidity", "Soil Moisture", "Temperature"};
                String[] values = new String[childs.length];
                for (int i = 0 ; i < values.length ; i++) {
                    values[i] = String.valueOf(snapshot.child(childs[i]).getValue());
                    if (values[i].compareTo("null") == 0) {
                        listener.onKeyNotFound(childs[i]);
                        return;
                    }
                }
                listener.onSuccessful(values);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailed();
            }
        });
    }

    public void removeListener() {
        if (eventListener != null)
            mDatabase.removeEventListener(eventListener);
        eventListener = null;
    }

    public void listen(onGetListener listener, Fragment fr, String parent , String... child) {
        removeListener();
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] values = new String[child.length];
                for (int i = 0 ; i < values.length ; i++) {
                    values[i] = String.valueOf(snapshot.child(child[i]).getValue());
                    if (values[i].compareTo("null") == 0 && fr.getContext() != null) {
                        listener.onKeyNotFound(child[i]);
                        return;
                    }
                }
                if (fr.getContext() != null)
                    listener.onSuccessful(values);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (fr.getContext() != null)
                    listener.onFailed();
            }
        };
        mDatabase.child(parent).addValueEventListener(eventListener);
    }

    public interface onGetListener {

        void onSuccessful(String[] values);

        void onFailed();

        void onKeyNotFound(String value);

    }



}
