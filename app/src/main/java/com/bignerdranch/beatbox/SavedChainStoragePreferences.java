package com.bignerdranch.beatbox;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Tasin Ishmam on 6/26/2018.
 */
public class SavedChainStoragePreferences {


    public static final String SHAREDPREF_KEY = "com.bignerdranch.beatbox.SHAREDPREF_KEY";
    public static final String SHAREDPREF_SOUNDCHAINLAB_ACCESS_KEY = "com.bignerdranch.beatboxSHAREDPREF_SOUNDCHAINLAB_ACCESS_KEY";
    public static final String SHAREDPREF_VERSION_DISPLAY_KEY = "com.bignerdranch.beatbox_SHAREDPREF_VERSION_DISPLAY_KEY";

 public static  SoundChainLab  getSavedSoundChainLab (Context context) {

        SharedPreferences mPrefs = context.getSharedPreferences(SHAREDPREF_KEY, context.MODE_PRIVATE);

        Gson gson = new Gson();

        String json = mPrefs.getString(SHAREDPREF_SOUNDCHAINLAB_ACCESS_KEY,"");

        return  gson.fromJson(json, SoundChainLab.class);

    }


    public static void saveSoundChainLab(Context context, SoundChainLab soundChainLab) {


        SharedPreferences mPrefs = context.getSharedPreferences(SHAREDPREF_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(soundChainLab);
        prefsEditor.putString(SHAREDPREF_SOUNDCHAINLAB_ACCESS_KEY, json);
        prefsEditor.apply();
    }

    public static String getSavedVersionName(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(SHAREDPREF_KEY, context.MODE_PRIVATE);

        String version = mPrefs.getString(SHAREDPREF_VERSION_DISPLAY_KEY,"");

        return version;

    }

    public static void setSavedVersionName(Context context, String name) {
        SharedPreferences mPrefs = context.getSharedPreferences(SHAREDPREF_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(SHAREDPREF_VERSION_DISPLAY_KEY, name);
        prefsEditor.apply();
    }

}
