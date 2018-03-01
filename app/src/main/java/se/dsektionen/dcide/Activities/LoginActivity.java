package se.dsektionen.dcide.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Utilities.LoginWebViewClient;

public class LoginActivity extends AppCompatActivity implements LoginWebViewClient.TokenFoundListener {
    private static final String TOKEN_URL = "https://dsek-api-dev.herokuapp.com/account/token";
    private static final String REDIRECT_HOST = "success.d-sektionen.se";

    private LoginWebViewClient webViewClient;

    public LoginActivity() {
        this.webViewClient = new LoginWebViewClient(REDIRECT_HOST);
        this.webViewClient.setOnTokenFoundListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        WebView webView = findViewById(R.id.login_webview);
        initializeWebView(webView);
    }

    @Override
    public void onTokenFound(String token) {
        DCideApp.getInstance().getUserSessionManager().setToken(token);
        Log.d("LOGIN", "TOKEN FOUND");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(String.format("%s?redirect=http://%s", TOKEN_URL, REDIRECT_HOST));
    }
}
