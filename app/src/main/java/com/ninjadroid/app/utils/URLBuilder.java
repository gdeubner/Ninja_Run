package com.ninjadroid.app.utils;

public class URLBuilder {
    private static String baseUrl = " http://cs431-08.cs.rutgers.edu:3000/docs#/";

    public static String getBaseUrl() { return baseUrl;}

    public static String getUserProfileUrl() {return baseUrl + "user_info";}

    public static String getNewUserUrl() {return baseUrl + "new_user";}

    public static String getSendRouteUrl() {return baseUrl + "send_route";}

    public static String getUpdateUserURL() {return baseUrl + "update_user";}




}
