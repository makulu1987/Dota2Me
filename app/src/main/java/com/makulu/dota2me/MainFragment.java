package com.makulu.dota2me;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.makulu.dota2api.Dota2;
import com.makulu.dota2api.Dota2Service;
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

/**
 * Created by xujintian on 2015/8/17.
 */
public class MainFragment extends RxFragment {
    TextView tv;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter = new HistoryAdapter();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    SimpleDraweeView[] radiants = new SimpleDraweeView[5];
    SimpleDraweeView[] dirs = new SimpleDraweeView[5];

    LinearLayoutManager layoutManager;

    float allAspectRatio = 0;

    private static final int EXPAND_STATE=0;
    private static final int COLLAPSE_STATE=1;
    private int state=EXPAND_STATE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        allAspectRatio = ((float) size.x) / (2 * size.y);


        tv = (TextView) view.findViewById(R.id.content);
        tv.setText("近期比赛");

        radiants[0] = (SimpleDraweeView) view.findViewById(R.id.radiant_01);
        radiants[1] = (SimpleDraweeView) view.findViewById(R.id.radiant_02);
        radiants[2] = (SimpleDraweeView) view.findViewById(R.id.radiant_03);
        radiants[3] = (SimpleDraweeView) view.findViewById(R.id.radiant_04);
        radiants[4] = (SimpleDraweeView) view.findViewById(R.id.radiant_05);
        dirs[0] = (SimpleDraweeView) view.findViewById(R.id.dire_01);
        dirs[1] = (SimpleDraweeView) view.findViewById(R.id.dire_02);
        dirs[2] = (SimpleDraweeView) view.findViewById(R.id.dire_03);
        dirs[3] = (SimpleDraweeView) view.findViewById(R.id.dire_04);
        dirs[4] = (SimpleDraweeView) view.findViewById(R.id.dire_05);

        recyclerView = (RecyclerView) view.findViewById(R.id.history);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastUpdated = -1;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentFirst = layoutManager.findFirstVisibleItemPosition();
                if (currentFirst != lastUpdated) {
                    lastUpdated = currentFirst;
                    updateDetail(currentFirst);

                }
            }
        });
        testApi();
    }

    private void updateDetail(int currentFirst) {
        Match match = historyAdapter.getMatch(currentFirst);
        switch (historyAdapter.getItemViewType(currentFirst)) {
            case 0:
                updateSoloDetail(match);
                break;
            case 1:
                updateAllDetail(match);
                break;
        }
    }

    private void updateSoloDetail(Match match) {
        List<Player> players = match.getPlayers();
        HeroSize heroSize = getHeroSize();
//        for (int i = 0; i < 5; i++) {
//            if (i == 2) {
//                radiants[i].setVisibility(View.VISIBLE);
//                dirs[i].setVisibility(View.VISIBLE);
//                loadContent("", radiants[i], heroSize, allAspectRatio);
//                loadContent("", dirs[i], heroSize, allAspectRatio);
//                continue;
//            }
//            radiants[i].setVisibility(View.INVISIBLE);
//            dirs[i].setVisibility(View.INVISIBLE);
//        }
        animateCollapse();
        if (players != null && players.size() > 0) {
            if (players.size() == 1) {
                String slot = players.get(0).getPlayer_slot();
                if (isRadiant(slot)) {
                    loadContent(players.get(0).getHero_id(), radiants[2], heroSize, allAspectRatio);
                    loadContent("", dirs[2], heroSize, allAspectRatio);
                } else {
                    loadContent("", radiants[2], heroSize, allAspectRatio);
                    loadContent(players.get(0).getHero_id(), dirs[2], heroSize, allAspectRatio);
                }
            } else {
                String slot1 = players.get(0).getPlayer_slot();
                if (isRadiant(slot1)) {
                    loadContent(players.get(0).getHero_id(), radiants[2], heroSize, allAspectRatio);
                    loadContent(players.get(1).getHero_id(), dirs[2], heroSize, allAspectRatio);
                } else {
                    loadContent(players.get(1).getHero_id(), radiants[2], heroSize, allAspectRatio);
                    loadContent(players.get(0).getHero_id(), dirs[2], heroSize, allAspectRatio);
                }
            }
        }

    }

    private void updateAllDetail(Match match) {
        List<Player> players = match.getPlayers();
        HeroSize heroSize = getHeroSize();
//        for (int i = 0; i < 5; i++) {
//            radiants[i].setVisibility(View.VISIBLE);
//            dirs[i].setVisibility(View.VISIBLE);
//        }
        animateExpand();
        for (int i = 0; i < players.size(); i++) {
            String slot = players.get(i).getPlayer_slot();
            if (isRadiant(slot)) {
                loadContent(players.get(i).getHero_id(), radiants[position(slot)], heroSize, allAspectRatio);
            } else {
                loadContent(players.get(i).getHero_id(), dirs[position(slot)], heroSize, allAspectRatio);
            }
        }
    }


    void testApi() {
        RetrofitUtils.createApi(Dota2Service.class)
                .getAllMatchHistory(getString(R.string.steam_id))
                .compose(RetrofitUtils.<MatchResult>applyCommon2Main())
                .compose(this.<MatchResult>bindToLifecycle())
                .subscribe(matchHistory -> {
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

        public Match getMatch(int position) {
            return historyList.get(position);
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new HistoryHolder(LayoutInflater.from(getActivity()).inflate(R.layout.history_item, viewGroup, false));
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
                Intent detail = new Intent(getActivity(), MatchDetailActivity.class);
                detail.putExtra("matchId", matchHistory.getMatch_id());
                startActivity(detail);
            });
            time.setText(sdf.format(new Date(Long.parseLong(matchHistory.getStart_time()) * 1000)));
            lobbyType.setText(LobbyType.getLocalName(Integer.parseInt(matchHistory.getLobby_type())));
        }
    }

    private void loadContent(String heroId, SimpleDraweeView simpleDraweeView, HeroSize heroSize, float aspectRatio) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true)
                .setOldController(simpleDraweeView.getController());
        if (!TextUtils.isEmpty(heroId) && null != Dota2.getHero(heroId)) {
            Hero hero = Dota2.getHero(heroId);
            builder.setImageRequest(ImageRequest.fromUri(UrlGenerator.generateHeroImage(hero.getName(), HeroSize.FQHP)));
            builder.setLowResImageRequest(ImageRequest.fromUri(UrlGenerator.generateHeroImage(hero.getName(), HeroSize.SHP)));
        } else {
            builder.setUri("");
        }
        simpleDraweeView.setController(builder.build());
        simpleDraweeView.setAspectRatio(aspectRatio);
    }

    HeroSize getHeroSize() {
        return HeroSize.FQHP;
    }


    private void animateCollapse(){
        if(state==COLLAPSE_STATE){
            return;
        }
        state=COLLAPSE_STATE;
        AlphaAnimation alphaAnimation= new AlphaAnimation(1.0f,0);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        for(int i=0;i<5;i++){
            if(i==2){
                continue;
            }
            radiants[i].startAnimation(alphaAnimation);
            dirs[i].startAnimation(alphaAnimation);
        }
    }
    private void animateExpand(){
        if(state==EXPAND_STATE){
            return;
        }
        state=EXPAND_STATE;
        AlphaAnimation alphaAnimation= new AlphaAnimation(0,1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        for(int i=0;i<5;i++){
            if(i==2){
                continue;
            }
            radiants[i].startAnimation(alphaAnimation);
            dirs[i].startAnimation(alphaAnimation);
        }
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
