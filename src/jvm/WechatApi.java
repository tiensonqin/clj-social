import org.scribe.builder.api.DefaultApi20;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * Wechat OAuth 2.0 api.
 */
public class WechatApi extends DefaultApi20
{
    private static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code";
    // private static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code#wechat_redirect";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

    @Override
    public String getAccessTokenEndpoint()
        {
            return "https://api.weixin.qq.com/sns/oauth2/access_token";
        }

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
        {
            Preconditions.checkValidUrl(config.getCallback(),
                                        "Must provide a valid url as callback. Wechat does not support OOB");

            final StringBuilder sb = new StringBuilder(String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback())));
            if (config.hasScope()) {
                sb.append('&').append(OAuthConstants.SCOPE).append('=').append(OAuthEncoder.encode(config.getScope()));}
            if (config.hasState()) {
                sb.append('&').append(OAuthConstants.STATE).append('=').append(OAuthEncoder.encode(config.getState()));
            }
            return sb.toString();
        }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new AccessTokenExtractor() {

            @Override
            public Token extract(String response) {
                Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");

                Matcher matcher1 = Pattern.compile("\"access_token\":\"([^&\"]+)\"").matcher(response);
                Matcher matcher2 = Pattern.compile("\"openid\":\"([^&\"]+)\"").matcher(response);
                if (matcher1.find() && matcher2.find())
                {
                    String token = OAuthEncoder.decode(matcher1.group(1));
                    String openid = OAuthEncoder.decode(matcher2.group(1));
                    return new Token(token, openid, response);
                }
                else
                {
                    throw new OAuthException("Response body is incorrect. Can't extract a token from this: '" + response + "'", null);
                }
            }
        };
    }

    @Override
    public OAuthService createService(OAuthConfig config) {
        return new WechatOAuth2Service(this, config);
    }

    private class WechatOAuth2Service extends OAuth20ServiceImpl {

        private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
        private static final String GRANT_TYPE = "grant_type";
        private DefaultApi20 api;
        private OAuthConfig config;

        public WechatOAuth2Service(DefaultApi20 api, OAuthConfig config) {
            super(api, config);
            this.api = api;
            this.config = config;
        }


        @Override
        public Token getAccessToken(Token requestToken, Verifier verifier) {
            OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
            request.addQuerystringParameter("appid", config.getApiKey());
            request.addQuerystringParameter("secret", config.getApiSecret());
            request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
            request.addQuerystringParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);

            Response response = request.send();

            return api.getAccessTokenExtractor().extract(response.getBody());
        }
    }
}
