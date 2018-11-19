package android.trithe.real.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.webkit.WebView;

public class WebViewBrowserActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_browser);
        webView = findViewById(R.id.webview);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String a = bundle.getString("url");
        webView.loadUrl(a);
        webView.getSettings().setJavaScriptEnabled(true);
        //load ảnh tự động
        webView.getSettings().setLoadsImagesAutomatically(true);

    }
}
