package edu.jru.deleon.crislenard.examscheduler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashScreen extends Activity {
    public void onAttachedtoWindow(){
        super.onAttachedToWindow();
        Window window = getWindow();

        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        startAnimations();

    }
    private void startAnimations(){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.alpha);
        animation.reset();
        RelativeLayout l = (RelativeLayout)findViewById(R.id.lay);
        l.clearAnimation();
        l.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,R.anim.translate);
        animation.reset();
        TextView tv = (TextView)findViewById(R.id.txtTitle);
        tv.clearAnimation();
        tv.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,R.anim.translate2);
        animation.reset();
        TextView tv2 = (TextView)findViewById(R.id.txtDesc);
        tv2.clearAnimation();
        tv2.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,R.anim.translate);
        animation.reset();
        ImageView imgLogo = (ImageView)findViewById(R.id.imgLogo);
        imgLogo.clearAnimation();
        imgLogo.startAnimation(animation);


        splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    //pause time
                    while (waited < 6000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {

                } finally {
                    SplashScreen.this.finish();
                }
            }
        };

        splashThread.start();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
