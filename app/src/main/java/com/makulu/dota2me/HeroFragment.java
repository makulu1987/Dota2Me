package com.makulu.dota2me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.makulu.dota2api.Data;
import com.makulu.dota2api.Dota2ServiceFactory;
import com.makulu.dota2api.HeroSize;
import com.makulu.dota2api.model.hero.Hero;
import com.makulu.dota2me.R;
import com.makulu.dota2api.RetrofitUtils;
import com.makulu.dota2api.UrlGenerator;
import com.trello.rxlifecycle.components.RxFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by xujintian on 2015/8/17.
 */
public class HeroFragment extends RxFragment {


    RecyclerView recyclerView;
    HistoryAdapter historyAdapter = new HistoryAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.items);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(historyAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dota2ServiceFactory.getHeros(getActivity()).compose(RetrofitUtils.applyCommon2Main())
                .compose(this.<Data<List<Hero>>>bindToLifecycle())
                .delaySubscription(300,TimeUnit.MILLISECONDS)
                .subscribe(listData -> {
                    historyAdapter.refresh(listData.getData());
                });
    }

    class HistoryAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<Hero> items = new ArrayList<>();

        public void refresh(List<Hero> historyList) {
            this.items.clear();
            notifyDataSetChanged();
            this.items.addAll(historyList);
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ItemHolder(LayoutInflater.from(getActivity()).inflate(R.layout.hero_thumbnail, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder historyHolder, int i) {
            historyHolder.refreshContent(items.get(i));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv;
        SimpleDraweeView iv;

        public ItemHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.hero_name);
            iv = (SimpleDraweeView) itemView.findViewById(R.id.hero_thumbnail);
        }

        void refreshContent(Hero item) {
            tv.setText(item.getLocalized_name() + "");

            HeroSize heroSize = getHeroSize();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(UrlGenerator.generateHeroImage(item.getName(), heroSize))
                    .setTapToRetryEnabled(true)
                    .setOldController(iv.getController())
                    .build();
            iv.setController(controller);
            iv.setAspectRatio(heroSize.getWidth() / heroSize.getHeight());
        }
    }

    HeroSize getHeroSize() {
        return HeroSize.FQHP;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Dota2ServiceFactory.clearHeroMemory();
    }
}
