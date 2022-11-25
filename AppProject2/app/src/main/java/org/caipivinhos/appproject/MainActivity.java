package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String username = "";
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    TextView tvMessage;
    private int dotscount;
    private ImageView[] dots;
    Button btstart;
    String prevStarted = "prevStarted";
    public final static String EXTRA_MESSAGE = "oi";
    SharedPreferences sharedpreferences;

    /*
    @Override
    protected void onResume() {
        super.onResume();

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String returnValue = data.getStringExtra(EXTRA_MESSAGE);
        System.out.println(returnValue);
        sharedpreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        sharedpreferences.edit().putString("username", returnValue).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        if (sharedpreferences.getBoolean(prevStarted, false)) {
            moveToSecondary();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        }
        else {
            username = sharedpreferences.getString("username", "Unknown");
        }

        tvMessage = findViewById(R.id.helloUser);
        tvMessage.setTextSize(30f);
        tvMessage.setText("Bem-vinda " + username);

        // Set the text view as the activity layout
        // setContentView(tvMessage);

        viewPager =(ViewPager) findViewById(R.id.recomendations);
        sliderDotspanel = (LinearLayout)findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        btstart = (Button) findViewById(R.id.btStart);
        btstart.setOnClickListener(view -> onBtStartClick());
    }


    void onBtStartClick() {
        Intent i = new Intent(this, PieChartActivity.class);
        startActivity(i);

    }
    public void moveToSecondary(){
        // use an intent to travel from one activity to another.
        Intent intent = new Intent(this,NamePrompt.class);
        startActivity(intent);
    }
}