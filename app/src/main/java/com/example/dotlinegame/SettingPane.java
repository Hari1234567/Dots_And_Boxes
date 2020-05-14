package com.example.dotlinegame;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingPane#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingPane extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static int dimension;
    public static int numPlayers;
    View view;
    Button playBut;

    public SettingPane() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingPane newInstance(String param1, String param2) {
        SettingPane fragment = new SettingPane();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_settings, container, false);
        playBut=(Button)view.findViewById(R.id.playButton);
        SeekBar dimensionBar=(SeekBar)view.findViewById(R.id.dimensionSlider);
        SeekBar playerCountSlider=(SeekBar)view.findViewById(R.id.playerCountSlider);
            final TextView dimensionReader=(TextView)view.findViewById(R.id.seekbarlabel2);
            final TextView playerCountReader=(TextView)view.findViewById(R.id.seekbarlabel1);
            dimensionReader.setText(Integer.toString(5+dimensionBar.getProgress())+"X"+Integer.toString(5+dimensionBar.getProgress()));

            ImageView seek1Imagwe=(ImageView) view.findViewById(R.id.imageviewseek1);
            if(MainMenu.AIMode) {
                playerCountReader.setText(Integer.toString(1 + playerCountSlider.getProgress()));
                seek1Imagwe.setImageDrawable(getResources().getDrawable(R.drawable.difficultylogo));

            }
            else
                playerCountReader.setText(Integer.toString(2+playerCountSlider.getProgress()));

            dimensionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    dimensionReader.setText(Integer.toString(5+seekBar.getProgress())+"X"+Integer.toString(5+seekBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            playerCountSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(MainMenu.AIMode)
                    playerCountReader.setText(Integer.toString(1+seekBar.getProgress()));
                    else
                        playerCountReader.setText(Integer.toString(2+seekBar.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        playBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar dimensionSlider=(SeekBar)view.findViewById(R.id.dimensionSlider);
                SeekBar playerCountBar=(SeekBar)view.findViewById(R.id.playerCountSlider);
                dimension=5+dimensionSlider.getProgress();
                numPlayers=2+playerCountBar.getProgress();

                MainMenu mainMenu=(MainMenu)getActivity();
                mainMenu.launchGame();

            }
        });
        if(MainMenu.AIMode) {
            TextView playerLabel = (TextView) view.findViewById(R.id.playerCountLabel);
            playerLabel.setText(R.string.difficulty);
            SeekBar playerCountSlder = (SeekBar) view.findViewById(R.id.playerCountSlider);
            playerCountSlder.setMax(2);
        }

        return view;
    }


}
