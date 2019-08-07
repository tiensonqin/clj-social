package com.tiensonqin.Mixer;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class MixerApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "mixer.com/oauth/authorize";
    private static final String ACCESS_TOKEN_RESOURCE = "mixer.com/api/v1/oauth/token";

    protected MixerApi() {
    }

    private static class InstanceHolder {
        private static final MixerApi INSTANCE = new MixerApi();
    }

    public static MixerApi instance() {
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
