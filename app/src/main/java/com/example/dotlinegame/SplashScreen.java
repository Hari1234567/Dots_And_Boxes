package com.example.dotlinegame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final ImageView dotImage=(ImageView) findViewById(R.id.dotImage);
        final ImageView andImage=(ImageView) findViewById(R.id.andImage);
        final ImageView boxesImage=(ImageView) findViewById(R.id.boxesImage);
        Animation lefttoMid1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.lefttomid);
        Animation lefttoMid2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.lefttomid);
        final Animation righttoMid= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.righttomid);
        final Animation midRight1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.midmoveright);
        final Animation midRight2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.midmoveright);
        final Animation midLeft= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.midmoveleft);
        final Animation exitLeft=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.leftexit);
        final Animation exitRight1=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rightexit);
        final Animation exitRight2=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rightexit);
         lefttoMid1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                    dotImage.startAnimation(midRight1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
         midRight1.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                  dotImage.startAnimation(exitRight1);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         lefttoMid2.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                  boxesImage.startAnimation(midRight2);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         midRight2.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 boxesImage.startAnimation(exitRight2);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         righttoMid.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                   andImage.startAnimation(midLeft);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         midLeft.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                   andImage.setAnimation(exitLeft);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         exitLeft.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                  andImage.setVisibility(View.INVISIBLE);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         exitRight1.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 dotImage.setVisibility(View.INVISIBLE);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         exitRight2.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 boxesImage.setVisibility(View.INVISIBLE);
                 Intent i = new Intent(SplashScreen.this, MainMenu.class);
                 startActivity(i);
                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                 finish();

             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
        lefttoMid1.setStartOffset(1000);


        dotImage.startAnimation(lefttoMid1);
        righttoMid.setStartOffset(1200);
        lefttoMid2.setStartOffset(1400);
         andImage.setAnimation(righttoMid);
        boxesImage.startAnimation(lefttoMid2);



    }
}
