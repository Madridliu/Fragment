package com.example.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity{  
    private final int SPLASH_DISPLAY_LENGHT = 1000; //—”≥Ÿ“ª√Î    
      
    @Override   
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.splash1);   
        new Handler().postDelayed(new Runnable(){   
    
         @Override   
         public void run() {   
             Intent mainIntent = new Intent(Splash.this,MainActivity.class);   
             startActivity(mainIntent);   
             finish();   
         }   
              
        }, SPLASH_DISPLAY_LENGHT);   
    }   
  
}  