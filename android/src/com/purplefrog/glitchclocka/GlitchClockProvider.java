package com.purplefrog.glitchclocka;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.content.res.*;
import android.util.*;
import android.widget.*;

import java.text.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: thoth
 * Date: 10/20/11
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class GlitchClockProvider
    extends AppWidgetProvider
{
    private static final String LOG_TAG="GlitchClockProvider";
    public static final String CLOCK_WIDGET_UPDATE = "com.purplefrog.glitchclocka.UPDATE_GLITCH_CLOCK";
    public static final String GLITCH_NEW_DAY = "com.purplefrog.glitchclocka.NEW_DAY";
    public static final int NOTIFY_NEW_DAY = 1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        final Resources res = context.getResources();
        final long glitchTime = glitchTimeSeconds();
        final String hhmm = hhmm_string(glitchTime, res);
        final String dmy = dmy_string(glitchTime, res);

        Log.d(LOG_TAG, "GlitchClock onUpdate() ; "+hhmm+"  "+dmy);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, hhmm, dmy, appWidgetId);
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String hhmm, String dmy, int appWidgetId)
    {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.glitch_clock);

        rv.setTextViewText(R.id.hhmm, hhmm);
        rv.setTextViewText(R.id.dmy, dmy);

        Intent intent = new Intent(context, LearningReadout.class);
        rv.setOnClickPendingIntent(R.id.widget, PendingIntent.getActivity(context, 0, intent, 0));

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);

        Log.d(LOG_TAG, "onEnabled()");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, roundUp(System.currentTimeMillis(), 10), 10000, createClockTickIntent(context));
    }

    @Override
    public void onDisabled(Context context)
    {
        Log.d(LOG_TAG, "onDisabled()");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));

        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
//        Log.d(LOG_TAG, "onReceive()");

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
//            Log.d(LOG_TAG, "Clock update");

            final Resources res = context.getResources();
            final long glitchTime = glitchTimeSeconds();
            final String hhmm = hhmm_string(glitchTime, res);
            final String dmy = dmy_string(glitchTime, res);

//            Log.d(LOG_TAG, "GlitchClock update text ; "+hhmm+"  "+dmy);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids) {
                updateAppWidget(context, appWidgetManager, hhmm, dmy, appWidgetID);
            }

            long glitchDay = glitchTime / (24*60*60);

            SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), 0);

            long rememberedGlitchDay = prefs.getLong("remembered_glitch_day", 0);
//            Log.d(LOG_TAG, glitchDay +" -vs- "+rememberedGlitchDay);
            if (glitchDay != rememberedGlitchDay) {
                Log.d(LOG_TAG, "new day notification");
                NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                Notification not = new Notification(R.drawable.glitch, "new Glitch day", System.currentTimeMillis());

                PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(GLITCH_NEW_DAY), 0);

                not.setLatestEventInfo(context, "New Glitch day", newDayMessage(res), pi);
                not.flags = Notification.FLAG_AUTO_CANCEL;

                mgr.notify(NOTIFY_NEW_DAY, not);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("remembered_glitch_day", glitchDay);
                editor.commit();
            }
        }

    }

    protected static Random rand = new Random();
    public String newDayMessage(Resources res)
    {
        String[] strings = res.getString(R.string.new_day_greetings).split("\\|");
        return strings[rand.nextInt(strings.length)].trim();
//        return "Wake up and nibble the piggies!";
    }

    private PendingIntent createClockTickIntent(Context context)
    {
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    //

    public static long roundUp(long x, int base)
    {
        return (x/base +1)*base;
    }



    public String dmy_string(long glitchTime, Resources res)
    {
        long glitchDay = glitchTime / (24*60*60);

        int dayPerYear = (int) (glitchDay % 308);

        String month = monthForDayOfYear(dayPerYear);
        int day = dayForDayOfYear(dayPerYear);

        return String.format(res.getString(R.string.dmy_format), day, month, (glitchDay / 308));
    }

    public static int dayForDayOfYear(int dayPerYear)
    {
        int leftover = dayPerYear;
        for (GlitchMonth month : months) {
            if (leftover < month.nDays) {
                break;
            }
            leftover -= month.nDays;
        }
        return leftover+1;
    }

    public static String monthForDayOfYear(int dayPerYear)
    {
        int leftover = dayPerYear;
        for (GlitchMonth month : months) {
            if (leftover < month.nDays) {
                return month.name;
            }
            leftover -= month.nDays;
        }
        return "overflow";
    }

    public static long glitchTimeSeconds()
    {
        return ( System.currentTimeMillis()/1000)*6 - 6847206 - 279L*308*24*60*60;
    }

    public static String hhmm_string(long glitchTime, Resources res)
    {
        int m = (int) (glitchTime / 60 % 60);
        int h = (int) (glitchTime /(60*60)  %24);

        return String.format(res.getString(R.string.hhmm_format), h, m);
    }

    public static final GlitchMonth[] months = {
        new GlitchMonth("Primuary", 29),
        new GlitchMonth("Spork", 3),
        new GlitchMonth("Bruise", 53),
        new GlitchMonth("Candy", 17),
        new GlitchMonth("Fever", 73),
        new GlitchMonth("Junuary", 19),
        new GlitchMonth("Septa", 13),
        new GlitchMonth("Remember", 37),
        new GlitchMonth("Doom", 5),
        new GlitchMonth("Widdershins", 47),
        new GlitchMonth("Eleventy", 11),
        new GlitchMonth("Recurse", 1)
    };

    public static class GlitchMonth
    {
        public final String name;
        public final int nDays;

        public GlitchMonth(String name, int nDays)
        {
            this.name = name;
            this.nDays = nDays;
        }

    }
}
