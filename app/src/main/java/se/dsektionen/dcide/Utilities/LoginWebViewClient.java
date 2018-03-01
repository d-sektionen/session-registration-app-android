package se.dsektionen.dcide.Utilities;

import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginWebViewClient extends WebViewClient {
    private static final String TAG = LoginWebViewClient.class.getName();

    private String successHost;
    private TokenFoundListener tokenFoundListener;

    public LoginWebViewClient(String successHost) {
        this.successHost = successHost;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (host.equals(successHost)) {
            String token = uri.getQueryParameter("token");
            if (tokenFoundListener != null) {
                tokenFoundListener.onTokenFound(token);
            } else {
                Log.w(TAG, "Token found but no listener was available for processing");
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }


    public void setOnTokenFoundListener(TokenFoundListener listener) {
        this.tokenFoundListener = listener;
    }

    public interface TokenFoundListener {
        void onTokenFound(String token);
    }
}
