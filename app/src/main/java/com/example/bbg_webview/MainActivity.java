package com.example.bbg_webview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView iView;
    WebView wView;
    String url = "";
    String getUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url="https://industrial.myhyline.com:3334/amPlanning/tarefa_Mobile.aspx";

        wView=findViewById(R.id.webPage);

        wView.getSettings().setBuiltInZoomControls(true);
        wView.getSettings().setDisplayZoomControls(false);
        wView.getSettings().setJavaScriptEnabled(true);
        wView.setWebChromeClient(new WebChromeClient());
        wView.setWebViewClient(new WebViewClient());
        wView.setScaleX(1);
        wView.setScaleY(1);
        wView.loadUrl(url);

        iView=findViewById(R.id.imageView);
        iView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    protected void onSaveInstanceState(Bundle outState){
        wView.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}