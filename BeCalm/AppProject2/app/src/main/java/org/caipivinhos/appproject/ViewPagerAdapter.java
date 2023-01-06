package org.caipivinhos.appproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Random;

public class ViewPagerAdapter extends PagerAdapter{

    private final Context context;
    private String[] recs = new String[5];
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    public void setString() {
        String[] tipsArray;

        tipsArray = context.getResources().getStringArray(R.array.tips);

        for(int i = 0; i<5 ; i++) {
            int randomIndex = new Random().nextInt(16);
            recs[i] = tipsArray[randomIndex];

        }
    }
    @Override
    public int getCount() {
        return recs.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        setString();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.rec_viewer, null);
        TextView textView = (TextView) view.findViewById(R.id.textViewer);
        textView.setText(recs[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View)object;
        vp.removeView(view);
    }
}
