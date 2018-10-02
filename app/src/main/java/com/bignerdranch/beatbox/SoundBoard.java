package com.bignerdranch.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tasin Ishmam on 6/26/2018.
 */
public class SoundBoard {

    private static final String TAG = "SoundBoard";

    public static final String SOUNDS_FOLDER = "sample_sounds";

    private static final int MAX_SOUNDS = 1;

    AssetManager mAssets;
    Context mContext;

    private SoundPool mSoundPool;


    private List<Sound> mSounds = new ArrayList<>();

    public SoundBoard(Context context) {
        mAssets = context.getAssets();
        // This old constructor is deprecated, but we need it for
        // compatibility.
        //noinspection deprecation
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();

        Collections.sort(mSounds, new Comparator<Sound>() {
            @Override
            public int compare(Sound o1, Sound o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    private void loadSounds() {
        String[] soundNames = new String[1];

        try{
            soundNames = mAssets.list(SOUNDS_FOLDER);
            Log.i("TAG", "Found " + soundNames.length + "sounds");

            for(String name : soundNames)
            {   if (name.contains("dhormo")) continue;

                String Assetpath = SOUNDS_FOLDER + "/" + name;
                Sound sound = new Sound(Assetpath);
                load(sound);
                mSounds.add(sound);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void load(Sound sound) throws IOException{
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundID(soundId);

    }

    public void play(Sound sound) {
        Integer soundID = sound.getSoundID();

        if(soundID == null) return;

        mSoundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);

    }

    public  List<Sound> getSounds()
    {
        return mSounds;
    }

    public void release()
    {
          mSoundPool.release();
    }

}
