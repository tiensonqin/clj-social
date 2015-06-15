import org.scribe.builder.api.DefaultApi20;

import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;
import org.scribe.model.OAuthConstants;

public class GithubApi extends DefaultApi20
{
    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://github.com/login/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
        {
            Preconditions.checkValidUrl(config.getCallback(),
                                        "Must provide a valid url as callback. github does not support OOB");

            final StringBuilder sb = new StringBuilder(String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback())));
            if (config.hasScope()) {
                sb.append('&').append(OAuthConstants.SCOPE).append('=').append(OAuthEncoder.encode(config.getScope()));}
            return sb.toString();
        }
}
