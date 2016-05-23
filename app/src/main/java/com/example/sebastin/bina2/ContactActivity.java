package com.example.sebastin.bina2;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class ContactActivity extends AppCompatActivity {
    TextView actionTextView;
    TextView contactTextView;
    int[] stringResoruces = {R.string.contactString,R.string.aboutString};
    int[] actionStringResources = {R.string.contactActionString,R.string.actionAboutString};
    int currentStringResource;
    int menus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OPTIMA.TTF");
        actionTextView = (TextView)findViewById(R.id.actionText);
        actionTextView.setTypeface(typeface,Typeface.BOLD);
        contactTextView = (TextView)findViewById(R.id.contactText);
        contactTextView.setTypeface(typeface);
        actionTextView.setTextSize(20);
        Bundle bundle = getIntent().getExtras();
        menus = bundle.getInt("menuclick");
        currentStringResource = stringResoruces[menus];
        actionTextView.setText(actionStringResources[menus]);
        contactTextView.setText(currentStringResource);
    }
}
