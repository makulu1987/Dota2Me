package com.makulu.dota2api;

/**
 * Created by xujintian on 2015/8/17.
 */
public enum LobbyType {
    Invalid(-1),
    Public_matchmaking(0),
    Practice(1),
    Tournament(2),
    Tutorial(3),
    Co_op_with_bots(4),
    Team_match(5),
    Solo_Queue(6),
    Ranked(7),
    Solo_Mid_1vs1(8);

    private int value;

    LobbyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getName(int value) {
        String name="";
        switch (value) {
            case -1:
                name="Invalid";
                break;
            case 0:
                name="Public_matchmaking";
                break;
            case 1:
                name="Practice";
                break;
            case 2:
                name="Tournament";
                break;
            case 3:
                name="Tutorial";
                break;
            case 4:
                name="Co_op_with_bots";
                break;
            case 5:
                name="Team_match";
                break;
            case 6:
                name="Solo_Queue";
                break;
            case 7:
                name="Ranked";
                break;
            case 8:
                name="Solo_Mid_1vs1";
                break;
        }
        return name;
    }
    public static String getLocalName(int value) {
        String name="";
        switch (value) {
            case -1:
                name="未知";
                break;
            case 0:
                name="公开匹配";
                break;
            case 1:
                name="练习赛";
                break;
            case 2:
                name="锦标赛";
                break;
            case 3:
                name="教程";
                break;
            case 4:
                name="人机对战";
                break;
            case 5:
                name="队伍对战";
                break;
            case 6:
                name="单排";
                break;
            case 7:
                name="天梯";
                break;
            case 8:
                name="单人中路Solo";
                break;
        }
        return name;
    }
    public static boolean isSolo(String lobbyType){
        return "8".equals(lobbyType);
    }
}
