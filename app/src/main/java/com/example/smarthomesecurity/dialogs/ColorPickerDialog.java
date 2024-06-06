package com.example.smarthomesecurity.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smarthomesecurity.R;
import com.flask.colorpicker.ColorPickerView;

public class ColorPickerDialog extends DialogFragment {

    View view;
    int startingColor;
    onColorChange occ;

    public ColorPickerDialog(int initialColor) {
        startingColor = initialColor;
    }

    public void setOnColorChangeListener(onColorChange occ) {
        this.occ = occ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_colorpicker, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ColorPickerView colorPicker = view.findViewById(R.id.color_picker_view);
        colorPicker.setColor(startingColor, true);
        ImageView imageViewClose= view.findViewById(R.id.image_view_close);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (occ != null)
                    occ.onChange(colorPicker.getSelectedColor());
                dismiss();
            }
        });
        return view;
    }

    public interface onColorChange {
        void onChange(int newColor);
    }

}
