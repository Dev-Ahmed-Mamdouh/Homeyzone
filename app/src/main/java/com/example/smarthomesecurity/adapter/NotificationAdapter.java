package com.example.smarthomesecurity.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.smarthomesecurity.R;
import com.example.smarthomesecurity.models.Notification;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private List<Notification> notifications;
    Context mContext;
    int layout_resource;

    public NotificationAdapter(List<Notification> notifications,int layout_resource, Context context) {
        super(context, layout_resource, notifications);
        this.notifications = notifications;
        this.mContext=context;
        this.layout_resource = layout_resource;
    }

    private static class ViewHolder {
        TextView txtHeader;
        TextView txtHint;
        TextView txtTime;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_notification, parent, false);
            viewHolder.txtHeader = (TextView) convertView.findViewById(R.id.txt_header);
            viewHolder.txtHint = (TextView) convertView.findViewById(R.id.txt_hint);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (notifications.get(position).isDanger()) {
            convertView.setBackground(getContext().getResources().getDrawable(R.drawable.bg_danger));
        } else {
            convertView.setBackground(getContext().getResources().getDrawable(R.drawable.bg_alert));
        }
        viewHolder.txtHeader.setText(notifications.get(position).getTitle()  + " : " + notifications.get(position).getValue());
        viewHolder.txtHint.setText(notifications.get(position).getHint());
        viewHolder.txtTime.setText(notifications.get(position).getTime());
        // Return the completed view to render on screen
        return convertView;
    }
}