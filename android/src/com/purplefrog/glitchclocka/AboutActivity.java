package com.purplefrog.glitchclocka;

import android.app.*;
import android.os.*;
import android.webkit.*;

/**
 * Created by IntelliJ IDEA.
 * User: thoth
 * Date: 10/27/11
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class AboutActivity
    extends Activity
{
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/about.html");
    }
}
