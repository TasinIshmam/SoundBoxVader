package com.bignerdranch.beatbox;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tasin Ishmam on 6/6/2018.
 */
public class SoundChain implements Serializable {

    private  List<Integer> mSoundChainList;
   private String mName;
   private int mDuratingInMilis = 0;

    public SoundChain(List<Integer> soundChainList, String name) {
        mSoundChainList = new ArrayList<>(soundChainList);
        this.mName = name;
    }

    public int getDuratingInMilis() {
        return mDuratingInMilis;
    }

    public void setDuratingInMilis(int duratingInMilis) {
        this.mDuratingInMilis = duratingInMilis;
    }

    public String getDurationString() {

        int secDuration = (mDuratingInMilis / 1000) + 1;
        int minutes = secDuration / 60;
        int seconds = secDuration % 60;

        String ret = Integer.toString(minutes) + ":" + Integer.toString(seconds);

        return ret;
    }

    public List<Integer> getSoundChainList() {
        return mSoundChainList;
    }

    public String getName() {
        return mName;
    }

    public File getFile(Context context){
       return new File(context.getFilesDir(), getName() + ".mp3");
    }
}
