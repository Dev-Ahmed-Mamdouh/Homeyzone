package com.example.smarthomesecurity.fragment;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.smarthomesecurity.R;

public class TabsFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ConstraintLayout[] btns;
    private View selectorAnchor;
    private int selected_index = 0;
    private String[] btn_texts;
    private int[] images = new int[] {R.drawable.tab_profile, R.drawable.tab_setting,
            R.drawable.tab_home, R.drawable.tab_notification, R.drawable.tab_connections};
    private boolean toRight = true;
    private boolean isAnimating = false;
    private ConstraintLayout selector_data, selector_data1, selector_data2;
    private OnTabsClick delegate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_tabs, container, false);
        int[] btn_ids = new int[]{R.id.btn_profile, R.id.btn_setting, R.id.btn_home,
                R.id.btn_notification, R.id.btn_connections};
        btn_texts = getContext().getResources().getStringArray(R.array.tabs);
        btns = new ConstraintLayout[btn_ids.length];
        selectorAnchor = view.findViewById(R.id.selector_anchor);
        selector_data = view.findViewById(R.id.selector_data);
        selector_data1 = view.findViewById(R.id.selector_data1);
        selector_data2 = view.findViewById(R.id.selector_data2);
        for (int i = 0 ; i < btns.length ; i++) {
            btns[i] = view.findViewById(btn_ids[i]);
            btns[i].setOnClickListener(this);
        }
//        BorderedView tab_background =view.findViewById(R.id.tab_background);
//        tab_background.setBorderSide(BorderedView.BorderSide.up);
//        tab_background.setContentBackgroundColor(Color.WHITE);
//        tab_background.setBorderColor(ColorManager.darkenColor(Color.WHITE));
        view.post(() -> setSelectorData(2));
        return view;
    }

    @Override
    public void onClick(View v) {
        if (isAnimating)
            return;
        int index = -1;
        for (int i = 0 ; i < btns.length ; i++) {
            if (btns[i].getId() == v.getId())
                index = i;
        }
        if (index == selected_index)
            return;
        delegate.OnClick(index);
        toRight = index > selected_index;
        setSelectorData(index);
    }

    private void setSelectorData(int index) {
        isAnimating = true;
        float duration = 300;
        int width = selector_data.getWidth();
        float selectorDistance = (float) (getView().getWidth() * 0.185 * Math.abs(index-selected_index));
        float animation_duration = (float) (width * duration * (1 + (Math.abs(index-selected_index) * 0.085))) / selectorDistance;
        if (animation_duration > duration)
            animation_duration = duration;
        ValueAnimator dataOutAnimation = ValueAnimator.ofInt(0, width * 2);
        dataOutAnimation.addUpdateListener(animation -> {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) selector_data1.getLayoutParams();
            if (toRight)
                params.leftMargin = (int) animation.getAnimatedValue();
            else
                params.rightMargin = (int) animation.getAnimatedValue();
            selector_data1.setLayoutParams(params);
            if ((int) animation.getAnimatedValue() == width * 2) {//end of animation
                dataOutAnimation.removeAllUpdateListeners();
            }
        });
        dataOutAnimation.setDuration((long) animation_duration);
        dataOutAnimation.start();

        ((ImageView)selector_data2.getChildAt(0)).setImageResource(images[index]);
        ((AppCompatTextView)selector_data2.getChildAt(1)).setText(btn_texts[index]);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) selector_data2.getLayoutParams();
        if (toRight) {
            params.rightMargin = width * 2;
            params.leftMargin = 0;
        } else {
            params.leftMargin = width * 2;
            params.rightMargin = 0;
        }
        selector_data2.setLayoutParams(params);
        selector_data2.setVisibility(View.VISIBLE);

        ValueAnimator dataInAnimation = ValueAnimator.ofInt(width * 2, 0);
        dataInAnimation.setDuration((long) animation_duration);
        dataInAnimation.setStartDelay((long) (duration - animation_duration));

        dataInAnimation.addUpdateListener(animation1 -> {
            if (toRight)
                params.rightMargin = (int) animation1.getAnimatedValue();
            else
                params.leftMargin = (int) animation1.getAnimatedValue();
            selector_data2.setLayoutParams(params);
            if ((int)animation1.getAnimatedValue() == 0) {
                ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) selector_data1.getLayoutParams();
                params1.rightMargin = 0;
                params1.leftMargin = 0;
                selector_data1.setLayoutParams(params1);
                selector_data2.setVisibility(View.GONE);
                ((ImageView)selector_data1.getChildAt(0)).setImageResource(images[index]);
                ((AppCompatTextView)selector_data1.getChildAt(1)).setText(btn_texts[index]);
                ((ImageView)selector_data2.getChildAt(0)).setImageResource(0);
                ((AppCompatTextView)selector_data2.getChildAt(1)).setText("");
                dataInAnimation.removeAllUpdateListeners();
            }
        });
        dataInAnimation.start();


        ConstraintLayout.LayoutParams params_selector = (ConstraintLayout.LayoutParams) selectorAnchor.getLayoutParams();
        ValueAnimator selectorAnimation = ValueAnimator.ofFloat(params_selector.horizontalBias, (float) (1 - (index * 0.25)));
        selectorAnimation.setDuration((long) duration);
        selectorAnimation.addUpdateListener(valueAnimator -> {
            params_selector.horizontalBias = (float) valueAnimator.getAnimatedValue();
            selectorAnchor.setLayoutParams(params_selector);
            if ((float) (1 - (index * 0.25)) == (float) valueAnimator.getAnimatedValue()) {//end of animation
                isAnimating = false;
                selectorAnimation.removeAllUpdateListeners();
            }
        });
        selectorAnimation.start();
        new Handler().postDelayed(() -> {
            ConstraintLayout.LayoutParams btn_params = (ConstraintLayout.LayoutParams) btns[selected_index].getLayoutParams();
            btn_params.matchConstraintPercentWidth = 0.185f;
            btns[selected_index].setLayoutParams(btn_params);
            selected_index = index;
            ConstraintLayout.LayoutParams btn_params2 = (ConstraintLayout.LayoutParams) btns[selected_index].getLayoutParams();
            btn_params2.matchConstraintPercentWidth = 0.26f;
            btns[selected_index].setLayoutParams(btn_params2);
        }, (long)(duration / 2));
    }

    public void setOnTabClickListener(OnTabsClick ot) {
        delegate = ot;
    }

    public interface OnTabsClick {
        void OnClick(int index);
    }

}