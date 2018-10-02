package com.bignerdranch.beatbox;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tasin Ishmam on 6/6/2018.
 */
public class SoundChainLab implements Serializable {

    private static SoundChainLab sSoundChainLab;
    List<SoundChain> mSoundChains;
   transient Context  mContext;


    public static final String SHAREDPREF_KEY = "com.bignerdranch.beatbox.SHAREDPREF_KEY";

    public static final String SHAREDPREF_SOUNDCHAINLAB_ACCESS_KEY = "com.bignerdranch.beatboxSHAREDPREF_SOUNDCHAINLAB_ACCESS_KEY";


    public static SoundChainLab get(Context context) {


        if(sSoundChainLab == null)
        {



            try
            {

                sSoundChainLab = SavedChainStoragePreferences.getSavedSoundChainLab(context);
                sSoundChainLab.mContext = context;
            } catch (Exception e)
            {
                e.printStackTrace();
                Log.i("TAG", "Error trying to retreive SoundChainLab from Sharedpref Json data ");
                sSoundChainLab = new SoundChainLab(context);

            }


        }
        return sSoundChainLab;
    }

    private SoundChainLab(Context context) {


           mSoundChains = new ArrayList<>();


        mContext = context;
    }

    public void addChain (SoundChain soundChain) {

        mSoundChains.add(soundChain);



    }

    public List<SoundChain> getSoundChains() {
        return mSoundChains;
    }

    SoundChain getSoundChain(String name) {

        for(SoundChain soundChain : mSoundChains){
            if(soundChain.getName().equals(name))
                return soundChain;
        }

        return  null;
    }
}
