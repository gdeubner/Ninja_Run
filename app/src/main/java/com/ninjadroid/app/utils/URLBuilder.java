package com.ninjadroid.app.utils;

public class URLBuilder {
    private static String scheme = "http";

    private static String encodedAuthority = "cs431-08.cs.rutgers.edu:3002";

    private static String baseUrl = " http://cs431-08.cs.rutgers.edu:3002/";

    public static String getBaseUrl() { return baseUrl;}

    public static String getTestPath() {return "test";}

    public static String getUserIDPath() {return "user_login";}

    public static String getUserNamePath() {return "user_info";}

    public static String getUserProfilePath() {return "user_stats";}

    public static String getNewUserPath() {return "new_user";}

    public static String getSendRoutePath() {return "send_route";}

    public static String getGetRoutePath() {return "get_route";}

    public static String getUpdateUserPath() {return "update_user";}

    public static String getShareRoute() { return "share_route";}

    public static String getSharedHist() { return "shared_routes";}

    public static String getRouteHistoryPath() {return "route_history";}

    public static String addFollow() {return "add_follow";}

    public static String showFollowingList() {return "show_followinglist";}

    public static String registerUser() {return "register_user";}

    public static String updateProfile() {return "update_userprofile";}

    public static String getFollowerList() {return "show_followerlist";}

    public static String getAddHistory() {return "add_history";}

    public static String deleteShared() {return "delete_shared";}

    public static String getScheme() {
        return scheme;
    }

    public static String getEncodedAuthority() {
        return encodedAuthority;
    }
}
