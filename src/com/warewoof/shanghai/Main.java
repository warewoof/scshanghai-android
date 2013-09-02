package com.warewoof.shanghai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.warewoof.shanghai.activity.DisplaySplashScreen;

public class Main extends Activity {
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i;        
        i = new Intent(this, DisplaySplashScreen.class);
        startActivity(i);        
		finish();           
    }
}
