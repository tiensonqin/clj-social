import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * QQ OAuth 2.0 api.
 */
public class QQApi20 extends DefaultApi20
{
    private static final String AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=1";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

    @Override
    public String getAccessTokenEndpoint()
        {
            return "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code";
        }

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
        {
            Preconditions
                .checkValidUrl(config.getCallback(),
                               "Must provide a valid url as callback. QQ does not support OOB");
            // Append scope if present
            if (config.hasScope())
            {
                return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
            }
            else
            {
                return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
            }
        }
}
