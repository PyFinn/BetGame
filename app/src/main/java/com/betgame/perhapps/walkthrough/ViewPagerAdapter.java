package com.betgame.perhapps.walkthrough;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.betgame.perhapps.R;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    //Arrays
    public int[] icons = {
            R.drawable.icon_football,
            R.drawable.icon_book,
            R.drawable.icon_graph,
            R.drawable.icon_money
    };

    public int[] headlogos = {
            R.string.welcome_to_bet_game,
            R.string.bet_on_real_events,
            R.string.grow_your_balance,
            R.string.get_started_funds
    };
    public int[] descs = {
            R.string.welcome_screen_text,
            R.string.real_events_text,
            R.string.grow_balance_text,
            R.string.start_funds_text
    };

    @Override
    public int getCount() {
        return headlogos.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.walkthrough_page_one, container, false);

        ImageView ivIcon = (ImageView) view.findViewById(R.id.icon_wrapper);
        TextView tvTitle = (TextView) view.findViewById(R.id.betgame_title);
        TextView tvText = (TextView) view.findViewById(R.id.tv_walktrhough_text);

        ivIcon.setImageResource(icons[position]);
        tvTitle.setText(headlogos[position]);
        tvText.setText(descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
