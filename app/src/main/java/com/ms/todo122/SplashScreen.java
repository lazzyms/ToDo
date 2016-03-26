package com.ms.todo122;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by SMS on 2/26/2016.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        thread.start();

    }

}
