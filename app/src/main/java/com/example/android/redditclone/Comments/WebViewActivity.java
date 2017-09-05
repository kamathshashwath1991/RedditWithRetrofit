package com.example.android.redditclone.Comments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.redditclone.R;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        WebView webView= (WebView) findViewById(R.id.webview);
        final ProgressBar progressBar= (ProgressBar) findViewById(R.id.webviewLoadingProgressBar);
        final TextView loadingText= (TextView) findViewById(R.id.ProgressText);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent= getIntent();
        String url= intent.getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                loadingText.setText("");
            }
        });
    }



}
