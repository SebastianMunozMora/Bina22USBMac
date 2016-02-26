package com.example.sebastin.bina2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class ProjectActivity extends AppCompatActivity {
    public String projectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        EditText projectText;
        projectText = (EditText)findViewById(R.id.projectEditText);
        projectName = projectText.getText().toString();
    }
    public void recordActivity (View view){
        Intent intent = new Intent(this,RecordActivity.class);
        intent.putExtra("ProjectActivitiyprojectName", projectName);
        startActivity(intent);
    }
}
