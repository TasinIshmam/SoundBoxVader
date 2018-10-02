package com.bignerdranch.beatbox;

/**
 * Created by Tasin Ishmam on 4/27/2018.
 */
public class Sound {
    private String mAssetPath;
    private String mName;
    private Integer mSoundID;

    public Integer getSoundID() {
        return mSoundID;
    }

    public void setSoundID(Integer soundID) {
        mSoundID = soundID;
    }

    public Sound(String assetPath) { mAssetPath = assetPath;

        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        mName = filename.replace(".mp3", "");

    }
    public String getAssetPath() {
        return mAssetPath;
    }
    public String getName() {
        return mName;
    }

    public int compareTo(final Sound o)
    {
        return mName.compareToIgnoreCase(o.getName());
    }
}