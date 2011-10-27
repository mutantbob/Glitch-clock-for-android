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
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.tinyspeck.android.*;
import org.json.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: thoth
 * Date: 10/25/11
 * Time: 4:31 PM
 */
public class LearningReadout
    extends Activity
{
    Glitch glitch;
    private TextView debugText;
    private static final String LOG_TAG = "LearningReadout";
    private TextView nameW;
    private TextView descriptionW;
    private ProgressBar progressBar;
    private ImageView skillIconW;
    private Timer timer;
    //    private long timeComplete;
//    private int totalTime;
    protected LearningState learningState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        Log.d(LOG_TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.learning);

        glitch = new Glitch(glitchClientKey(), "glitchandroidsdk://auth");

        debugText = (TextView) findViewById(R.id.debug);
        nameW = (TextView)findViewById(R.id.skillName);
        skillIconW = (ImageView)findViewById(R.id.skillIcon);
        descriptionW = (TextView)findViewById(R.id.description);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        glitch.accessToken = memorizedAccessToken();
        
        if (checkStateMachine())
            return;

    }

    private String memorizedAccessToken()
    {
        SharedPreferences prefs = getPreferences(0);
        final String remembered = prefs.getString("glitchToken", null);
//        Log.d(LOG_TAG, "remembered glitch token = "+remembered);
        return remembered;
    }

    protected void memorizeToken(String token)
    {
        SharedPreferences prefs = getPreferences(0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("glitchToken", token);
        ed.commit();

//        Log.d(LOG_TAG, "stashed token "+token);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        learningState = savedInstanceState.getParcelable("learningState");
        glitch.accessToken = savedInstanceState.getString("glitchToken");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable("learningState", learningState);
        outState.putString("glitchToken", glitch.accessToken);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

//        Log.d(LOG_TAG, "onResume()");

        if (null == learningState) {
            learningState = new LearningState("fake skill", System.currentTimeMillis() / 1000 + 60, 2 * 60, "", "fake description");

            debugLearningData();

        } else {
//            Log.d(LOG_TAG, "restored");
            updateReadoutFull();
        }

        ensureTimerRunning();
    }

    public synchronized void ensureTimerRunning()
    {
        if (timer != null)
            return;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run()
            {
                updateProgressTimeBar();
            }
        };
        timer= new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 10000);
    }

    @Override
    protected void onPause()
    {
//        Log.d(LOG_TAG, "onPause()");

        synchronized (this) {
            if (null!=timer) {
                timer.cancel();
                timer = null;
            }
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshLearningData();
                return true;
            case R.id.about:
                if (true) {
                    startActivity(new Intent(this, AboutActivity.class));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("file:///android_asset/about.html")));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //

    protected void refreshLearningData()
    {
        if (!glitch.isAuthenticated()) {
            glitch.authorize("read", this);
            return;
        }

        rpcListLearning();
    }

    /**
     * This intent can be activated two ways, by clicking on one of our AppWidgets, or is a callback from the OAuth mechanism.
     * This function returns true if we've been called from OAuth and need to harvest the auth data.
     * This function returns false if we've been activated by any other mechanism
     */
    protected boolean checkStateMachine()
    {
        Intent intent = getIntent();

        if (intent.hasCategory(Intent.CATEGORY_BROWSABLE))
        {
            final Uri uri = intent.getData();

            if (uri != null)
            {
                glitch.handleRedirect(uri, new MyGlitchSessionDelegate());
                return true;
            }
        }

        return false;
    }

    /**
     * This exists so I can load test data into the widget without bugging the central server.
     */
    @SuppressWarnings({"ConstantIfStatement"})
    protected void debugLearningData()
    {
        Log.d(LOG_TAG, "debugLearningData()");
        if (true) {

            refreshLearningData();

        } else if (true) {


            try {
                final String JSON_1 = "{\"ok\":1,\"learning\":[]}";
                final String JSON_2 = "{\"ok\":1,\"learning\":{\"betterlearning_4\":{\"class_tsid\":\"betterlearning_4\",\"required_skills\":[\"betterlearning_3\"],\"time_remaining\":233323,\"required_level\":0,\"icon_44\":\"http://c1.glitch.bz/img/skills/betterlearning_4_28144.png\",\"icon_460\":\"http://c1.glitch.bz/img/skills-460/betterlearning_4_28144.png\",\"total_time\":242374,\"time_complete\":1319879122,\"name\":\"Better Learning IV\",\"icon_100\":\"http://c1.glitch.bz/img/skills-100/betterlearning_4_28144.png\",\"url\":\"/skills/59/\",\"time_start\":1319643807,\"description\":\"At the fourth level of Better Learning, you can chop a whopping 12% off the time required to learn all future skills - and the first 32 carry no time penalty. Better Learning: for the serious self-edumacator.\"}}}";
                handleJSONResponse(new JSONObject(JSON_2));
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WTF?", e);
            }
        } else {
            learningState = new LearningState("fake skill", System.currentTimeMillis()/1000 + 60, 2*60, "", "fake description");
            updateReadoutFull();
        }
    }

    public static String glitchClientKey()
    {
        /**
         * The API key is sensitive information and should not be checked in to GIT or shared with anyone.
         * If it is shared, irresponsible entities could use it in a way that forces
         * the Glitch operators to revoke the key and cripple this application.
         *
         * To find out how to register for your own Glitch API key visit
         * http://developer.glitch.com/api/
         */
        return DontCheckInToGit.glitchKey;
    }

    public void updateReadoutFull()
    {
        nameW.setText(learningState.name);
        if (false && learningState.skillIconURL!=null && learningState.skillIconURL.length()>0) {
            skillIconW.setImageURI(Uri.parse(learningState.skillIconURL));
        }
        descriptionW.setText(learningState.description);

        Log.d(LOG_TAG, "configuring progress bar");
        if (learningState.totalTime > 0) {
            progressBar.setMax(learningState.totalTime);

            ensureTimerRunning();

            updateProgressTimeBar();
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    protected void updateProgressTimeBar()
    {
//        Log.d(LOG_TAG, "tick");
        int prog = learningState.totalTime - (int) (learningState.timeComplete - System.currentTimeMillis() / 1000);
        if (prog > learningState.totalTime) {
            timer.cancel();
        }
        progressBar.setProgress(prog);
    }

    public void handleJSONResponse(JSONObject json)
    {
        JSONObject learning_ = json.optJSONObject("learning");

        if (learning_ == null) {
            learningState=new LearningState("nothing", System.currentTimeMillis() / 1000, 0, "", "you're not learnin' nuthin.");
            updateReadoutFull();
            return;
        }

        String skillId = learning_.keys().next().toString();
        JSONObject learning = learning_.optJSONObject(skillId);

        String name= learning.optString("name");
        long timeComplete = learning.optLong("time_complete");
        int totalTime = learning.optInt("total_time");
        String skillIconURL = learning.optString("icon_100");
        String description = learning.optString("description");

        learningState = new LearningState(name, timeComplete, totalTime, skillIconURL, description);
        updateReadoutFull();

        final String partial = name + " " + timeComplete + " " + totalTime + " " + skillIconURL + " \n" + description;

        debugText.setText(partial + "\n\n" + json);
    }

    protected void rpcListLearning()
    {
        Log.d(LOG_TAG, "starting glitch API call");
        GlitchRequest req = glitch.getRequest("skills.listLearning");
        req.execute(new GlitchRequestDelegate()
        {
            public void requestFinished(GlitchRequest request)
            {
                final JSONObject json = request.response;
                debugText.setText(json.toString());

                Log.d(LOG_TAG, "" + json);

                handleJSONResponse(json);
            }

            public void requestFailed(GlitchRequest request)
            {
                glitch.accessToken = null;// just in case our remembered token was bad
                
                Log.d(LOG_TAG, "glitch request failed : "+request.response);

                debugText.setText("blam : " + request.response);

                Toast.makeText(LearningReadout.this, "Glitch authentication failed.  If our access token has expired, perform a refresh to get a new token.", Toast.LENGTH_LONG).show();
            }
        });
    }
    //

    private class MyGlitchSessionDelegate
        implements GlitchSessionDelegate
    {
        public void glitchLoginSuccess()
        {
            debugText.setText("login success");

            memorizeToken(glitch.accessToken);

            Runnable r = new Runnable()
            {
                public void run()
                {
                    rpcListLearning();
                }
            };

            runOnUiThread(r);
        }



        public void glitchLoginFail()
        {
            debugText.setText("login fail");
        }

        public void glitchLoggedOut()
        {
            debugText.setText("logged out");
        }
    }
}
