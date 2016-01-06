package com.makulu.dota2me;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.makulu.dota2api.Dota2;
import com.makulu.dota2api.model.event.BaseEvent;
import com.makulu.dota2api.model.event.HeroEvent;
import com.makulu.dota2api.model.event.InitFinishEvent;
import com.makulu.dota2api.model.event.ItemEvent;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SplashActivity extends Activity implements View.OnClickListener {
    Button btn;
    SimpleDraweeView splash_bg;
    DraweeController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash_bg = (SimpleDraweeView) findViewById(R.id.splash_bg);
        controller = Fresco.newDraweeControllerBuilder()
                .setUri("res://com.makulu.dota2me/" + R.drawable.dota2_splash_loading)
                .setAutoPlayAnimations(false)
                .build();
        splash_bg.setController(controller);
        btn = (Button) findViewById(R.id.login);
        btn.setOnClickListener(this);
        EventBus.getDefault().register(this);
        Dota2.init(this);
    }

    public void onEventMainThread(HeroEvent heroEvent) {
        Log.i("HeroEvent", heroEvent.getMessage());
    }

    public void onEventMainThread(ItemEvent itemEvent) {
        Log.i("ItemEvent", itemEvent.getMessage());
    }

    public void onEvent(BaseEvent baseEvent) {
        Log.i("BaseEvent", baseEvent.getMessage());
    }

    public void onEventMainThread(InitFinishEvent initFinishEvent) {
        btn.setVisibility(View.VISIBLE);
        Log.i("InitFinishEvent", initFinishEvent.getMessage());
        if(!startAnimate()) {
            Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                startAnimate();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimate();
    }

    private boolean startAnimate() {
        if(controller.getAnimatable()!=null) {
            Animatable animatable=controller.getAnimatable();
            if (!animatable.isRunning()) {
                animatable.start();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimate();

    }

    private void stopAnimate() {
        if(controller.getAnimatable()!=null) {
            Animatable animatable=controller.getAnimatable();
            if(animatable.isRunning()) {
                animatable.stop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        stopAnimate();

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
