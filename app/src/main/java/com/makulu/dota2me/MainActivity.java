package com.makulu.dota2me;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import com.trello.rxlifecycle.components.RxActivity;

public class MainActivity extends RxActivity {

    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment(), "main").commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
//        if (navigationView != null) {
//            navigationView.setNavigationItemSelectedListener(menuItem -> {
//                if (menuItem.isChecked()) {
//                    return false;
//                }
//                //切换相应 Fragment 等操作
//                menuItem.setChecked(true);
//                mDrawerLayout.closeDrawers();
//                switch (menuItem.getItemId()) {
//                    case R.id.main:
//                        getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment(), "main").commit();
//                        break;
//                    case R.id.item:
//                        getFragmentManager().beginTransaction().replace(R.id.container, new ItemFragment(), "item").commit();
//                        break;
//                    case R.id.hero:
//                        getFragmentManager().beginTransaction().replace(R.id.container, new HeroFragment(), "hero").commit();
//                        break;
//                }
//                return false;
//            });
//        }
    }

}
