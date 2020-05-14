package com.example.dotlinegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.dotlinegame.CustomView.CustomWidget;

public class MainActivity extends AppCompatActivity {
    public CustomWidget gameBoard;
    TextView score1,score2,score3,score4,score5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameBoard=(CustomWidget)findViewById(R.id.gameBoard);
        score1=(TextView)findViewById(R.id.playerScore1);
        score2 =(TextView)findViewById(R.id.playerScore2);
        score3=(TextView)findViewById(R.id.playerScore3);
        score4=(TextView)findViewById(R.id.playerScore4);
        score5=(TextView)findViewById(R.id.playerScore5);
        if(MainMenu.AIMode)
            SettingPane.numPlayers=2;
        switch(SettingPane.numPlayers){
            case 2:

                    score3.setVisibility(View.INVISIBLE);
                    score4.setVisibility(View.INVISIBLE);
                    score5.setVisibility(View.INVISIBLE);
                break;
            case 3:

                score4.setVisibility(View.INVISIBLE);
                score5.setVisibility(View.INVISIBLE);
                break;
            case 4:

                score5.setVisibility(View.INVISIBLE);
                break;


        }
        Button undoButton=(Button)findViewById(R.id.undoBut);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameBoard.undo();
            }
        });
    }
    public void startAnim(View v){
        v.startAnimation((Animation) getResources().getAnimation(android.R.anim.fade_in));
    }
    public void loadGameOver(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GameOver gameOver=new GameOver();
        fragmentTransaction.setCustomAnimations(R.anim.rollup, R.anim.rolldown);
        fragmentTransaction.replace(R.id.gameOverfragment, gameOver, "LaunchGameOver").commit();
        TextView[] badges=new TextView[5];
        Animation righttoMid= AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
        badges[0] = (TextView)findViewById(R.id.redbadge);
        badges[1] = (TextView)findViewById(R.id.bluebadge);
        badges[2] = (TextView)findViewById(R.id.greenbadge);
        badges[3] = (TextView)findViewById(R.id.yellowbadge);
        badges[4] = (TextView)findViewById(R.id.pinkbadge);
        for(int i=0;i<5;i++){
            badges[i].startAnimation(righttoMid);
        }
    }


}
