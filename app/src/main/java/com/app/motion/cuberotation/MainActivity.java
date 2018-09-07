package com.app.motion.cuberotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.app.motion.cuberotation.view.CubeRotationView;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private CubeRotationView mCubeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = this.findViewById(R.id.seekbar);
        mCubeView = this.findViewById(R.id.cube_panel);


        seekBar.setOnSeekBarChangeListener(mCubeView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCubeView.start();
    }
}
