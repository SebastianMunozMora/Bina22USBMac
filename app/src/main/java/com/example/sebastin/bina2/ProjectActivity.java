package com.example.sebastin.bina2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import java.io.File;

public class ProjectActivity extends AppCompatActivity {
    public String projectName;
    public EditText projectText;
    public PopupMenu popupmenu;
    public String[] projectlist;
    public String projectSelected;
    public String directory = "/BinaRecordings";
    public File dir;
    public File root;
    public int it = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        root = Environment.getExternalStorageDirectory();
        projectText = (EditText)findViewById(R.id.projectEditText);
        projectName = projectText.getText().toString();
    }
    public void projectSelect (View view ){
        dir = new File(root.getAbsolutePath()+ directory);
        projectlist = dir.list();
        popupmenu = new PopupMenu(this,view);
        new Thread(new Task()).start();
    }
    public void setDirectory (View view){
        projectName = projectText.getText().toString();
    }
    public void recordActivity (View view){
        projectName = projectText.getText().toString();
        Intent intent = new Intent(this,RecordActivity.class);
        intent.putExtra("ProjectActivitiyprojectName", projectName);
        startActivity(intent);
    }

    public class Task implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < projectlist.length; i++) {
                it = i;
                popupmenu.getMenu().add(projectlist[it]);

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    MenuInflater menuInflater = popupmenu.getMenuInflater();
                    menuInflater.inflate(R.menu.popupdirectory,popupmenu.getMenu());
                    popupmenu.show();
                    PopUpEventHandler popUpEventHandler = new PopUpEventHandler(getApplicationContext());
                    popupmenu.setOnMenuItemClickListener(popUpEventHandler);
                }
            });
        }
    }
    public class PopUpEventHandler implements PopupMenu.OnMenuItemClickListener{
        Context context;
            public PopUpEventHandler (Context context){
                this.context = context;
            }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            projectSelected = item.getTitle().toString();
            projectText.setText(projectSelected);
            return true;
        }
    }
}
