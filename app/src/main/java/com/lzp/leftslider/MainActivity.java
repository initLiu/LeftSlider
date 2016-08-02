package com.lzp.leftslider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mContentView;
    private View mLeftView;
    private DrawerFrame mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = (LinearLayout) findViewById(R.id.root);
        mLeftView = LayoutInflater.from(this).inflate(R.layout.leftview_layout, null);
        mDrawer = new DrawerFrame(this, mContentView, (ViewGroup) mLeftView);
    }
}
