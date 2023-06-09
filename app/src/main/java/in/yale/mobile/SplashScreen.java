package in.yale.mobile;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       // AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setContentView(R.layout.splash_screen);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
       // handleSplash();
        handle();
    }

private  void handle(){
    final Thread timer = new Thread() {
        public void run() {
            try {
                //Display for 3 seconds
                sleep(1000);
            } catch (InterruptedException e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {

                Intent intent;
                intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(new Intent(intent));
                finish();


            }
        }
    };

    timer.start();
}
    private void handleSplash() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent;
//                intent = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(new Intent(intent));
//                finish();
//            }
//        }, 500);
        Intent intent;
               intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(new Intent(intent));
                finish();
    }

    @Override
    public void onBackPressed() {

    }
}
