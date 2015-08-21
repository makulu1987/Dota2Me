package com.makulu.dota2me;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.makulu.dota2api.model.event.BaseEvent;
import com.makulu.dota2api.model.event.HeroEvent;
import com.makulu.dota2api.model.event.InitFinishEvent;
import com.makulu.dota2api.model.event.ItemEvent;

import de.greenrobot.event.EventBus;

public class SplashActivity extends Activity implements View.OnClickListener {
    TextView heros;
    ScrollView hero_scrollView;
    TextView items;
    ScrollView item_scrollView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        heros = (TextView) findViewById(R.id.heros);
        items = (TextView) findViewById(R.id.items);
        hero_scrollView = (ScrollView) findViewById(R.id.hero_scrollView);
        item_scrollView = (ScrollView) findViewById(R.id.item_scrollView);
        btn = (Button) findViewById(R.id.login);
        btn.setOnClickListener(this);
        EventBus.getDefault().register(this);
//        Dota2ServiceFactory.init(this);
    }

    public void onEventMainThread(HeroEvent heroEvent) {
        Log.i("HeroEvent", heroEvent.getMessage());
        heros.setText(heros.getText() + "\n" + heroEvent.getMessage());
        hero_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void onEventMainThread(ItemEvent itemEvent) {
        Log.i("ItemEvent", itemEvent.getMessage());
        items.setText(items.getText() + "\n" + itemEvent.getMessage());
        item_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void onEvent(BaseEvent baseEvent) {
//        Log.i("BaseEvent",baseEvent.getMessage());
    }

    public void onEventMainThread(InitFinishEvent initFinishEvent) {
        btn.setVisibility(View.VISIBLE);
        Log.i("InitFinishEvent", initFinishEvent.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
