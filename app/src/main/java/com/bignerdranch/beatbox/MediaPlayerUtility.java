package com.bignerdranch.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Tasin Ishmam on 4/27/2018.
 */
public class MediaPlayerUtility {
    private static MediaPlayerUtility sMediaPlayerUtilityInstance;

    private static final String TAG = "MediaPlayerUtility";

    public static final String SOUNDS_FOLDER = "sample_sounds";

    private static final int MAX_SOUNDS = 1;

    private Boolean mPreviewChainIsPlaying = false;


    AssetManager mAssets;
    Context mContext;




    private MediaPlayer mMediaplayer;


    public interface notifyTrackFinished {
        public void notifyFinish();
    }

    private  notifyTrackFinished callBack;

    public void initializeCallback(notifyTrackFinished callBackref){
        callBack = callBackref;
        Log.i("TAG", "Callback reference initialized");

    }

    public void reInitializeMediaPlayer() {

        mMediaplayer = new MediaPlayer();
    }



    public static MediaPlayerUtility get(Context context)
    {
        if(sMediaPlayerUtilityInstance == null)
        {
            sMediaPlayerUtilityInstance = new MediaPlayerUtility(context);
        }

        return sMediaPlayerUtilityInstance;
    }

    private MediaPlayerUtility(Context context)
    {
        mAssets = context.getAssets();
        mContext = context;
        mMediaplayer = new MediaPlayer();

    }








    public void playChain(SoundChain soundChain) throws IOException {


        if(mMediaplayer == null) reInitializeMediaPlayer();

        File playFile = soundChain.getFile(mContext);

        mMediaplayer.setDataSource(playFile.getAbsolutePath());
        mMediaplayer.prepare();
        mMediaplayer.start();

        mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(callBack != null) {
                    callBack.notifyFinish();
                    Log.i("TAG", "Callback is being done");
                }

                mMediaplayer.reset();
            }
        });



    }

    public void stopPlaying() {
        if(mMediaplayer.isPlaying())
            mMediaplayer.stop();
        mMediaplayer.reset();
        callBack.notifyFinish();
    }



    public int getChainDuration(List<Integer> itemsToPlay, List<Sound> sounds) throws IOException {
        int milis = 0;


        AssetFileDescriptor afd;


        MediaPlayer mediaPlayer = new MediaPlayer();

        for(int i = 0; i < itemsToPlay.size(); i++) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            afd = mAssets.openFd(sounds.get(itemsToPlay.get(i)).getAssetPath());
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
            milis += mediaPlayer.getDuration();


        }


        mediaPlayer.release();




        return milis;
    }

    public void playChain(List<Integer> itemsToPlay, List<Sound>  sounds) throws IOException {

        if(itemsToPlay.isEmpty())
            return;
        mPreviewChainIsPlaying = true;

        final MediaPlayer[] mediaPlayers = new MediaPlayer[itemsToPlay.size()];

        AssetFileDescriptor afd = mAssets.openFd(sounds.get(itemsToPlay.get(0)).getAssetPath());
        mediaPlayers[0] = new MediaPlayer();
        mediaPlayers[0].setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        mediaPlayers[0].prepare();
        mediaPlayers[0].start();


        for (int i = 0; i < mediaPlayers.length - 1; i++) {
            mediaPlayers[i + 1] = new MediaPlayer();
            afd = mAssets.openFd(sounds.get(itemsToPlay.get(i + 1)).getAssetPath());
            mediaPlayers[i + 1].setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayers[i + 1].prepare();
            mediaPlayers[i].setNextMediaPlayer(mediaPlayers[i + 1]);


        }

        mediaPlayers[mediaPlayers.length - 1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                mPreviewChainIsPlaying = false;
                for (MediaPlayer mediaPlayer : mediaPlayers) {
                    mediaPlayer.release();
                }
            }
        });

    }

    boolean isMediaPlayerOn() {

        if(mMediaplayer != null)
            return mMediaplayer.isPlaying() || mPreviewChainIsPlaying;
        else
            return mPreviewChainIsPlaying;
    }



    public void release()
    {
        if(mMediaplayer != null) {
            mMediaplayer.release();
            mMediaplayer = null;



    }

        if(callBack != null)
            callBack.notifyFinish();
    }

}
