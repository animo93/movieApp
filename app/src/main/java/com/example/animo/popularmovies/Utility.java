package com.example.animo.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by animo on 2/11/16.
 */
public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.sort_order_key), context.getString(R.string.sort_order_default));

    }
}
