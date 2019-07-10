package com.tiensonqin.Twitch;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class TwitchApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "id.twitch.tv/oauth2/authorize";
    private static final String ACCESS_TOKEN_RESOURCE = "id.twitch.tv/oauth2/token";

    protected TwitchApi() {
    }

    private static class InstanceHolder {
        private static final TwitchApi INSTANCE = new TwitchApi();
    }

    public static TwitchApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://" + ACCESS_TOKEN_RESOURCE;
    }

    @Override
    public String getAuthorizationBaseUrl() {
        return "https://" + AUTHORIZE_URL;
    }
}
