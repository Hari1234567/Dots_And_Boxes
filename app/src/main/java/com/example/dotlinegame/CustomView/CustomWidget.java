package com.example.dotlinegame.CustomView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dotlinegame.MainActivity;
import com.example.dotlinegame.MainMenu;
import com.example.dotlinegame.R;
import com.example.dotlinegame.SettingPane;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class CustomWidget extends View {
    boolean squareFilled = false;
    boolean boxForm=false;
    int numPlayers = 5;
    int turn = 0;
    Paint pointPaint;
    int[] scores;
    Paint[] linePaints, squarePaints;
    ArrayList<Float> pointsX, pointsY;
    float startX, startY, stopX, stopY;
    int dimension = 10;
    int spacing = 500 / dimension;
    ArrayList<Square> squares;
    ArrayList<Line> lines;
    MainActivity mainActivity;
    int totalFill = 0;
    int bestCount, otherCount, worstCount;
    int difficulty;
    public ArrayList<ArrayList<Integer>> chains;
    boolean gameOver=false;
    Paint recentLinePaint;
    Paint textPaint;
    ArrayList<Integer> bestIndices;
    ArrayList<Integer> otherIndices;
    ArrayList<Integer> worstIndices;
    ArrayList<Integer> worstPrimaryIndices;
    ArrayList<Integer> goodIndices;
    ArrayList<Integer> goodPrimaryIndices;
    ArrayList<Integer> otherPrimaryIndices;
    int pointGain;
    TextView playerIndicator;
    TextView[] badges;
    boolean over=false;
    float offset=pixelConvert(10);

    public CustomWidget(Context context) {
        super(context);
        init(null);
    }

    public CustomWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null);
    }

    public CustomWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public CustomWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attr) {
        gameOver=false;
        over=false;

        chains = new ArrayList<ArrayList<Integer>>();
        textPaint=new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(45);

        recentLinePaint=new Paint();
        recentLinePaint.setColor(Color.GRAY);
        recentLinePaint.setStrokeWidth(10);
        pointPaint = new Paint();
        squares = new ArrayList<Square>();
        dimension = SettingPane.dimension;
        numPlayers = SettingPane.numPlayers;
        if (MainMenu.AIMode) numPlayers = 2;
        difficulty = SettingPane.numPlayers - 1;
        otherCount = numPlayers * numPlayers;
        worstCount = 0;
        bestCount = 0;
        mainActivity = (MainActivity) getContext();
        scores = new int[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            scores[i] = 0;
        }
        TextView txt;

        spacing = 350 / dimension;
        pointPaint.setColor(Color.BLACK);
        linePaints = new Paint[numPlayers];
        squarePaints = new Paint[numPlayers];
        lines = new ArrayList<Line>();
        squares = new ArrayList<Square>();
        for (int i = 0; i < numPlayers; i++) {
            squarePaints[i] = new Paint();
            linePaints[i] = new Paint();
        }


        for (int i = 0; i < numPlayers; i++) {
            switch (i) {
                case 0:
                    linePaints[0].setColor(Color.RED);
                    squarePaints[0].setColor(getResources().getColor(R.color.darkRed));
                    break;
                case 1:
                    linePaints[1].setColor(Color.BLUE);
                    squarePaints[1].setColor(getResources().getColor(R.color.darkBlue));
                    break;
                case 2:
                    linePaints[2].setColor(Color.GREEN);
                    squarePaints[2].setColor(getResources().getColor(R.color.darkGreen));
                    break;
                case 3:
                    linePaints[3].setColor(Color.YELLOW);
                    squarePaints[3].setColor(getResources().getColor(R.color.darkYellow));
                    break;
                case 4:
                    linePaints[4].setColor(getResources().getColor(R.color.pink));
                    squarePaints[4].setColor(getResources().getColor(R.color.purple));
                    break;

            }

        }
        for (int i = 0; i < numPlayers; i++) {
            linePaints[i].setStrokeWidth(10);

        }
        linePaints[1].setStrokeWidth(10);
        pointsX = new ArrayList<Float>();
        pointsY = new ArrayList<Float>();
        offset=pixelConvert(spacing/2);
        for (int i = 0; i < dimension; i++) {
            pointsX.add((i+1) * pixelConvert(spacing)-offset);
            pointsY.add((i + 1) * pixelConvert(spacing));

        }
        for (int i = 1; i < dimension; i++) {
            for (int j = 1; j < dimension; j++) {
                squares.add(new Square(pointsX.get(i - 1), pointsY.get(j - 1), pointsX.get(i), pointsY.get(j - 1), pointsX.get(i), pointsY.get(j), pointsX.get(i - 1), pointsY.get(j)));

            }
        }

        startX = startY = stopX = stopY = 0;
        badges=new TextView[5];

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int totalFill = 0;


        badges[0] = (TextView) mainActivity.findViewById(R.id.redbadge);
        badges[1] = (TextView) mainActivity.findViewById(R.id.bluebadge);
        badges[2] = (TextView) mainActivity.findViewById(R.id.greenbadge);
        badges[3] = (TextView) mainActivity.findViewById(R.id.yellowbadge);
        badges[4] = (TextView) mainActivity.findViewById(R.id.pinkbadge);


        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                canvas.drawCircle(pointsX.get(i), pointsY.get(j), 7, pointPaint);

            }
        }
        for (int i = 0; i < lines.size(); i++) {
            if (i == lines.size() - 1) {
                canvas.drawLine(lines.get(i).getStartX(), lines.get(i).getStartY(), lines.get(i).getStopX(), lines.get(i).getStopY(), recentLinePaint);
            } else
                canvas.drawLine(lines.get(i).getStartX(), lines.get(i).getStartY(), lines.get(i).getStopX(), lines.get(i).getStopY(), linePaints[lines.get(i).getPlayerID()]);
        }
        scores[1]=0;
        for (int i = 0; i < squares.size(); i++) {
            if (squares.get(i).getFillFactor() == 4) {
                totalFill++;
                Rect rect = new Rect((int) squares.get(i).getX(0), (int) squares.get(i).getY(0), (int) squares.get(i).getX(2), (int) squares.get(i).getY(2));
                canvas.drawRect(rect, squarePaints[squares.get(i).getPlayerID()]);
                if(squares.get(i).getPlayerID()==1){
                    scores[1]++;
                }
            }
        }

        TextView txt=(TextView)mainActivity.findViewById(R.id.playerScore2);
        txt.setText("BLUE:                     " + Integer.toString(scores[1]));
        Activity parentActivity = (Activity) getContext();
        playerIndicator = (TextView) parentActivity.findViewById(R.id.playerIndicator);
        playerIndicator.setBackgroundColor(squarePaints[turn].getColor());
        Button undoBut = (Button) parentActivity.findViewById(R.id.undoBut);

        undoBut.setEnabled(lines.size() != 0);
        if (totalFill == (dimension - 1) * (dimension - 1)) {
            undoBut.setEnabled(false);
            if (!gameOver) {
                mainActivity.loadGameOver();
                gameOver = true;


                Vibrator v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);

                }


                int first, second, third;
                first = second = third = -1;

                for (int i = 0; i < numPlayers; i++) {
                    if (scores[i] >first) {
                        third = second;
                        second = first;
                        first = scores[i];
                    } else if (scores[i] > second && scores[i]!=first) {
                        third = second;
                        second = scores[i];
                    } else if (scores[i] > third&& scores[i]!=second) {
                        third = scores[i];
                    }
                }
                //Toast.makeText(getContext(),Integer.toString(first)+Integer.toString(second)+Integer.toString(third),Toast.LENGTH_SHORT).show();
                for (int i = 0; i < numPlayers; i++) {

                    if (scores[i] == first) {
                        badges[i].setBackground(getResources().getDrawable(R.drawable.gold));
                        badges[i].setText("1");
                        badges[i].setTextColor(Color.WHITE);


                    } else if (scores[i] == second) {
                        badges[i].setBackground(getResources().getDrawable(R.drawable.silver));
                        badges[i].setText("2");
                        badges[i].setTextColor(Color.WHITE);

                    } else if (scores[i] == third) {
                        badges[i].setBackground(getResources().getDrawable(R.drawable.bronze));
                        badges[i].setText("3");
                        badges[i].setTextColor(Color.WHITE);

                    }
                }
            }
        }

    }
    boolean lineadded=false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean value = super.onTouchEvent(event);
        if(!gameOver) {
            if (turn == 0 || !MainMenu.AIMode) {
                try {
                    boolean flag = true;

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            startX = event.getX();
                            startY = event.getY();

                            if ((startX+offset) < pointsX.get(pointsX.size()-1) && startY < pixelConvert(spacing) * dimension) {
                                if (startX / pixelConvert(spacing) - (int) (startX / pixelConvert(spacing) )<= 0.5f || startX > pixelConvert(spacing * dimension)) {
                                    startX = pointsX.get((int) (startX) / (int) pixelConvert(spacing));

                                } else if ((startX+offset) / pixelConvert(spacing) - (int) (startX /  pixelConvert(spacing)) > 0.5f) {
                                    startX = pointsX.get((int) (startX) / (int) pixelConvert(spacing)) + pixelConvert(spacing);

                                }
                                if (startY / pixelConvert(spacing) - (int) startY / (int) pixelConvert(spacing) <= 0.5f || startY > pixelConvert(spacing * dimension)) {
                                    startY = pointsY.get((int) (startY) / (int) pixelConvert(spacing));
                                } else if (startY / pixelConvert(spacing) - (int) startY / (int) pixelConvert(spacing) > 0.5f) {
                                    startY = pointsY.get((int) (startY) / (int) pixelConvert(spacing)) + pixelConvert(spacing);
                                }
                                startX -= pixelConvert(spacing);
                                startY -= pixelConvert(spacing);
                            } else {
                                if (startX > dimension * pixelConvert(spacing)-offset) {
                                    startX = pointsX.get(pointsX.size()-1);
                                    if (startY / pixelConvert(spacing) - (int) startY / (int) pixelConvert(spacing) <= 0.5f || startY > pixelConvert(spacing * dimension)) {
                                        startY = pointsY.get((int) (startY) / (int) pixelConvert(spacing));
                                    } else if (startY / pixelConvert(spacing) - (int) startY / (int) pixelConvert(spacing) > 0.5f) {
                                        startY = pointsY.get((int) (startY) / (int) pixelConvert(spacing)) + pixelConvert(spacing);
                                    }
                                    startY -= pixelConvert(spacing);
                                }
                                if (startY > dimension * pixelConvert(spacing)) {
                                    startY = dimension * pixelConvert(spacing);

                                    if (startX / pixelConvert(spacing) - (int) startX / (int) pixelConvert(spacing) <= 0.5f || startX > pixelConvert(spacing * dimension)) {
                                        startX = pointsX.get((int) (startX) / (int) pixelConvert(spacing));
                                    } else if (startX / pixelConvert(spacing) - (int) startX / (int) pixelConvert(spacing) > 0.5f) {
                                        startX = pointsX.get((int) (startX) / (int) pixelConvert(spacing)) + pixelConvert(spacing);
                                    }
                                    startX -= pixelConvert(spacing);
                                }

                            }
                            stopX = startX;
                            stopY = startY;
                            if (flag && startX >= pixelConvert(spacing)-offset && startY >= pixelConvert(spacing) && (startX+offset)%pixelConvert(spacing)==0) {
                                lineadded = true;
                                lines.add(new Line(startX, startY, stopX, stopY));
                            }

                            postInvalidate();


                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //if (!lines.get(lines.size() - 1).isReady()) {

                            stopX = event.getX();
                            stopY = event.getY();

                            if (dist(startX, startY, stopX, stopY) > pixelConvert(spacing)) {
                                if (Math.abs(startX - stopX) > Math.abs(startY - stopY)) {
                                    stopY = startY;
                                    stopX = startX + sign(stopX - startX) * pixelConvert(spacing);
                                } else {
                                    stopX = startX;
                                    stopY = startY + sign(stopY - startY) * pixelConvert(spacing);
                                }
                            }
                            if (lineadded) {
                                lines.get(lines.size() - 1).setStopX(stopX);
                                lines.get(lines.size() - 1).setStopY(stopY);
                                lines.get(lines.size() - 1).setPlayerID(turn);
                            }
                            postInvalidate();

                            return true;

                        case MotionEvent.ACTION_UP:


                            boolean isEqual = false;
                            for (int i = 0; i < lines.size() - 1; i++) {
                                if (lines.get(i).isEqual(lines.get(lines.size() - 1))) {
                                    isEqual = true;
                                    break;
                                }
                            }
                            float startX = lines.get(lines.size() - 1).getStartX();
                            float startY = lines.get(lines.size() - 1).getStartY();
                            float stopX = lines.get(lines.size() - 1).getStopX();
                            float stopY = lines.get(lines.size() - 1).getStopY();
                            if (((dist(startX, startY, stopX, stopY) < pixelConvert(spacing) || (startX - stopX) * (startY - stopY) != 0) || isEqual || (stopX > pixelConvert(dimension * spacing)-offset) || (stopY > pixelConvert(dimension * spacing)) || (stopX < pixelConvert(spacing)-offset) || (stopY < pixelConvert(spacing)))) {
                                lines.remove(lines.size() - 1);
                                lineadded=false;
                                postInvalidate();
                            } else {

                                flag = false;
                                cut = false;
                                cutOFF = 0;
                                bestCount = 0;
                                worstCount = 0;
                                otherCount = 0;
                                for (int i = 0; i < squares.size(); i++) {
                                    if (squares.get(i).isEdge(lines.get(lines.size() - 1))) {
                                        if (squares.get(i).getFillFactor() < 4) {

                                            squares.get(i).calculateFillFator();

                                        }
                                        if (squares.get(i).getFillFactor() == 4) {

                                            MediaPlayer waterDrop = MediaPlayer.create(getContext(), R.raw.boxformsound);

                                            waterDrop.start();

                                            squares.get(i).setPlayerID(turn);
                                            squareFilled = true;
                                            scores[turn]++;
                                            totalFill++;
                                            TextView scoreText;
                                            switch (turn) {

                                                case 0:
                                                    scoreText = (TextView) mainActivity.findViewById(R.id.playerScore1);
                                                    scoreText.setText("RED:                       " + Integer.toString(scores[turn]));
                                                    break;
                                                case 1:
                                                    scoreText = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                                    scoreText.setText("BLUE:                     " + Integer.toString(scores[turn]));
                                                    break;
                                                case 2:
                                                    scoreText = (TextView) mainActivity.findViewById(R.id.playerScore3);
                                                    scoreText.setText("GREEN:                 " + Integer.toString(scores[turn]));
                                                    break;
                                                case 3:
                                                    scoreText = (TextView) mainActivity.findViewById(R.id.playerScore4);
                                                    scoreText.setText("YELLOW:              " + Integer.toString(scores[turn]));
                                                    break;
                                                case 4:
                                                    scoreText = (TextView) mainActivity.findViewById(R.id.playerScore5);
                                                    scoreText.setText("PINK:                     " + Integer.toString(scores[turn]));
                                                    break;
                                            }
                                        }

                                    }

                                }
                                if (!squareFilled) {

                                    if ( lineadded) {
                                        if(MainMenu.AIMode) {
                                            turn = 1;
                                            AIturn();
                                        }
                                        else
                                            turn = (turn + 1) % numPlayers;


                                        MediaPlayer waterDrop = MediaPlayer.create(getContext(), R.raw.waterdropsound);
                                        waterDrop.start();


                                    }
                                    if (!lineadded && MainMenu.AIMode) {
                                        turn = 0;
                                    }
                                } else
                                    squareFilled = false;
                                postInvalidate();


                            }
                            lineadded = false;

                            return false;

                    }

                } catch (IndexOutOfBoundsException ignored) {

                }
            }

        }
        return value;

    }

    float pixelConvert(float dp) {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    float dist(float startX, float startY, float endX, float endY) {

        return (float) Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
    }

    int sign(float diff) {
        if (Math.abs(diff) < 20) {
            return 0;
        }
        return (int) Math.signum(diff);
    }
    boolean sacrifice=false;
    boolean cut=false;
    int cutOFF=0;
    public void AIturn() {
        final int lineSize=lines.size();
        final Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(scores[0]+scores[1]<(dimension-1)*(dimension-1)) {

                    Log.i("SATA", "AITurn");
                    int bestIterations = 0;
                    cutOFF++;
                    if (cutOFF > 3) {
                        cutOFF = 0;
                        sacrifice = false;
                        cut = true;
                    }
                    int smallIndex = 0;

                    int goodSize = 0;
                    int otherSize = 0;
                    int worstSize = 0;
                    Random random = new Random();
                    bestIndices = new ArrayList<Integer>();
                    otherIndices = new ArrayList<Integer>();
                    worstIndices = new ArrayList<Integer>();
                    worstPrimaryIndices = new ArrayList<Integer>();
                    goodIndices = new ArrayList<Integer>();
                    goodPrimaryIndices = new ArrayList<Integer>();
                    otherPrimaryIndices = new ArrayList<Integer>();
                    chains.clear();
                    scores[1] = 0;

                    for (int i = 0; i < squares.size(); i++) {
                        switch (squares.get(i).getFillFactor()) {
                            case 0:
                                if (squares.get(i).getGoodEdges().size() != 0) {
                                    otherPrimaryIndices.add(i);
                                } else {
                                    otherIndices.add(i);
                                }

                                otherIndices.add(i);

                                break;
                            case 1:
                                if (squares.get(i).getGoodEdges().size() != 0) {
                                    goodPrimaryIndices.add(i);
                                } else {
                                    goodIndices.add(i);
                                }

                                break;
                            case 2:

                                worstIndices.add(i);


                                break;
                            case 3:
                                bestIndices.add(i);
                                break;
                        }
                        if (squares.get(i).getFillFactor() == 4 && squares.get(i).getPlayerID() == 1) {
                            scores[1]++;
                        }
                    }

                    chains.clear();
                    for (int i = 0; i < squares.size(); i++) {
                        chains.add(new ArrayList<Integer>());
                    }

                    if (goodIndices.size() != 0) {
                        for (int i = 0; i < goodIndices.size(); i++) {
                            chains.get(goodIndices.get(i)).add(goodIndices.get(i));
                            squares.get(goodIndices.get(i)).getCluster(goodIndices.get(i));

                        }
                    }


                    if (otherIndices.size() != 0) {
                        for (int i = 0; i < otherIndices.size(); i++) {
                            chains.get(otherIndices.get(i)).add(otherIndices.get(i));
                            squares.get(otherIndices.get(i)).getCluster(otherIndices.get(i));

                        }
                    }

                    if (worstIndices.size() != 0) {
                        for (int i = 0; i < worstIndices.size(); i++) {
                            chains.get(worstIndices.get(i)).add(worstIndices.get(i));
                            squares.get(worstIndices.get(i)).getCluster(worstIndices.get(i));

                        }
                    }

                    if ((bestIndices.size() != 0) && !sacrifice) {

                        pointGain = 0;

                        while (bestIndices.size() != 0) {
                            bestIterations++;





                            lines.add(squares.get(bestIndices.get(0)).getFreeEdges());
                            lines.get(lines.size() - 1).setPlayerID(1);
                            boxForm=true;

                            for (int k = 0; k < squares.size(); k++) {
                                if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                    if (squares.get(k).getFillFactor() != 4)
                                        squares.get(k).calculateFillFator();
                                    if (squares.get(k).getFillFactor() == 4) {
                                        scores[1]++;
                                        pointGain++;


                                        TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                        txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                        squares.get(k).setPlayerID(1);
                                    }
                                }

                            }

                            bestIndices.clear();
                            worstIndices.clear();
                            goodIndices.clear();
                            goodPrimaryIndices.clear();
                            otherPrimaryIndices.clear();
                            otherIndices.clear();
                            for (int i = 0; i < squares.size(); i++) {
                                squares.get(i).calculateFillFator();
                                switch (squares.get(i).getFillFactor()) {
                                    case 0:
                                        if (squares.get(i).getGoodEdges().size() != 0) {
                                            otherPrimaryIndices.add(i);
                                        } else {
                                            otherIndices.add(i);
                                        }

                                        otherIndices.add(i);

                                        break;
                                    case 1:
                                        if (squares.get(i).getGoodEdges().size() != 0) {
                                            goodPrimaryIndices.add(i);
                                        } else {
                                            goodIndices.add(i);
                                        }

                                        break;
                                    case 2:

                                        worstIndices.add(i);


                                        break;
                                    case 3:
                                        bestIndices.add(i);
                                        break;
                                }
                            }


                        }



                        if (difficulty == 3 && !cut && (scores[1] + scores[0] < (dimension - 1) * (dimension - 1))) {

                            chains.clear();
                            int smallSize;
                            smallIndex = 0;
                            smallSize = 9999;
                            for (int i = 0; i < squares.size(); i++) {
                                chains.add(new ArrayList<Integer>());
                                if (squares.get(i).getFillFactor() != 4 && squares.get(i).getFillFactor() != 3) {
                                    chains.get(i).add(i);
                                    squares.get(i).getCluster(i);
                                    if (chains.get(i).size() < smallSize && chains.get(i).size() != 0) {
                                        smallSize = chains.get(i).size();
                                        smallIndex = i;
                                    }
                                }
                            }
                            ArrayList<Line> temp = new ArrayList<Line>();
                            if (smallSize > pointGain) {
                                if (bestIterations > 3) {
                                    for (int i = 0; i < 3; i++) {
                                        temp.add(lines.get(lines.size() - 1));
                                        lines.remove(lines.get(lines.size() - 1));
                                    }
                                } else {
                                    boxForm=false;
                                    for (int i = 0; i < bestIterations; i++) {
                                        temp.add(lines.get(lines.size() - 1));
                                        lines.remove(lines.get(lines.size() - 1));
                                    }
                                }
                            }
                            int newSmallSizes = 9999;
                            chains.clear();
                            for (int i = 0; i < squares.size(); i++) {
                                squares.get(i).calculateFillFator();
                            }
                            for (int i = 0; i < squares.size(); i++) {
                                chains.add(new ArrayList<Integer>());
                                if (squares.get(i).getFillFactor() != 4 && squares.get(i).getFillFactor() != 3) {
                                    chains.get(i).add(i);
                                    squares.get(i).getCluster(i);
                                    if (chains.get(i).size() < newSmallSizes && chains.get(i).size() != 0) {
                                        newSmallSizes = chains.get(i).size();
                                        smallIndex = i;
                                    }
                                }
                            }

                            if (newSmallSizes < pointGain) {

                                sacrifice = true;

                            } else {

                                for (int i = 0; i < temp.size(); i++) {
                                    boxForm=true;
                                    lines.add(temp.get(i));
                                    lines.get(lines.size() - 1).setPlayerID(1);
                                }

                                sacrifice = false;
                            }
                            for (int i = 0; i < squares.size(); i++) {
                                squares.get(i).calculateFillFator();
                            }
                            AIturn();
                        } else {
                            if (cut) {
                                cutOFF = 0;
                                cut = false;
                            }
                            scores[1]=0;


                            AIturn();
                        }


                    } else if (otherPrimaryIndices.size() != 0 && ((difficulty == 3) || (difficulty == 2))) {
                        sacrifice = false;
                        ArrayList<Line> goodLines = squares.get(otherPrimaryIndices.get(random.nextInt(otherPrimaryIndices.size()))).getGoodEdges();
                        lines.add(goodLines.get(random.nextInt(goodLines.size())));
                        lines.get(lines.size() - 1).setPlayerID(1);
                        for (int k = 0; k < squares.size(); k++) {
                            if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                squares.get(k).calculateFillFator();
                                if (squares.get(k).getFillFactor() == 4) {
                                    scores[1]++;

                                    TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                    txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                    squares.get(k).setPlayerID(1);
                                }
                            }
                        }

                    } else if (goodPrimaryIndices.size() != 0 && ((difficulty == 3) || (difficulty == 2))) {

                        sacrifice = false;

                        ArrayList<Line> goodLines = squares.get(goodPrimaryIndices.get(random.nextInt(goodPrimaryIndices.size()))).getGoodEdges();
                        lines.add(goodLines.get(random.nextInt(goodLines.size())));
                        lines.get(lines.size() - 1).setPlayerID(1);
                        for (int k = 0; k < squares.size(); k++) {
                            if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                squares.get(k).calculateFillFator();
                                if (squares.get(k).getFillFactor() == 4) {
                                    scores[1]++;

                                    TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                    txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                    squares.get(k).setPlayerID(1);
                                }
                            }
                        }


                    } else if (difficulty == 3 || difficulty ==2) {
                        chains.clear();
                        int smallSize;
                        smallIndex = 0;
                        smallSize = 9999;
                        for (int i = 0; i < squares.size(); i++) {
                            chains.add(new ArrayList<Integer>());
                            if (squares.get(i).getFillFactor() != 4 && squares.get(i).getFillFactor() != 3) {
                                chains.get(i).add(i);
                                squares.get(i).getCluster(i);
                                if (chains.get(i).size() < smallSize && chains.get(i).size() != 0) {
                                    smallSize = chains.get(i).size();
                                    smallIndex = i;
                                }
                            }
                        }

                        Line l = squares.get(smallIndex).getFreeEdges(true);
                        if (l != null) {
                            lines.add(squares.get(smallIndex).getFreeEdges(true));
                            lines.get(lines.size() - 1).setPlayerID(1);
                            for (int k = 0; k < squares.size(); k++) {
                                if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                    squares.get(k).calculateFillFator();
                                    if (squares.get(k).getFillFactor() == 4) {
                                        scores[1]++;

                                        TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                        txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                        squares.get(k).setPlayerID(1);
                                    }
                                }
                            }
                        }
                        else{
                            while (bestIndices.size() != 0) {
                                bestIterations++;


                                lines.add(squares.get(bestIndices.get(0)).getFreeEdges());
                                lines.get(lines.size() - 1).setPlayerID(1);
                                boxForm=true;
                                for (int k = 0; k < squares.size(); k++) {
                                    if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                        if (squares.get(k).getFillFactor() != 4)
                                            squares.get(k).calculateFillFator();
                                        if (squares.get(k).getFillFactor() == 4) {
                                            scores[1]++;
                                            TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                            txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                            squares.get(k).setPlayerID(1);
                                        }
                                    }


                                }

                                bestIndices.clear();
                                worstIndices.clear();
                                goodIndices.clear();
                                goodPrimaryIndices.clear();
                                otherPrimaryIndices.clear();
                                otherIndices.clear();
                                for (int i = 0; i < squares.size(); i++) {
                                    squares.get(i).calculateFillFator();
                                    switch (squares.get(i).getFillFactor()) {
                                        case 0:
                                            if (squares.get(i).getGoodEdges().size() != 0) {
                                                otherPrimaryIndices.add(i);
                                            } else {
                                                otherIndices.add(i);
                                            }

                                            otherIndices.add(i);

                                            break;
                                        case 1:
                                            if (squares.get(i).getGoodEdges().size() != 0) {
                                                goodPrimaryIndices.add(i);
                                            } else {
                                                goodIndices.add(i);
                                            }

                                            break;
                                        case 2:

                                            worstIndices.add(i);


                                            break;
                                        case 3:
                                            bestIndices.add(i);
                                            break;
                                    }
                                }
                            }
                            AIturn();

                        }

                        sacrifice = false;
                    } else if (goodIndices.size() != 0) {

                        sacrifice = false;
                        // Toast.makeText(getContext(),"good",Toast.LENGTH_SHORT).show();
                        lines.add(squares.get(goodIndices.get(random.nextInt(goodIndices.size()))).getFreeEdges());

                        lines.get(lines.size() - 1).setPlayerID(1);
                        for (int k = 0; k < squares.size(); k++) {
                            if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                squares.get(k).calculateFillFator();
                                if (squares.get(k).getFillFactor() == 4) {
                                    scores[1]++;

                                    TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                    txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                    squares.get(k).setPlayerID(1);
                                }
                            }
                        }

                    } else if (otherIndices.size() != 0) {

                        sacrifice = false;
                        lines.add(squares.get(otherIndices.get(random.nextInt(otherIndices.size()))).getFreeEdges());

                        lines.get(lines.size() - 1).setPlayerID(1);
                        for (int k = 0; k < squares.size(); k++) {
                            if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                squares.get(k).calculateFillFator();
                                if (squares.get(k).getFillFactor() == 4) {
                                    scores[1]++;

                                    TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                    txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                    squares.get(k).setPlayerID(1);
                                }
                            }
                        }

                    } else if (worstIndices.size() != 0) {

                        sacrifice = false;
                        lines.add(squares.get(worstIndices.get(random.nextInt(worstIndices.size()))).getFreeEdges());
                        lines.get(lines.size() - 1).setPlayerID(1);

                        for (int k = 0; k < squares.size(); k++) {
                            if (squares.get(k).isEdge(lines.get(lines.size() - 1))) {
                                squares.get(k).calculateFillFator();
                                if (squares.get(k).getFillFactor() == 4) {
                                    scores[1]++;


                                    TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                                    txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                                    squares.get(k).setPlayerID(1);
                                }
                            }
                        }

                    } else {

                    }


                    turn = 0;
                    postInvalidate();
                    if(!boxForm){
                        if(lineSize<lines.size()) {
                            MediaPlayer waterDrop = MediaPlayer.create(getContext(), R.raw.waterdropsound);
                            waterDrop.start();
                        }
                    }else{
                        MediaPlayer waterDrop=MediaPlayer.create(getContext(),R.raw.boxformsound);
                        waterDrop.start();
                        boxForm=false;
                    }
                }

            }
        },400);

    }

    public void undo() {
        MediaPlayer waterDrop=MediaPlayer.create(getContext(),R.raw.erasersound);
        waterDrop.start();

        cut = false;
        cutOFF = 0;
        try {
            if (MainMenu.AIMode) {
                while (lines.get(lines.size() - 1).getPlayerID() == 1) {

                    for (int i = 0; i < squares.size(); i++) {
                        if (squares.get(i).isEdge(lines.get(lines.size() - 1))) {

                            squares.get(i).decreaseFillFactor();
                            if (squares.get(i).getFillFactor() == 3) {
                                scores[1]--;

                            }
                        }
                    }
                    lines.remove(lines.size() - 1);


                }
                for (int i = 0; i < squares.size(); i++) {
                    if (squares.get(i).isEdge(lines.get(lines.size() - 1))) {

                        squares.get(i).decreaseFillFactor();
                        if (squares.get(i).getFillFactor() == 3) {
                            scores[0]--;

                        }
                    }
                }
                lines.remove(lines.size() - 1);
                TextView txt = (TextView) mainActivity.findViewById(R.id.playerScore1);
                txt.setText("RED:                       " + Integer.toString(scores[0]));
                txt = (TextView) mainActivity.findViewById(R.id.playerScore2);
                txt.setText("BLUE:                     " + Integer.toString(scores[1]));

                turn = 0;
            } else {
                boolean flag = true;
                for (int i = 0; i < squares.size(); i++) {
                    if (squares.get(i).isEdge(lines.get(lines.size() - 1))) {
                        if (squares.get(i).getFillFactor() == 4) {
                            flag = false;
                        }
                        squares.get(i).decreaseFillFactor();
                        if (squares.get(i).getFillFactor() == 3) {
                            scores[squares.get(i).getPlayerID()]--;

                        }
                    }
                }
                lines.remove(lines.size() - 1);


                TextView scoreText;
                switch (turn) {

                    case 0:
                        scoreText = (TextView) mainActivity.findViewById(R.id.playerScore1);
                        scoreText.setText("RED:                       " + Integer.toString(scores[turn]));
                        break;
                    case 1:
                        scoreText = (TextView) mainActivity.findViewById(R.id.playerScore2);
                        scoreText.setText("BLUE:                     " + Integer.toString(scores[turn]));
                        break;
                    case 2:
                        scoreText = (TextView) mainActivity.findViewById(R.id.playerScore3);
                        scoreText.setText("GREEN:                 " + Integer.toString(scores[turn]));
                        break;
                    case 3:
                        scoreText = (TextView) mainActivity.findViewById(R.id.playerScore4);
                        scoreText.setText("YELLOW:              " + Integer.toString(scores[turn]));
                        break;
                    case 4:
                        scoreText = (TextView) mainActivity.findViewById(R.id.playerScore5);
                        scoreText.setText("PINK:                     " + Integer.toString(scores[turn]));
                        break;
                }
                if (flag) {
                    if (turn == 0) {
                        turn = numPlayers - 1;
                    } else {
                        turn = (turn - 1) % numPlayers;
                    }
                }

            }

            postInvalidate();
        } catch (Exception e) {
        }

    }

    class Line {

        private float startX, startY, stopX, stopY;
        private int playerID;

        Line(float startX, float startY, float stopX, float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
            // playerID=-1;
        }

        int getPlayerID() {
            return playerID;
        }

        void setPlayerID(int id) {
            playerID = id;
        }

        float getStartX() {
            return startX;
        }

        float getStartY() {

            return startY;
        }

        float getStopX() {
            return stopX;
        }

        float getStopY() {
            return stopY;
        }

        void setStartX(float val) {
            startX = val;
        }

        void setStartY(float val) {
            startY = val;
        }

        void setStopX(float val) {
            stopX = val;
        }

        void setStopY(float val) {
            stopY = val;
        }

        boolean isEqual(Line l) {
            return (startX == l.getStartX() && startY == l.getStartY() && stopX == l.getStopX() && stopY == l.getStopY()) || (startX == l.getStopX() && startY == l.getStopY() && stopX == l.getStartX() && stopY == l.getStartY());

        }


    }

    class Square {

        private float[] pointsX;
        private float[] pointsY;
        private int fillFactor;
        private int playerID;
        private  float centerX,centerY;


        Square(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
            fillFactor = 0;
            pointsX = new float[4];
            pointsY = new float[4];
            pointsX[0] = x1;
            pointsX[1] = x2;
            pointsX[2] = x3;
            pointsX[3] = x4;
            pointsY[0] = y1;
            pointsY[1] = y2;
            pointsY[2] = y3;
            pointsY[3] = y4;
            centerX=(x1+x2)/2;
            centerY=(y1+y2)/2;

        }
        float getCenterX(){
            return centerX;
        }
        float getCenterY(){
            return centerY;
        }

        int getPlayerID() {
            return playerID;
        }

        void setPlayerID(int id) {
            playerID = id;
        }


        void calculateFillFator(){
            fillFactor=0;
            for(int i=0;i<lines.size();i++){
                if(isEdge(lines.get(i))){
                    fillFactor++;
                }
            }
        }
        void decreaseFillFactor(){
            fillFactor--;
        }

        int getFillFactor() {
            return fillFactor;
        }

        boolean isEdge(Line l) {
            boolean edge = false;
            for (int i = 0; i < 3; i++) {
                if (l.isEqual(new Line(pointsX[i], pointsY[i], pointsX[i + 1], pointsY[i + 1]))) {
                    edge = true;
                    break;
                }
            }
            if (l.isEqual(new Line(pointsX[3], pointsY[3], pointsX[0], pointsY[0]))) {
                edge = true;

            }

            return edge;
        }

        float getX(int id) {
            return pointsX[id];
        }

        float getY(int id) {
            return pointsY[id];
        }

        public Line getEdge(int id) {
            switch (id) {
                case 0:
                case 1:
                case 2:
                    return new Line(pointsX[id], pointsY[id], pointsX[id + 1], pointsY[id + 1]);

                default:
                    return new Line(pointsX[3], pointsY[3], pointsX[0], pointsY[0]);

            }
        }

        public Line getFreeEdges(){

            ArrayList<Line> freeEdges=new ArrayList<Line>();
            for(int i=0;i<4;i++){
                boolean flag=true;
                for(int j=0;j<lines.size();j++){
                    if(getEdge(i).isEqual(lines.get(j))){
                        flag=false;
                        break;
                    }
                }
                for(int j=0;j<squares.size();j++){
                    if(squares.get(j).isEdge(getEdge(i))&&squares.get(j)!=this){

                    }
                }
                if(flag)freeEdges.add(getEdge(i));
            }

            try {
                return freeEdges.get(new Random().nextInt(freeEdges.size()));
            }catch(Exception e){
                // Toast.makeText(getContext(),"LOL",Toast.LENGTH_SHORT).show();
                return getEdge(0);

            }
        }
        public Line getFreeEdges(boolean filter){

            ArrayList<Line> freeEdges=new ArrayList<Line>();
            for(int i=0;i<4;i++){
                boolean flag=true;
                for(int j=0;j<lines.size();j++){
                    if(getEdge(i).isEqual(lines.get(j))){
                        flag=false;
                        break;
                    }
                }
                for(int j=0;j<squares.size();j++){
                    if(squares.get(j).isEdge(getEdge(i))&&squares.get(j)!=this){
                        if(squares.get(j).getFillFactor()==3){
                            flag=false;
                        }
                    }
                }
                if(flag)freeEdges.add(getEdge(i));
            }

            try {
                return freeEdges.get(new Random().nextInt(freeEdges.size()));
            }catch(Exception e){
                // Toast.makeText(getContext(),"LOL",Toast.LENGTH_SHORT).show();
                return null;

            }
        }

        public ArrayList<Line> getGoodEdges(){
            ArrayList<Line> goodEdges=new ArrayList<Line>();
            if(getFillFactor()!=2) {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < squares.size(); j++) {
                        if (squares.get(j).isEdge(getEdge(i))) {
                            if (squares.get(j) != this) {
                                if (squares.get(j).getFillFactor() == 0 || squares.get(j).getFillFactor() == 1) {
                                    boolean flag = true;
                                    for (int k = 0; k < lines.size(); k++) {
                                        if (getEdge(i).isEqual(lines.get(k))) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        goodEdges.add(getEdge(i));

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                ArrayList<Square> neighbours=new ArrayList<Square>();
                for(int i=0;i<4;i++){
                    for(int j=0;j<squares.size();j++){
                        if(squares.get(j).isEdge(getEdge(i))){
                            boolean flag=true;
                            for(int k=0;k<lines.size();k++){
                                if(lines.get(k).isEqual(getEdge(i))){
                                    flag=false;
                                    break;
                                }
                            }
                            if(flag)
                                neighbours.add(squares.get(i));
                        }
                    }

                }

                for(int i=0;i<neighbours.size();i++){
                    if(neighbours.get(i).getFillFactor()==2){
                        return new ArrayList<Line>();
                    }
                }

                for(int i=0;i<4;i++){
                    boolean flag=true;
                    for(int j=0;j<lines.size();j++){
                        if(getEdge(i).isEqual(lines.get(j))){
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        goodEdges.add(getEdge(i));
                    }
                }

            }
            return goodEdges;
        }
        public ArrayList<Line> getOKEdges(){
            ArrayList<Line> goodEdges=new ArrayList<Line>();
            if(getFillFactor()!=2) {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < squares.size(); j++) {
                        if (squares.get(j).isEdge(getEdge(i))) {
                            if (squares.get(j) != this) {
                                if (squares.get(j).getFillFactor() == 0 || squares.get(j).getFillFactor() == 1 ||squares.get(j).getFillFactor() == 2) {
                                    boolean flag = true;
                                    for (int k = 0; k < lines.size(); k++) {
                                        if (getEdge(i).isEqual(lines.get(k))) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        goodEdges.add(getEdge(i));

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                ArrayList<Square> neighbours=new ArrayList<Square>();
                for(int i=0;i<4;i++){
                    for(int j=0;j<squares.size();j++){
                        if(squares.get(j).isEdge(getEdge(i))){
                            boolean flag=true;
                            for(int k=0;k<lines.size();k++){
                                if(lines.get(k).isEqual(getEdge(i))){
                                    flag=false;
                                    break;
                                }
                            }
                            if(flag)
                                neighbours.add(squares.get(i));
                        }
                    }

                }

                for(int i=0;i<neighbours.size();i++){
                    if(neighbours.get(i).getFillFactor()==2){
                        return new ArrayList<Line>();
                    }
                }

                for(int i=0;i<4;i++){
                    boolean flag=true;
                    for(int j=0;j<lines.size();j++){
                        if(getEdge(i).isEqual(lines.get(j))){
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        goodEdges.add(getEdge(i));
                    }
                }

            }
            return goodEdges;
        }

        public void getCluster(int clusterIndex){

            ArrayList<Line> freeEdges=new ArrayList<Line>();
            for(int i=0;i<4;i++){
                boolean flag=true;
                for(int j=0;j<lines.size();j++){
                    if(getEdge(i).isEqual(lines.get(j))){
                        flag=false;
                        break;
                    }
                }
                if(flag)freeEdges.add(getEdge(i));
            }

            for(int i=0;i<squares.size();i++){
                for(int j=0;j<freeEdges.size();j++) {
                    if (squares.get(i).isEdge(freeEdges.get(j))){
                        if(squares.get(i).getFillFactor()==2){
                            boolean flag=true;
                            for(int k=0;k<chains.get(clusterIndex).size();k++){
                                if(i==chains.get(clusterIndex).get(k)){
                                    flag=false;
                                    break;
                                }
                            }
                            if(flag) {

                                chains.get(clusterIndex).add(i);

                                squares.get(i).getCluster(clusterIndex);


                            }
                        }
                    }
                }
            }
        }
    }
}
