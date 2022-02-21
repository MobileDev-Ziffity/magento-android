package in.yale.mobile;

import android.util.Base64;

import com.comapi.ComapiAuthenticator;
import com.comapi.internal.network.AuthClient;
import com.comapi.internal.network.ChallengeOptions;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ChallengeHandler extends ComapiAuthenticator {

    String emailAddress = "";
    @Override
    public void onAuthenticationChallenge(AuthClient authClient, ChallengeOptions challengeOptions) {

        try {

            byte[] data;
            //<Shared secret> string must be the same as the value of the 'Shared secret' field in your push notification profile in Engagement Cloud.
            data = Constants.DOTDIGITAL_SECRET.getBytes("UTF-8");

            String base64Secret = Base64.encodeToString(data, Base64.DEFAULT);

            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");

            Map<String, Object> claims = new HashMap<>();
            claims.put("nonce", challengeOptions.getNonce());
            //<ID claim> string must be the same as the value of the 'ID claim' field in your push notification profile in Engagement Cloud.
            claims.put("sub", emailAddress);
            //<Audience> string must be the same as the value of the 'Audience' field in your push notification profile in Engagement Cloud.
            claims.put("aud", "https://api.comapi.com");
            //<Issuer> string must be the same as the value of the 'Issuer' field in your push notification profile in Engagement Cloud.
            claims.put("iss", "https://api.comapi.com/defaultauth");
            claims.put("iat", System.currentTimeMillis());
            claims.put("exp", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));

            final String token = Jwts.builder()
                    .setHeader(header)
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, base64Secret)
                    .compact();

            //Pass the JWT token to the SDK.
            authClient.authenticateWithToken(token);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //Authorisation failed.
            authClient.authenticateWithToken(null);
        }
    }
}
