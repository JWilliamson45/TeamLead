package com.jameswilliamson.teamlead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.view.ViewGroup.LayoutParams;
import android.graphics.Color;
import android.widget.RelativeLayout;

public class ContextSwitchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_switch);

        Button newButton = new Button(this);
        newButton.setText("Button 0");
        newButton.setBackgroundColor(Color.CYAN);
        Button newButton2 = new Button(this);
        newButton2.setText("Button 1");
        newButton2.setBackgroundColor(Color.RED);
        Button newButton3 = new Button(this);
        newButton3.setText("Button 2");
        newButton3.setBackgroundColor(Color.GREEN);

//        RelativeLayout tile0 = (RelativeLayout)findViewById(R.id.grid_index_0);
//        RelativeLayout tile1 = (RelativeLayout)findViewById(R.id.grid_index_1);
//        RelativeLayout tile4 = (RelativeLayout)findViewById(R.id.grid_index_4);

//        tile0.addView(newButton);
//        tile1.addView(newButton2);
//        tile4.addView(newButton3);
//
//        newButton.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        newButton2.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        newButton3.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        ///tile1.addView(newButton2);
        ///tile4.addView(newButton3);

        // note: in layout, probably want each cell to have android:layout_height="wrap_content"
    }
}
