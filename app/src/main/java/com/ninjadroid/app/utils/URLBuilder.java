package com.ninjadroid.app.utils;

public class URLBuilder {
    private static String scheme = "http";

    private static String encodedAuthority = "cs431-08.cs.rutgers.edu:3000";

    private static String baseUrl = " http://cs431-08.cs.rutgers.edu:3000/";

    public static String getBaseUrl() { return baseUrl;}

    public static String getTestPath() {return "test";}

    public static String getUserIDPath() {return "user_login";}

    public static String getUserProfilePath() {return "user_stats";}

    public static String getNewUserPath() {return "new_user";}

    public static String getSendRoutePath() {return "send_route";}

    public static String getUpdateUserPath() {return "update_user";}

    public static String getShareRoute() { return "share_route";}

    public static String getSharedHist() { return "shared_routes";}

    public static String getRouteHistoryPath() {return "route_history";}


    public static String getScheme() {
        return scheme;
    }

    public static String getEncodedAuthority() {
        return encodedAuthority;
    }
}
