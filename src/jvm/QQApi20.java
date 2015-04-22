import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;
import org.scribe.model.OAuthConstants;
/**
 * QQ OAuth 2.0 api.
 */
public class QQApi20 extends DefaultApi20
{
    private static final String AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code";

    @Override
    public String getAccessTokenEndpoint()
        {
            return "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code";
        }

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
        {
            Preconditions.checkValidUrl(config.getCallback(),
                                        "Must provide a valid url as callback. QQ does not support OOB");

            final StringBuilder sb = new StringBuilder(String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback())));
            if (config.hasScope()) {
                sb.append('&').append(OAuthConstants.SCOPE).append('=').append(OAuthEncoder.encode(config.getScope()));}
            if (config.hasState()) {
                sb.append('&').append(OAuthConstants.STATE).append('=').append(OAuthEncoder.encode(config.getState()));
            }
            return sb.toString();
        }
}
