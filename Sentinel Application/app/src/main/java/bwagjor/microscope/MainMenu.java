package bwagjor.microscope;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by benja_000 on 8/04/2015.
 */

public class MainMenu extends Activity {

    Button test, micro, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.title_menu);

        //Create TEST button

        test = (Button) findViewById(R.id.test);
        test.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    //test.setBackgroundDrawable(getResources().getDrawable(
                    //        R.drawable.playclick));
                    try {
                        Class ourClass = Class
                                .forName("bwagjor.microscope.MyActivity");
                        Intent ourIntent = new Intent(MainMenu.this, ourClass);
                        startActivity(ourIntent);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    // test.setBackgroundDrawable(getResources().getDrawable(
                    //         R.drawable.test));
                }
                return false; // the listener has NOT consumed the event, pass
                // it on

            }


        });

        

        //Create SETTINGS button

        settings = (Button) findViewById(R.id.set);
        settings.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    //test.setBackgroundDrawable(getResources().getDrawable(
                    //        R.drawable.playclick));
                    try {
                        Class ourClass = Class
                                .forName("bwagjor.microscope.Settings");
                        Intent ourIntent = new Intent(MainMenu.this, ourClass);
                        startActivity(ourIntent);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    // test.setBackgroundDrawable(getResources().getDrawable(
                    //         R.drawable.test));
                }
                return false; // the listener has NOT consumed the event, pass
                // it on

            }


        });
    }


}