package com.bignerdranch.beatbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.beatbox.databinding.FragmentBeatBoxBinding;
import com.bignerdranch.beatbox.databinding.ListItemSoundBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tasin Ishmam on 4/27/2018.
 */
public class BeatBoxFragment extends android.support.v4.app.Fragment {





    public List<Sound> mSounds;
    public List<Integer> mChainedItems;



    MenuItem mCancelButton;
    private MediaPlayerUtility mMediaPlayerUtility;
    private SoundBoard mSoundBoard;

    int mSelectionMode = 0;

    private static final int COMBINE_MODE_FLAG = 1;
    private static final int SHARE_MODE_FLAG = 2;

    private static final int DEFAULT_MODE_FLAG = 0;

    private static final int BUTTON_WIDTH_IN_DP = 120;
    SoundAdapter mSoundAdapter;



    private static final int REQUST_NAME = 0;
    private static final String DIALOG_NAME = "Dialog Name";
    private static final String SAVE_INSTANCE_STATE_KEY = "com.bignerdranch.beatbox_SAVEINSTANCESTATE";
    private static final String DEFAULT_VERSION_NAME_V2 = "failed to extract version name placeholder Version 2";
    private static final String CHANGE_LOG_DIALOG_TAG = "Change Log Dialog";

    FirebaseAnalytics mFirebaseAnalytics;

    public void testAnalyticsEventLog() {


// Create a Bundle containing information about
// the analytics event
        Bundle eventDetails = new Bundle();
        eventDetails.putString("my_message", "Clicked that special button");

// Log the event
        mFirebaseAnalytics.logEvent("my_custom_event", eventDetails);
    }



    @Override
    public void onDetach() {
        super.onDetach();

        mGoToSoundLabFragmentCallbackInterface = null;
        mNotifyDataSetChangeUpdateInterface = null;
    }

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

     FragmentBeatBoxBinding mBinding;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSoundBoard = new SoundBoard(getActivity());
    }








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_cancel: {
                mChainedItems.clear();
                toggleMode(DEFAULT_MODE_FLAG);
                if(mSoundAdapter != null) mSoundAdapter.notifyDataSetChanged();
                return true;
            }

            case R.id.menu_item_goto_sound_lab: {
                if(mGoToSoundLabFragmentCallbackInterface != null)
                    mGoToSoundLabFragmentCallbackInterface.startSoundLabFragment();
                return  true;
            }

            case R.id.menu_item_play_chain_preview: {
                if(!mMediaPlayerUtility.isMediaPlayerOn()) {
                    try {
                        mMediaPlayerUtility.playChain(mChainedItems, mSounds);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            case R.id.menu_item_share_button: {
                toggleMode(SHARE_MODE_FLAG);
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }



    void toggleMode(int mode) {


        mSelectionMode = mode;

        mChainedItems.clear();

        getActivity().invalidateOptionsMenu();

        if(mBinding != null) {
            if(mSelectionMode == COMBINE_MODE_FLAG) {
                mBinding.fab.setImageResource(R.drawable.ic_tick);
            } else {
                mBinding.fab.setImageResource(R.drawable.ic_plus_icon);
            }
        }


            if(mSoundAdapter != null) mSoundAdapter.notifyDataSetChanged();



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_beat_box, menu);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Sound Box");



        mCancelButton = menu.findItem(R.id.menu_item_cancel);
        MenuItem shareButton = menu.findItem(R.id.menu_item_share_button);
        MenuItem playChainPreviewButton = menu.findItem(R.id.menu_item_play_chain_preview);

        if(mSelectionMode == COMBINE_MODE_FLAG ){
            mCancelButton.setVisible(true);
            playChainPreviewButton.setVisible(true);
            shareButton.setVisible(false);

        } else if(mSelectionMode == DEFAULT_MODE_FLAG)
        {
            mCancelButton.setVisible(false);
            shareButton.setVisible(true);
            playChainPreviewButton.setVisible(false);

        } else if(mSelectionMode == SHARE_MODE_FLAG)
        { mCancelButton.setVisible(true);
        shareButton.setVisible(false);
        playChainPreviewButton.setVisible(false);
        }










    }



    private NotifyDataSetChangeUpdateInterface mNotifyDataSetChangeUpdateInterface;
    private GoToSoundLabFragmentCallbackInterface mGoToSoundLabFragmentCallbackInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof NotifyDataSetChangeUpdateInterface){
            mNotifyDataSetChangeUpdateInterface = (NotifyDataSetChangeUpdateInterface) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement NotifyDataSetChangeUpdateInterface");
        }

        if(context instanceof GoToSoundLabFragmentCallbackInterface){
            mGoToSoundLabFragmentCallbackInterface = (GoToSoundLabFragmentCallbackInterface) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement GoToSoundLabFragmentCallbackInterface");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_beat_box, container, false);

        mContext = getContext();
        mMediaPlayerUtility = MediaPlayerUtility.get(mContext);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

       // testAnalyticsEventLog();
        //Fabric.with(this, new Crashlytics());



        mSounds = mSoundBoard.getSounds();

        setRetainInstance(true);

        if(savedInstanceState != null) {
            WrapperClassForStateSave wrapperClassForStateSave = (WrapperClassForStateSave) savedInstanceState.getSerializable(SAVE_INSTANCE_STATE_KEY);

            mSelectionMode = wrapperClassForStateSave.getSelectionMode();
            mChainedItems = wrapperClassForStateSave.getChainedItens();
        } else {
            mChainedItems = new ArrayList<Integer>();
        }

        toggleMode(mSelectionMode);


        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), findSpanCount()));
          mSoundAdapter = new SoundAdapter(mSoundBoard.getSounds());
        mBinding.recyclerView.setAdapter(mSoundAdapter);


        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSelectionMode == COMBINE_MODE_FLAG) {
                    if(mChainedItems.size() >= 1) {
                        FragmentManager manager = getFragmentManager();
                        NamePickerFragment dialog = NamePickerFragment.newInstance();

                        dialog.setTargetFragment(BeatBoxFragment.this, REQUST_NAME);
                        dialog.show(manager, DIALOG_NAME);
                    } else
                    {
                        Toast.makeText(getActivity(), R.string.chain_instructions,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toggleMode(COMBINE_MODE_FLAG);
                }


                /* try {
                    mMediaPlayerUtility.playChain(mChainedItems);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mChainedItems.clear();
                adapter.notifyDataSetChanged();*/

            }
        });


        checkForChangeLog();

        return mBinding.getRoot();
    }

    private void checkForChangeLog() {

        String version = DEFAULT_VERSION_NAME_V2;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
             version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String storedVersion = SavedChainStoragePreferences.getSavedVersionName(getContext());

        if(storedVersion.equals(version) || storedVersion.equals(DEFAULT_VERSION_NAME_V2)) return;


        SavedChainStoragePreferences.setSavedVersionName(getContext(), version);

        FragmentManager manager = getFragmentManager();
        /*ChangeLogDialogFragment dialog = ChangeLogDialogFragment.newInstance();

        dialog.show(manager, CHANGE_LOG_DIALOG_TAG);
*/
    }

    private int findSpanCount() {
        int spanCount;

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 3;
        } else {
            spanCount = 5;        }


        try {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            double wi = (double) width / (double) dm.xdpi;
            double hi = (double) height / (double) dm.ydpi;

            double buttonWidthInInches = BUTTON_WIDTH_IN_DP / 160.0d;

             spanCount = (int) (wi / buttonWidthInInches);

            Log.i("TAG", "NO of buttons is " + spanCount);
        } catch (Exception e) {
                e.printStackTrace();
        }


        return spanCount;




    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> items = new ArrayList<>(mChainedItems);
        outState.putSerializable
                (SAVE_INSTANCE_STATE_KEY, new WrapperClassForStateSave(items ,Integer.valueOf(mSelectionMode)));




    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSoundBoard.release();
    }


    private class SoundHolder extends RecyclerView.ViewHolder {

        ListItemSoundBinding mBinding;
        Sound mSound;
        int position;

        public SoundHolder(ListItemSoundBinding binding ){
            super(binding.getRoot());

            mBinding = binding;





        }

        public void bind(Sound sound,int pos) {

        mBinding.playSoundButton.setText(sound.getName());
        mSound = sound;
        position = pos;
        }




    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            ListItemSoundBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_sound, parent, false);

            return new SoundHolder(binding);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
           final Sound sound =  mSounds.get(position);
           final int tempPos = position;
            holder.bind(sound, position);

            holder.mBinding.playSoundButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mSelectionMode == DEFAULT_MODE_FLAG ) {

                        if( !mMediaPlayerUtility.isMediaPlayerOn())
                        mSoundBoard.play(sound);
                    }
                    else if(mSelectionMode == COMBINE_MODE_FLAG) {


                        if(mChainedItems.contains(tempPos)){
                            mChainedItems.remove(Integer.valueOf(tempPos));
                            notifyDataSetChanged();
                        } else if (mChainedItems.size() < 8)
                        {
                            mChainedItems.add(tempPos);
                            notifyDataSetChanged();
                        }
                    }
                    else if (mSelectionMode == SHARE_MODE_FLAG) {
                        new ShareSingleFileTask(BeatBoxFragment.this).execute(sound);
                        toggleMode(DEFAULT_MODE_FLAG);

                    }
                }
            });

            holder.mBinding.playSoundButton.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {


                   if(mSelectionMode == DEFAULT_MODE_FLAG) {

                       toggleMode(COMBINE_MODE_FLAG);

                       if(mChainedItems.contains(tempPos)) {
                           mChainedItems.remove(Integer.valueOf(tempPos));
                       } else if(mChainedItems.size() < 8   ) {

                           mChainedItems.add(tempPos);

                       }

                       notifyDataSetChanged();
                       return true;





                   } else return false;

                }
            });



            if(mSelectionMode == COMBINE_MODE_FLAG && mChainedItems.contains(tempPos))
            {

                holder.mBinding.playSoundButton.setBackgroundResource(R.drawable.button_beat_box_selected);
            } else if (mSelectionMode == SHARE_MODE_FLAG){
                holder.mBinding.playSoundButton.setBackgroundResource(R.drawable.beat_box_button_share_mode);
            } else {
                holder.mBinding.playSoundButton.setBackgroundResource(R.drawable.button_beat_box);
            }


        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode == REQUST_NAME)
        {
            if(resultCode == Activity.RESULT_OK){
               SoundChainLab soundChainLab = SoundChainLab.get(mContext);
               String name = (String) data.getSerializableExtra(NamePickerFragment.Extra_Key_Name);

             name = resolveNamingConflict(name);


                SoundChain newChain = new SoundChain(mChainedItems, name);

               int durationMilis = 0;
                try {
                   durationMilis = mMediaPlayerUtility.getChainDuration(mChainedItems, mSounds);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                newChain.setDuratingInMilis(durationMilis);



                WrapperClassForAsyncTask wrapperClassForAsyncTask = new WrapperClassForAsyncTask(newChain);



                new CreateSoundChainFileTask(getActivity().getApplicationContext()).execute(wrapperClassForAsyncTask);

            }


            mChainedItems.clear();
            toggleMode(DEFAULT_MODE_FLAG);





            if(mSoundAdapter != null)
            mSoundAdapter.notifyDataSetChanged();
        }
    }

    private String resolveNamingConflict(String name) {

        SoundChainLab soundChainLab = SoundChainLab.get(getContext());

        SoundChain oldChainWithSameName = soundChainLab.getSoundChain(name);

        int tempNumber = 0;
        String  tempName = name;

        while (oldChainWithSameName != null)
        {
            tempNumber++;
            tempName = name + tempNumber;

            oldChainWithSameName = soundChainLab.getSoundChain(tempName);

        }

        return tempName;

    }

    private class CreateSoundChainFileTask extends AsyncTask<WrapperClassForAsyncTask, Void, Void>{

        SoundChain mSoundChain;
        WeakReference<Context> mContextWeakReference;


        public CreateSoundChainFileTask(Context context) {
            mContextWeakReference = new WeakReference<Context>(context);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



            Context context = mContextWeakReference.get();

            if(context == null) return;

            SoundChainLab soundChainLab = SoundChainLab.get(context);
            soundChainLab.addChain(mSoundChain);

           SavedChainStoragePreferences.saveSoundChainLab(context, soundChainLab);

            Toast.makeText(context, R.string.chain_added_confirm,
                    Toast.LENGTH_LONG).show();

            /*Snackbar.make(mBinding.recyclerView, R.string.chain_added_confirm, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();*/
            if(mNotifyDataSetChangeUpdateInterface != null)
            mNotifyDataSetChangeUpdateInterface.notifyFragment();


        }

        @Override
        protected Void doInBackground(WrapperClassForAsyncTask... wrapperClassForAsyncTasks) {
             mSoundChain = wrapperClassForAsyncTasks[0].getSoundChain();

            SoundFileManagementUtility.createSoundChainFile(mSoundChain, mContext, mSounds);
            return null;
        }
    }

    private class WrapperClassForAsyncTask {

        SoundChain mSoundChain;




        public SoundChain getSoundChain() {
            return mSoundChain;
        }

        public WrapperClassForAsyncTask( SoundChain soundChain) {


            mSoundChain = soundChain;
        }
    }

    public interface NotifyDataSetChangeUpdateInterface {
        public void   notifyFragment();
    }

    public interface GoToSoundLabFragmentCallbackInterface {
        public void startSoundLabFragment();
    }

private static class ShareSingleFileTask extends AsyncTask<Sound, Void, File> {
        WeakReference<Fragment> mFragmentWeakReference;

    public ShareSingleFileTask(Fragment fragmentRef) {
        mFragmentWeakReference = new WeakReference<>(fragmentRef);
    }

    @Override
    protected void onPostExecute(File file) {

        Fragment fragment = mFragmentWeakReference.get();

        if(fragment == null || file == null) return;


        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("audio/mp3");

        Uri uri = FileProvider.getUriForFile(fragment.getContext(), "com.bignerdranch.beatbox.fileprovider", file);
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
        fragment.startActivity(Intent.createChooser(shareIntent, "Share Audio Clip"));

    }

    @Override
    protected File doInBackground(Sound... sounds) {
        Sound sound = sounds[0];

        Fragment fragment = mFragmentWeakReference.get();

        if(fragment == null) return null;


       return SoundFileManagementUtility.cacheSoundAsset(sound, fragment.getContext());

    }
}




}

//TODO Make buttons more responsive in beatbox, Color scheme, make chain playing more apparent
//todo find a way to give the user info on how to use the app interface