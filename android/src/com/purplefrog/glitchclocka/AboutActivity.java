/*
    Copyright (C) 2011 Robert Forsman

    This file is part of Glitch clock for android.

    Glitch clock for android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Glitch clock for android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Glitch clock for android.  If not, see <http://www.gnu.org/licenses/>.
 */

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
