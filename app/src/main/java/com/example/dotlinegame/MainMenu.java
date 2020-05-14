package com.example.dotlinegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {
boolean flag,AIflag;
public static boolean AIMode=false;
    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        SettingPane setting=new SettingPane();
        fragmentTransaction.setCustomAnimations(R.anim.rollup,R.anim.rolldown);
        flag=false;

        AIflag=false;
        try {
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("LaunchSetting")).commit();
        }catch(NullPointerException n){
            super.onBackPressed();
        }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag=false;
        setContentView(R.layout.activity_main_menu);
        Button humanBut=(Button)findViewById(R.id.humanPlay);
        humanBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SettingPane setting = new SettingPane();
                    fragmentTransaction.setCustomAnimations(R.anim.rollup, R.anim.rolldown);
                    fragmentTransaction.replace(R.id.settingsFragment, setting, "LaunchSetting").commit();
                    flag = true;
                    AIflag=false;

                }
                AIMode=false;

            }
        });
        Button aiBut=(Button)findViewById(R.id.AIPlay);
        aiBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AIflag) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SettingPane setting = new SettingPane();
                    fragmentTransaction.setCustomAnimations(R.anim.rollup, R.anim.rolldown);
                    fragmentTransaction.replace(R.id.settingsFragment, setting, "LaunchSetting").commit();
                    AIflag = true;
                    flag=false;

                }
                AIMode=true;
            }
        });


    }
    public void launchGame(){
        Intent i= new Intent(MainMenu.this,MainActivity.class);

        startActivity(i);

    }
}
