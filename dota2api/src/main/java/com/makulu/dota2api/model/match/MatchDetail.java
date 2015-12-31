package com.makulu.dota2api.model.match;

import java.util.List;

/**
 * Created by xingzheng on 2015/12/31.
 */
public class MatchDetail {
    public MatchDetailResult result;

    public static class MatchDetailResult{
        public List<PlayerDetail> players;

        public String radiant_win;
        public String duration;
        public String start_time;
        public String match_id;
        public String match_seq_num;
        public String tower_status_radiant;
        public String tower_status_dire;
        public String barracks_status_radiant;
        public String barracks_status_dire;
        public String cluster;
        public String first_blood_time;
        public String lobby_type;
        public String human_players;
        public String leagueid;
        public String positive_votes;
        public String negative_votes;
        public String game_mode;
        public String engine;
    }
}
