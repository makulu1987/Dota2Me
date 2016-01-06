package com.makulu.dota2me;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.makulu.dota2api.Dota2Service;
import com.makulu.dota2api.Dota2;
import com.makulu.dota2api.HeroSize;
import com.makulu.dota2api.RetrofitUtils;
import com.makulu.dota2api.UrlGenerator;
import com.makulu.dota2api.model.hero.Hero;
import com.makulu.dota2api.model.item.Item;
import com.makulu.dota2api.model.match.MatchDetail;
import com.makulu.dota2api.model.match.PlayerDetail;
import com.trello.rxlifecycle.components.RxActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by xingzheng on 2015/12/31.
 */
public class MatchDetailActivity extends RxActivity {
    TextView match_detail_winner;
    RecyclerView listview;
    MatchDetailAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macth_detail);
        match_detail_winner = (TextView) findViewById(R.id.match_detail_winner);
        listview = (RecyclerView) findViewById(R.id.match_detail_list);
        adapter=new MatchDetailAdapter();
        listview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        listview.setAdapter(adapter);
        String matchId = getIntent().getStringExtra("matchId");
        RetrofitUtils.createApi(Dota2Service.class)
                .getMatchDetails(getString(R.string.steam_id), matchId)
                .compose(RetrofitUtils.<MatchDetail>applyCommon2Main())
                .compose(this.<MatchDetail>bindToLifecycle())
                .subscribe(new Subscriber<MatchDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onError",e.getMessage());
                    }

                    @Override
                    public void onNext(MatchDetail matchDetail) {
                        match_detail_winner.setText(matchDetail.result.radiant_win);
                        adapter.updaye(matchDetail.result.players);
                    }
                });

    }

    class MatchDetailAdapter extends RecyclerView.Adapter<MatchDetailHolder> {
        public List<PlayerDetail> playerDetails = new ArrayList<>();

        public void updaye(List<PlayerDetail> playerDetails) {
            this.playerDetails.clear();
            notifyDataSetChanged();
            this.playerDetails.addAll(playerDetails);
            notifyDataSetChanged();
        }

        @Override
        public MatchDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new MatchDetailHolder(LayoutInflater.from(MatchDetailActivity.this).inflate(R.layout.view_match_detail_item_radiant, parent, false));
                default:
                    return new MatchDetailHolder(LayoutInflater.from(MatchDetailActivity.this).inflate(R.layout.view_match_detail_item_dire, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(MatchDetailHolder holder, int position) {
            holder.refreshContent(playerDetails.get(position));
        }

        @Override
        public int getItemCount() {
            return playerDetails.size();
        }

        @Override
        public int getItemViewType(int position) {
            return isRadiant(playerDetails.get(position).player_slot) ? 0 : 1;
        }
    }

    class MatchDetailHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public SimpleDraweeView hero;
        public SimpleDraweeView[] items;
        public TextView kills;
        public TextView deaths;
        public TextView assists;

        public MatchDetailHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.player_id);
            kills = (TextView) itemView.findViewById(R.id.player_kill);
            deaths = (TextView) itemView.findViewById(R.id.player_death);
            assists = (TextView) itemView.findViewById(R.id.player_assists);
            hero = (SimpleDraweeView) itemView.findViewById(R.id.player_hero_img);
            items = new SimpleDraweeView[]{
                    (SimpleDraweeView) itemView.findViewById(R.id.item_0),
                    (SimpleDraweeView) itemView.findViewById(R.id.item_1),
                    (SimpleDraweeView) itemView.findViewById(R.id.item_2),
                    (SimpleDraweeView) itemView.findViewById(R.id.item_3),
                    (SimpleDraweeView) itemView.findViewById(R.id.item_4),
                    (SimpleDraweeView) itemView.findViewById(R.id.item_5)
            };
        }

        public void refreshContent(PlayerDetail playerDetail) {
            id.setText(playerDetail.account_id + "");
            kills.setText("杀：" + playerDetail.kills);
            deaths.setText("死：" + playerDetail.deaths);
            assists.setText("协助：" + playerDetail.assists);
            loadHero(playerDetail.hero_id, hero, HeroSize.FQHP);
            loadItem(playerDetail.item_0, items[0]);
            loadItem(playerDetail.item_1, items[1]);
            loadItem(playerDetail.item_2, items[2]);
            loadItem(playerDetail.item_3, items[3]);
            loadItem(playerDetail.item_4, items[4]);
            loadItem(playerDetail.item_5, items[5]);
        }
    }

    private void loadHero(String heroId, SimpleDraweeView simpleDraweeView, HeroSize heroSize) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true)
                .setOldController(simpleDraweeView.getController());
        if (!TextUtils.isEmpty(heroId) && null != Dota2.getHero(heroId)) {
            Hero hero = Dota2.getHero(heroId);
            builder.setUri(UrlGenerator.generateHeroImage(hero.getName(), heroSize));
        } else {
            builder.setUri(Uri.parse("http://bizhi.33lc.com/uploadfile/2013/0909/20130909093801210.jpg"));
        }
        simpleDraweeView.setController(builder.build());
        simpleDraweeView.setAspectRatio(heroSize.getWidth() / heroSize.getHeight());
    }

    private void loadItem(String itemId, SimpleDraweeView simpleDraweeView) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true)
                .setOldController(simpleDraweeView.getController());
        if (!TextUtils.isEmpty(itemId) && null != Dota2.getItem(itemId)) {
            Item item = Dota2.getItem(itemId);
            builder.setUri(UrlGenerator.generateItemImage(item.getId()));
        } else {
            builder.setUri(Uri.parse("http://bizhi.33lc.com/uploadfile/2013/0909/20130909093801210.jpg"));
        }
        simpleDraweeView.setController(builder.build());
        simpleDraweeView.setAspectRatio(1);
    }

    private boolean isRadiant(String slot) {
        Integer value = Integer.parseInt(slot);
        return (value & 128) > 0;
    }

    private int position(String slot) {
        Integer by = Integer.parseInt(slot);
        return by & 7;
    }
}
