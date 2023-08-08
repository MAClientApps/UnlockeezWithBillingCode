package com.wifisecure.unlockeez.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.wifisecure.unlockeez.R;
import com.wifisecure.unlockeez.UnLockeEzMainPageActivity;
import com.wifisecure.unlockeez.Utils;


public class UnLockeEzPremiumActivity extends AppCompatActivity {

    private WebView viewUnlockeezPremium;
    LinearLayout layoutCheckConnection;
    Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlockeez_premuim);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initView() {
        viewUnlockeezPremium = findViewById(R.id.viewUnlockeezPremium);
        layoutCheckConnection = findViewById(R.id.layoutCheckConnection);
        CookieManager.getInstance().setAcceptCookie(true);
        viewUnlockeezPremium.getSettings().setJavaScriptEnabled(true);
        viewUnlockeezPremium.getSettings().setUseWideViewPort(true);
        viewUnlockeezPremium.getSettings().setLoadWithOverviewMode(true);
        viewUnlockeezPremium.getSettings().setDomStorageEnabled(true);
        viewUnlockeezPremium.getSettings().setPluginState(WebSettings.PluginState.ON);
        viewUnlockeezPremium.setWebChromeClient(new WebChromeClient());
        viewUnlockeezPremium.setVisibility(View.VISIBLE);

        viewUnlockeezPremium.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                String url = request.getUrl().toString();
                if (!url.startsWith("http")) {
                    startActivity(new Intent(UnLockeEzPremiumActivity.this, UnLockeEzMainPageActivity.class));
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        finish();
                        return;
                    } catch (Exception e) {
                        finish();
                        return;
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        loadDataView();
    }

    public void checkInternetConnection() {
        layoutCheckConnection.setVisibility(View.VISIBLE);
        btnRetry = findViewById(R.id.btnTryAgain);
        btnRetry.setOnClickListener(view -> {
            layoutCheckConnection.setVisibility(View.GONE);
            loadDataView();
        });
    }

    protected void loadDataView() {
        if (Utils.isNetworkAvailable(this)) {
            viewUnlockeezPremium.loadUrl(Utils.generatePremiumLink(UnLockeEzPremiumActivity.this));
        } else {
            checkInternetConnection();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewUnlockeezPremium.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewUnlockeezPremium.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewUnlockeezPremium.loadUrl("about:blank");
    }

    @Override
    public void onBackPressed() {
    }

}
