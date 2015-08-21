package com.makulu.dota2me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makulu.dota2api.Dota2Service;
import com.makulu.dota2api.RetrofitUtils;
import com.makulu.dota2api.model.match.Match;
import com.makulu.dota2api.model.match.MatchResult;
import com.makulu.dota2api.model.match.Player;
import com.trello.rxlifecycle.components.RxFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujintian on 2015/8/17.
 */
public class MainFragment extends RxFragment {
    TextView tv;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter = new HistoryAdapter();

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
    }


    class HistoryHolder extends RecyclerView.ViewHolder {
        TextView tv;
        TextView[] players = new TextView[10];

        public HistoryHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.history_content);
            players[0] = (TextView) itemView.findViewById(R.id.radiant_01);
            players[1] = (TextView) itemView.findViewById(R.id.radiant_02);
            players[2] = (TextView) itemView.findViewById(R.id.radiant_03);
            players[3] = (TextView) itemView.findViewById(R.id.radiant_04);
            players[4] = (TextView) itemView.findViewById(R.id.radiant_05);
            players[5] = (TextView) itemView.findViewById(R.id.dire_01);
            players[6] = (TextView) itemView.findViewById(R.id.dire_02);
            players[7] = (TextView) itemView.findViewById(R.id.dire_03);
            players[8] = (TextView) itemView.findViewById(R.id.dire_04);
            players[9] = (TextView) itemView.findViewById(R.id.dire_05);
        }

        void refreshContent(Match matchHistory) {
            List<Player> players = matchHistory.getPlayers();

            tv.setText(matchHistory.toString());
            for (int i = 0; i < players.size(); i++) {
                this.players[i].setText(players.get(i).getPlayer_slot() + "");
            }
        }
    }
}
