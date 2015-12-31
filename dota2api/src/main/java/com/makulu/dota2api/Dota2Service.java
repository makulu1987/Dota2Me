package com.makulu.dota2api;

import com.makulu.dota2api.model.hero.HeroResult;
import com.makulu.dota2api.model.item.ItemResult;
import com.makulu.dota2api.model.match.MatchDetail;
import com.makulu.dota2api.model.match.MatchResult;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by xujintian on 2015/8/14.
 */
public interface Dota2Service {

    String Dota2Url = "https://api.steampowered.com";
    /**
     * Web developers can now retrieve the match history and match details in JSON or XML format for use in their own applications.
     * <p>
     * BEGIN BY READING THE TERMS AND CONDITIONS OF STEAM'S WEBAPI BY CLICKING HERE
     * <p>
     * <p>
     * THE API CANNOT BE USED TO GET PRIVATE MATCHES
     * <p>
     * GETTING A KEY
     * <p>
     * First off get a dev key from here, http://steamcommunity.com/dev/apikey and login with your Steam account and you will get unique key.
     * Please do not share this key as it identifies you when you make WebAPI requests.
     * <p>
     *  Originally Posted by Zoid
     * When you go to http://steamcommunity.com/dev/apikey the "domain" field is just a note. It's not actually used for anything and is just a helpful field so you can tell us what your website is. You can just put your name in for now. Once you get a key, its what uniquely identifies you when accessing our WebAPI calls.
     * THE API CALLS
     * <p>
     * The following API calls are available for use:
     * Code:
     * (GetMatchHistory)              https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/v001/
     * (GetMatchDetails)              https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/
     * (GetHeroes)                    https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/
     * (GetPlayerSummaries)           https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/
     * (EconomySchema)                https://api.steampowered.com/IEconItems_570/GetSchema/v0001/
     * (GetLeagueListing)             https://api.steampowered.com/IDOTA2Match_570/GetLeagueListing/v0001/
     * (GetLiveLeagueGames)           https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/
     * (GetMatchHistoryBySequenceNum) https://api.steampowered.com/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v0001/
     * (GetTeamInfoByTeamID)          https://api.steampowered.com/IDOTA2Match_570/GetTeamInfoByTeamID/v001/
     * • For a full list of available calls, see http://wiki.teamfortress.com/wiki/WebAPI
     * <p>
     * Common options:
     * Note that unless otherwise stated, an option's default is empty/ignored.
     * Code:
     * key=<key>       # Your personal API key (from above)
     * language=<lang> # The language to retrieve results in (default is en_us) (see http://en.wikipedia.org/wiki/ISO_639-1 for the language codes (first two characters) and http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes for the country codes (last two characters))
     * format=<form>   # The format to retrieve results in ("JSON" or "XML") (default is JSON)
     */


    /**
     * GetMatchHistory
     * Used to get a list of matches played.
     */


    String GetMatchHistory = "/IDOTA2Match_570/GetMatchHistory/v001/";

    @GET(GetMatchHistory)
    Observable<MatchResult> getAllMatchHistory(@Query("key") String key);

    /**
     Available options:
     Code:
     hero_id=<id>                   # Search for matches with a specific hero being played (hero ID, not name, see HEROES below)
     game_mode=<mode>               # Search for matches of a given mode (see below)
     skill=<skill>                  # 0 for any, 1 for normal, 2 for high, 3 for very high skill (default is 0)
     min_players=<count>            # the minimum number of players required in the match
     account_id=<id>                # Search for all matches for the given user (32-bit or 64-bit steam ID)
     league_id=<id>                 # matches for a particular league
     start_at_match_id=<id>         # Start the search at the indicated match id, descending
     matches_requested=<n>          # Maximum is 25 matches (default is 25)
     tournament_games_only=<string> # set to only show tournament games
     Examples:
     To get the latest 25 matches played by person with 32-bit ID "XXXXX":
     Code:
     https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=<key>&account_id=XXXXX
     To get the latest single match:
     Code:
     https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=<key>&matches_requested=1
     Note, in order to go to the "next page" of results you need to do one of two things:
     • Use the match_id of the last match returned by the query, and then use it as the start_at_match_id:
     Code:
     https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=<key>&start_at_match_id=<id>&<OTHER_OPTIONS>
     Result Field Format:
     • num_results - the number of results contained in this response
     • total_results - the total number of results for this particular query [(total_results / num_results) = total_num_pages]
     • results_remaining - the number of results left for this query [(results_remaining / num_results) = remaining_num_pages]
     • matches - an array of num_results matches:
     ○ match_id - the numeric match ID
     ○ match_seq_num - the match's sequence number - the order in which matches are recorded
     ○ start_time - date in UTC seconds since Jan 1, 1970 (unix time format)
     ○ lobby_type - the type of lobby
     § see: https://github.com/kronusme/dota2-ap...a/lobbies.json
     ○ players - an array of players:
     § account_id - the player's 32-bit Steam ID - will be set to "4294967295"
     if the player has set their account to private.
     § player_slot - an 8-bit unsigned int: if the left-most bit is set, the player was on dire. the two right-most bits represent the player slot (0-4).
     § hero_id - the numeric ID of the hero that the player used (see below).
     *
     */

    /**
     * GetMatchDetails
     * Used to get detailed information about a specified match.
     */
    String GetMatchDetails = "/IDOTA2Match_570/GetMatchDetails/v001/";

    @GET(GetMatchDetails)
    Observable<MatchDetail> getMatchDetails(@Query("key") String key, @Query("match_id") String match_id);
    /**
     Available options:
     Code:
     match_id=<id> # the match's ID
     Examples:
     To get the details for match with ID "XXXXX":
     Code:
     https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=<key>&match_id=XXXXX
     Result Field Format:
     • players - an array of players:
     ○ account_id - the player's 32-bit Steam ID - will be set to "4294967295"
     if the player has set their account to private.
     ○ player_slot - an 8-bit unsigned int: if the left-most bit is set, the player was on dire. the two right-most bits represent the player slot (0-4)
     ○ hero_id - the numeric ID of the hero that the player used.
     ○ item_0 - the numeric ID of the item that player finished with in their top-left slot.
     ○ item_1 - the numeric ID of the item that player finished with in their top-center slot.
     ○ item_2 - the numeric ID of the item that player finished with in their top-right slot.
     ○ item_3 - the numeric ID of the item that player finished with in their bottom-left slot.
     ○ item_4 - the numeric ID of the item that player finished with in their bottom-center slot.
     ○ item_5 - the numeric ID of the item that player finished with in their bottom-right slot.
     ○ kills - the number of kills the player got.
     ○ deaths - the number of times the player died.
     ○ assists - the number of assists the player got.
     ○ leaver_status
     Spoiler:  
     ○ gold - the amount of gold the player had left at the end of the match
     ○ last_hits - the number of times a player last-hit a creep
     ○ denies - the number of times a player denied a creep
     ○ gold_per_min - the player's total gold/min
     ○ xp_per_min - the player's total xp/min
     ○ gold_spent - the total amount of gold the player spent over the entire match
     ○ hero_damage - the amount of damage the player dealt to heroes
     ○ tower_damage - the amount of damage the player dealt to towers
     ○ hero_healing - the amount of damage on other players that the player healed
     ○ level - the player's final level
     ○ ability_upgrades - an array detailing the order in which a player's ability points were spent.
     § ability - the numeric ID of the ability that the point was spent on.
     § time - the time this ability point was spent - in number of seconds since the start of the match.
     § level - the level of the hero when the ability was leveled.
     ○ additional_units - details about additional units controlled by the player (i.e. lone druid's spirit bear)
     § unitname - the name of the unit
     § item_0 - the numeric ID of the item that player finished with in their top-left slot.
     § item_1 - the numeric ID of the item that player finished with in their top-center slot.
     § item_2 - the numeric ID of the item that player finished with in their top-right slot.
     § item_3 - the numeric ID of the item that player finished with in their bottom-left slot.
     § item_4 - the numeric ID of the item that player finished with in their bottom-center slot.
     § item_5 - the numeric ID of the item that player finished with in their bottom-right slot.
     ○ season - ????????
     ○ radiant_win - true if radiant won, false otherwise
     ○ duration - the total time in seconds the match ran for
     ○ start_time - date in UTC seconds since Jan 1, 1970 (unix time format)
     ○ match_id - the numeric match ID
     ○ match_seq_num - the match's sequence number - the order in which matches are recorded
     ○ tower_status_radiant - an 11-bit unsinged int: see THIS LINK
     ○ tower_status_dire - an 11-bit unsinged int: see THIS LINK
     ○ barracks_status_radiant - a 6-bit unsinged int: see THIS LINK
     ○ barracks_status_radiant - a 6-bit unsinged int: see THIS LINK
     ○ cluster - see REPLAYS below
     ○ first_blood_time - the time in seconds at which first blood occurred
     ○ replay_salt - see REPLAYS below CURRENTLY REMOVED FROM THE API
     ○ lobby_type - the type of lobby (see GetMatchHistory above)
     ○ human_players - the number of human players in the match
     ○ leagueid - the leauge this match is from (see GetMatchHistory above)
     ○ positive_votes - the number of thumbs up the game has received
     ○ positive_votes - the number of thumbs up the game has received
     ○ game_mode - a number representing the game mode of this match
     § see https://github.com/kronusme/dota2-ap...data/mods.json
     ○ The following fields are only included if there were teams applied to radiant and dire (i.e. this is a league match in a private lobby)
     § radiant_name - the name of the radiant team
     § radiant_logo - the radiant team's logo (for details on fetching this image, see FAQ below)
     § radiant_team_complete - true if all players on radiant belong to this team, false otherwise (i.e. are the stand-ins {false} or not {true})
     § dire_name - the name of the dire team
     § dire_logo - the dire team's logo (for details on fetching this image, see FAQ below)
     § dire_team_complete - true if all players on dire belong to this team, false otherwise (i.e. are the stand-ins {false} or not {true})
     *
     */
    /**
     * GetMatchHistoryBySequenceNum
     * Used to get the matches in the order which they were recorded (i.e. sorted ascending by match_seq_num).
     * This means that the first match on the first page of results returned by the call will be the very first public mm-match recorded in the stats.
     */
    String GetMatchHistoryBySequenceNum = "/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v0001/";

    @GET(GetMatchHistoryBySequenceNum)
    Observable<MatchResult> getMatchHistoryBySequenceNum(@Query("key") String key, @Query("match_id") String match_id);

    /**
     *
     * Available Options
     * Code:
     * start_at_match_seq_num=<id>    # Start the search at the indicated match id, descending
     * matches_requested=<n>          # Maximum is 25 matches (default is 25)
     * Result Field Format:
     * See GetMatchHistory.
     *
     *
     * GetHeroes
     * Used to get an UP-TO-DATE list of heroes.
     *
     * Available Options
     * Common options only (see above)
     *
     * Examples:
     * To get the list of heroes with english names:
     * Code:
     * https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=<key>&language=en_us
     * Result Field Format:
     * • heroes - an array of the heroes:
     * ○ name - the hero's in-game "code name"
     * ○ id - the hero's numeric ID
     * ○ localized_name - the hero's text name (language specific result - this field is not present if no language is specified)
     * • count - the total number of heroes in the list
     *
     * GetLeagueListing
     * Used to get a list of the tournament leagues that are available for viewing in the client (i.e. you can buy a ticket to them).
     * Intended for use in conjunction with GetLiveLeagueGames.
     *
     * Available Options
     * Common options only (see above)
     *
     * Result Field Format:
     * • leagues - an array of the leagues:
     * ○ name - the league's full name (language specific)
     * ○ leagueid - the league's numeric ID
     * ○ description - a description of the leauge (language specific)
     * ○ tournament_url - the url of the tournament's home page
     *
     * GetLiveLeagueGames
     * Used to get a list of the tournament games that are currently in progress.
     *
     * Available Options
     * Common options only (see above) - Note that if no language is specified, the API will return the in-game "string" placeholders for all fields marked with (language specific).
     *
     * Result Field Format:
     * • games - an array of the games:
     * ○ players - a list of players in the game
     * § account_id - the 32-bit account ID
     * § name - the player's display name
     * § hero_id - the hero ID of the hero that this player is playing as
     * § team - what team the player is currently playing on:
     * □ 0 = RADIANT
     * □ 1 = DIRE
     * □ 2 = BROADCASTER
     * □ 4 = UNASSIGNED
     * ○ radiant_team - information about the radiant's tournament team
     * § team_name - the team's name
     * § team_id - the team's ID
     * § team_logo - the team's logo (for details on fetching this image, see FAQ below)
     * § complete - true if all players belong to this team, false otherwise (i.e. are the stand-ins {false} or not {true})
     * ○ dire_team - information about the dire's tournament team
     * § same as radiant_team above
     * ○ lobby_id - the ID for the match's lobby
     * ○ spectators - the number of spectators currently watching
     * ○ tower_state - a 22-bit uint detailing if each tower is alive (see this link for exact details)
     * ○ league_id - the ID of the league this match is for (see GetLeagueListing above).
     */


    /**
     * GetTeamInfoByTeamID
     * Used to get data about teams that have been created in the client.
     * Note that this call by default will return a list of every team (with 100 per page) sorted ascending by team_id
     */
    String GetTeamInfoByTeamID = "/IDOTA2Match_570/GetTeamInfoByTeamID/v001/";

    @GET(GetTeamInfoByTeamID)
    Observable<MatchResult> getTeamInfoByTeamID(@Query("key") String key, @Query("start_at_team_id") String start_at_team_id);

    /**
     * Available Options
     * Code:
     * start_at_team_id  # the ID of the team to start at
     * teams_requested   # the number of teams to return (default is 100)
     * Examples:
     * To get the details of a specific team
     * Code:
     * https://api.steampowered.com/IDOTA2Match_570/GetTeamInfoByTeamID/v001/?key=<key>&start_at_team_id=<team's id>&teams_requested=1
     * Result Field Format:
     * • teams - an array of the teams
     * ○ team_id - the numeric ID of the team
     * ○ name - the name of the team
     * ○ tag - the team's abbreviation tag
     * ○ time_created - the Unix time at which the team was created
     * ○ rating - ????????????????? something to do with MM ranking?
     * ○ logo - the team's logo (for details on fetching this image, see FAQ below)
     * ○ logo_sponsor - the image showing the team's sponsor(s) (for details on fetching this image, see FAQ below)
     * ○ country_code - the country the team is from (see http://en.wikipedia.org/wiki/ISO_3166-1#Current_codes) (empty string if not specified)
     * ○ url - the team's homepage (empty string if not specified)
     * ○ games_played_with_current_roster - the number of team matchmaking games the team has played
     * ○ player_X_account_id - (where X >= 0) there is one field per player in the roster
     * ○ admin_account_id - the account id of the player who is the administrator of the team in the dota client
     */
    /**
     * GetPlayerSummaries
     * Used to get details about a player's Steam account.
     * <p>
     * For full details, see http://wiki.teamfortress.com/wiki/We...layerSummaries
     */
    String GetPlayerSummaries = "/IDOTA2Match_570/GetPlayerSummaries/v001/";

    @GET(GetPlayerSummaries)
    Observable<MatchResult> getPlayerSummaries(@Query("key") String key, @Query("start_at_team_id") String start_at_team_id);

    /**
     * EconomySchema
     * Used to get list of economy (cosmetic) items.
     * <p>
     * For full details, see http://wiki.teamfortress.com/wiki/WebAPI/GetSchema 
     */
    String EconomySchema = "/IDOTA2Match_570/EconomySchema/v001/";

    @GET(EconomySchema)
    Observable<MatchResult> getEconomySchema(@Query("key") String key, @Query("start_at_team_id") String start_at_team_id);

    /**
     * ITEMS
     */
    String GetGameItems = "/IEconDOTA2_570/GetGameItems/v001/";

    @GET(GetGameItems)
    Observable<ItemResult> getGameItems(@Query("key") String key, @Query("language") String lang);
    /** The best way to get the list of ID => Item mappings is by using the following api call:
     * Code:
     * https://api.steampowered.com/IEconDOTA2_570/GetGameItems/V001/?key=<key>&language=<lang (i.e. en)>
     * Result Field Format:
     * • items- an array of items
     * ○ id - the ID used to identify the item in the api
     * ○ name - the code name of the item (i.e. item_blades_of_attack)
     * ○ cost - the gold cost of the item
     * ○ secret_shop - 1 if is available from the secret shop, 0 otherwise
     * ○ side_shop - 1 if is available from the side shop, 0 otherwise
     * ○ recipe - 1 if it is a recipe, 0 otherwise
     * ○ localized_name - if a language is specified, this will show the in-game name of the item for that langauge.
     *
     * IDs
     * Item IDs can be found in/parsed from the following game file:
     * Code:
     * <path to steam>/Steam/steamapps/common/dota 2 beta/game/dota/scripts/npc/items.txt
     * Images
     * There are two choices for this, either get the full quality images direct from the game files:
     * See CyborgMatt's guide here to learn how to open a vpk file.
     * the images can be found in the file:
     * Code:
     * <path to steam>/Steam/steamapps/common/dota 2 beta/dota/pak01_dir.vpk
     * Then inside this file, they can be found in
     * Code:
     * root\resource\flash3\images\
     * OR get high-quality images from the steam servers:
     * Code:
     * http://cdn.dota2.com/apps/dota2/images/items/<name>_lg.png
     * where <name> is the item's "name" from the text file above, without the "item_" at the start.
     */
    /**
     * HEROES
     */

    String GetHeroes = "/IEconDOTA2_570/GetHeroes/v0001/";

    @GET(GetHeroes)
    Observable<HeroResult> getHeros(@Query("key") String key, @Query("language") String lang);
    /**
     *  IDs
     * Use the above GetHeroes API call to retrieve a list of hero IDs
     *
     * Images
     * You use the method as above in ITEMS
     * OR you can get them from valve's servers:
     * There are 4 different types of images, all follow the form:
     * Code:
     * http://cdn.dota2.com/apps/dota2/images/heroes/<name>_<suffix>
     * where <name> is the hero's "name" from GetHeros, without the "npc_hero_dota_" at the start and <suffix> is one of the following:
     * 1) tiny horizontal portrait - 35x20px (this one is no longer available)
     * Code:
     * <suffix> = eb.png
     * 2) small horizontal portrait - 59x33px
     * Code:
     * <suffix> = sb.png
     * 3) large horizontal portrait - 205x11px
     * Code:
     * <suffix> = lg.png
     * 4) full quality horizontal portrait - 256x114px
     * Code:
     * <suffix> = full.png
     * 5) full quality vertical portrait - 234x272px (note this is a .jpg)
     * Code:
     * <suffix> = vert.jpg
     */
    /**
     * REPLAYS
     */
    //TODO replays
    /** Where can I get the URL for a match's replay?
     * Replays are formed from the following URL:
     * Code:
     * http://replay<cluster>.valve.net/570/<match_id>_<replay_salt>.dem.bz2
     * Where <cluster>, <match_id> and <replay_salt> are available from the fields in GetMatchDetails.
     *
     * See https://developer.valvesoftware.com/..._2_Demo_Format for details on how to parse downloaded replay files
     */
    /**
     * STEAMIDS
     */
    //TODO steamId
    /** The Dota2 API generally gives you people's SteamIDs as 32-bit numbers.
     *
     * In order to convert from these 32-bit numbers to Steam Names, you must first convert between the 32-bit ID and 64-bit ID:
     * • On a system that supports up to 64-bit numbers you can do the following:
     * ○ STEAMID64 - 76561197960265728 = STEAMID32
     * ○ STEAMID32 + 76561197960265728 = STEAMID64
     * ○ OR
     * ○ STEAMID32 = The right-most 32-bits of STEAMID64
     * ○ STEAMID64 = concatenate("00000001000100000000000000000001", STEAMID32);
     * • On a system that only supports up to 32-bit numbers - it's trickier. You have to rely on the language's built-in "big number" functions (i.e. PHP's gmp extension: see this post for details)
     * Once you have the 64-bit ID, then you can use the GetPlayerSummaries call to get their detail!
     *
     *
     * How to get someone's 64-bit ID to search with:
     *
     * If you have their vanity URL, it should look like this:
     * Code:
     * http://steamcommunity.com/id/<vanity_name>/
     * Using ResolveVanityURL (see http://wiki.teamfortress.com/wiki/We...solveVanityURL for more info), you can get the 64-bit ID as follows:
     * [CODE]http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=<key>&vanityurl=<vanity_name>[CODE]
     *
     * If you have an ID url:
     * Code:
     * http://steamcommunity.com/profiles/<id>/
     * Then the <id> is their 64-bit ID and you're done!
     *
     * If you have just a Steam-Name:
     * You can use this to search the Dota2 API directly using the player_name option of GetMatchHistory
     * You can then find their 32-bit ID in the list and then convert it to a 64-bit ID as above.
     *
     *
     *
     * FAQ
     *
     * Are match history and details going to be available for private matches?
     * These are not available at this time. We are looking at a possible OATUH based authentication system that will allow players to retrieve their private match history securely and allow third party sites to get that information they grant access to. We hope to have more details soon.
     *
     * Is there a WebAPI for current games in progress?
     * Not yet! It has been suggested, so we will just have to wait and see!
     *
     * Are there limits on how many API calls?
     * • You must manually limit your requests to one request per second in order to reduce the strain on the servers
     * • If you get a 503 Error: the matchmaking server is busy or you exceeded limits. Please wait 30 seconds and try again.
     * • Please note that as written in the WebAPI T&C's, you are limited to 100,000 API calls per day.
     * Can I persist the data in a database or similar?
     * YES! In fact, it is recommended in order to reduce the strain on the server.
     *
     * What is this "Unix time"/"UTC seconds since..."?
     * http://en.wikipedia.org/wiki/Unix_time
     *
     * How do I get a player's 32- or 64-bit SteamID?
     * See SteamIDs above!
     *
     * How to I fetch a team's logo?
     * See the example posted by DanielJ here: http://dev.dota2.com/showthread.php?...l=1#post462059
     * Though the image extension is not given, it is PNG.
     *
     * I want to start developing, should I jump right into grabbing data using the API?
     * Short answer; no. You should not.
     * Whilst you are developing your application, it will be rather silly and inconsiderate to slam the API with calls for data you just throw away.
     * In stead, consider one of two alternatives:
     * 1) (preferred) Manually make a few calls to the API and save the results to your hard drive, then use these to test from until you're confident your application does what it's supposed to.
     * 2) If you are developing your actual dynamic API calls, (first make sure you have implemented a suitable request limit as above) consider using the Dota2 Beta TEST API, which works identically to the Dota2 Beta API, except its urls are different:
     * Code:
     * Replace "IDOTA2Match_570" with "IDOTA2Match_205790"
     * Can I create a private, unofficial league and use the API to get data for it?
     * NO. You can only get publicly available data using the API, so you CANNOT get any private matches using it.
     *
     * Is there feature X in the API which will give me a set of cumulative data Y?
     * NO. The only features in the API are those listed above.
     * If you want to figure out the best hero, the best player, the most used hero, the most bought item or the team with the most wins, etc, etc, etc you have to use the available API calls to fetch the data, store it in a database and do the calculations yourself.
     *
     *
     * PLEASE REPLY WITH ANY THINGS THAT ARE WRONG/THINGS I SHOULD ADD.
     * THANKS
     *
     *
     *
     * CHANGE LOG:
     * • 11/10/2012 - Created Thread.
     * • 12/10/2012 - Made SteamIDs its own section, changed to using ResolveVanityURL (thanks to RJackson).
     * • 15/10/2012 - Quoted Zoid's "API is down" post.
     * • 27/01/2013 - Removed api is down post, added link to the api's T&C's, fixed up a few errors (thanks to sema), added to-do list.
     * • 31/01/2013 - Added info about leaver_status (thanks to adrianlegg) and lobby_type (thanks to Cyborgmatt).
     * • 03/02/2013 - Updated GetMatchHistory & GetMatchDetails (thanks to adrianlegg for game_mode data).
     * • 06/02/2013 - Updated GetMatchHistory & GetMatchDetails with latest parameters from ISteamWebAPIUtil/GetSupportedAPIList. Added GetLeagueListing, GetLiveLeagueGames & GetMatchHistoryBySequenceNum. Added info about team logo picture files in FAQ.
     * • 07/02/2013 - Added GetTeamInfoByTeamID.
     * • 03/04/2013 - Changed the links which show how tower_status and barracks_status work.
     * • 19/02/2014 - Updated the hero and item image urls for valve's servers.
     * • 04/04/2014 - Updated FAQ.
     * • 29/04/2014 - Linked out to GelioS's json data sets
     * • 04/05/2015 - Added GetGameItems
     * TODO:
     * • New functionality as per http://www.joindota.com/en/news/6545...er-file-layout
     * • items in VPK as per http://dev.dota2.com/showthread.php?...ight=items.txt (thanks to alcaras)
     * • fantasy API
     *
     * 来自 <http://dev.dota2.com/showthread.php?t=58317>
     */


}
