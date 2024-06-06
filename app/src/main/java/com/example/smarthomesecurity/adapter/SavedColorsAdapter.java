package com.example.smarthomesecurity.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.smarthomesecurity.R;

import java.util.List;

public class SavedColorsAdapter extends ArrayAdapter<Integer> {

    private Integer[] colors;
    Context mContext;

    public SavedColorsAdapter(Context context, Integer[] colors) {
        super(context, R.layout.adapter_colors, colors);
        this.colors = colors;
        this.mContext=context;
    }

    private static class ViewHolder {
        TextView txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_colors, parent, false);
            viewHolder.txt = convertView.findViewById(R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txt.setBackgroundColor(Color.RED);
        // Return the completed view to render on screen
        return convertView;
    }
}