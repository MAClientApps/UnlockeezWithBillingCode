package com.wifisecure.unlockeez.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.wifisecure.unlockeez.R;

public class UnLockeEzWebViewActivity extends AppCompatActivity {

    private WebView unlockkwebView;
//    Network networkInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_unlockk_redirect);

        unlockkwebView = findViewById(R.id.redirect_webview_unlockk);
        unlockkwebView.setWebViewClient(new WebViewClient());
        unlockkwebView.getSettings().setLoadsImagesAutomatically(true);
        unlockkwebView.getSettings().setJavaScriptEnabled(true);
        unlockkwebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        unlockkwebView.loadUrl("https://www.google.com/");
    }
}