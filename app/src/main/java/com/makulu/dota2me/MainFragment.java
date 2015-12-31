package com.makulu.dota2me;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.makulu.dota2api.DotaCache;
import com.makulu.dota2api.HeroSize;
import com.makulu.dota2api.LobbyType;
import com.makulu.dota2api.RetrofitUtils;
import com.makulu.dota2api.UrlGenerator;
import com.makulu.dota2api.model.hero.Hero;
import com.makulu.dota2api.model.match.Match;
import com.makulu.dota2api.model.match.MatchResult;
import com.makulu.dota2api.model.match.Player;
import com.trello.rxlifecycle.components.RxFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by xujintian on 2015/8/17.
 */
public class MainFragment extends RxFragment {
    TextView tv;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter = new HistoryAdapter();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView) view.findViewById(R.id.content);
        recyclerView = (RecyclerView) view.findViewById(R.id.history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(historyAdapter);
        testApi();
    }

    void testApi() {
        RetrofitUtils.createApi(Dota2Service.class)
                .getAllMatchHistory(getString(R.string.steam_id))
                .compose(RetrofitUtils.<MatchResult>applyCommon2Main())
                .compose(this.<MatchResult>bindToLifecycle())
                .subscribe(matchHistory -> {
                    tv.setText(matchHistory.result.toString());
                    historyAdapter.refresh(matchHistory.result.matches);
                });
    }

    class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {
        private List<Match> historyList = new ArrayList<>();

        public void refresh(List<Match> historyList) {
            this.historyList.clear();
            notifyDataSetChanged();
            this.historyList.addAll(historyList);
            notifyDataSetChanged();
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    return new SoloHistoryHolder(LayoutInflater.from(getActivity()).inflate(R.layout.history_item_solo, viewGroup, false));
                default:
                    return new TotalHistoryHolder(LayoutInflater.from(getActivity()).inflate(R.layout.history_item, viewGroup, false));
            }

        }

        @Override
        public void onBindViewHolder(HistoryHolder historyHolder, int i) {
            historyHolder.refreshContent(historyList.get(i));
        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return LobbyType.isSolo(historyList.get(position).getLobby_type()) ? 0 : 1;
        }
    }


    class HistoryHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView seq_number;
        TextView time;
        TextView lobbyType;

        public HistoryHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.history_id);
            seq_number = (TextView) itemView.findViewById(R.id.history_seq_number);
            time = (TextView) itemView.findViewById(R.id.history_time);
            lobbyType = (TextView) itemView.findViewById(R.id.history_lobbyType);
        }

        void refreshContent(Match matchHistory) {
            id.setText(matchHistory.getMatch_id());
            id.setOnClickListener(v -> {
                Intent detail=new Intent(getActivity(),MatchDetailActivity.class);
                detail.putExtra("matchId",matchHistory.getMatch_id());
                startActivity(detail);
            });
            time.setText(sdf.format(new Date(Long.parseLong(matchHistory.getStart_time()) * 1000)));
            lobbyType.setText(LobbyType.getLocalName(Integer.parseInt(matchHistory.getLobby_type())));
        }
    }

    class TotalHistoryHolder extends HistoryHolder {
        SimpleDraweeView[] radiants = new SimpleDraweeView[5];
        SimpleDraweeView[] dirs = new SimpleDraweeView[5];

        public TotalHistoryHolder(View itemView) {
            super(itemView);
            radiants[0] = (SimpleDraweeView) itemView.findViewById(R.id.radiant_01);
            radiants[1] = (SimpleDraweeView) itemView.findViewById(R.id.radiant_02);
            radiants[2] = (SimpleDraweeView) itemView.findViewById(R.id.radiant_03);
            radiants[3] = (SimpleDraweeView) itemView.findViewById(R.id.radiant_04);
            radiants[4] = (SimpleDraweeView) itemView.findViewById(R.id.radiant_05);
            dirs[0] = (SimpleDraweeView) itemView.findViewById(R.id.dire_01);
            dirs[1] = (SimpleDraweeView) itemView.findViewById(R.id.dire_02);
            dirs[2] = (SimpleDraweeView) itemView.findViewById(R.id.dire_03);
            dirs[3] = (SimpleDraweeView) itemView.findViewById(R.id.dire_04);
            dirs[4] = (SimpleDraweeView) itemView.findViewById(R.id.dire_05);
        }

        void refreshContent(Match matchHistory) {
            super.refreshContent(matchHistory);
            List<Player> players = matchHistory.getPlayers();
            HeroSize heroSize = getHeroSize();
            for (int i = 0; i < players.size(); i++) {
                String slot = players.get(i).getPlayer_slot();
                if (isRadiant(slot)) {
                    loadContent(players.get(i).getHero_id(), radiants[position(slot)], heroSize);
                } else {
                    loadContent(players.get(i).getHero_id(), dirs[position(slot)], heroSize);
                }
            }
        }
    }

    class SoloHistoryHolder extends HistoryHolder {
        SimpleDraweeView radiant_01;
        SimpleDraweeView dire_01;

        public SoloHistoryHolder(View itemView) {
            super(itemView);
            radiant_01 = (SimpleDraweeView) itemView.findViewById(R.id.radiant_01);
            dire_01 = (SimpleDraweeView) itemView.findViewById(R.id.dire_01);
        }

        void refreshContent(Match matchHistory) {
            super.refreshContent(matchHistory);
            List<Player> players = matchHistory.getPlayers();
            HeroSize heroSize = getHeroSize();
            if (isRadiant(players.get(0).getPlayer_slot())) {
                loadContent(players.get(0).getHero_id(), radiant_01, heroSize);
                loadContent(players.get(1).getHero_id(), dire_01, heroSize);
            } else {
                loadContent(players.get(0).getHero_id(), dire_01, heroSize);
                loadContent(players.get(1).getHero_id(), radiant_01, heroSize);
            }
        }
    }


    private void loadContent(String heroId, SimpleDraweeView simpleDraweeView, HeroSize heroSize) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true)
                .setOldController(simpleDraweeView.getController());
        if (!TextUtils.isEmpty(heroId) && null != DotaCache.getHero(heroId)) {
            Hero hero = DotaCache.getHero(heroId);
            builder.setUri(UrlGenerator.generateHeroImage(hero.getName(), heroSize));
        } else {
            builder.setUri(Uri.parse("http://bizhi.33lc.com/uploadfile/2013/0909/20130909093801210.jpg"));
        }
        simpleDraweeView.setController(builder.build());
        simpleDraweeView.setAspectRatio(heroSize.getWidth() / heroSize.getHeight());
    }

    HeroSize getHeroSize() {
        return HeroSize.FQHP;
    }


    private boolean isRadiant(String slot) {
        Integer value=Integer.parseInt(slot);
        return (value&128)>0;
    }

    private int position(String slot) {
        Integer by = Integer.parseInt(slot);
        return by&7;
    }
}
