package com.bignerdranch.beatbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.beatbox.databinding.FragmentSoundLabBinding;
import com.bignerdranch.beatbox.databinding.ListItemChainSoundBinding;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Tasin Ishmam on 6/6/2018.
 */
public class SoundLabFragment extends Fragment implements MediaPlayerUtility.notifyTrackFinished {

    MediaPlayerUtility mMediaPlayerUtility;
    List<SoundChain> mSoundChains;
    FragmentSoundLabBinding mBinding;
    SoundChainAdapter mSoundChainAdapter;

    public static int REQUEST_DELETE_CONFIRMATION = 0;

    public GoToBeatBoxFragmentCallbackInterface mGoToBeatBoxFragmentCallbackInterface;

    int currentlyPlaying = -1;

    public interface GoToBeatBoxFragmentCallbackInterface {
        public void startBeatBoxFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_goto_beat_box_fragment) {
            if(mGoToBeatBoxFragmentCallbackInterface != null)
                mGoToBeatBoxFragmentCallbackInterface.startBeatBoxFragment();
            return  true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_sound_lab, menu);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Sounds");



    }

    public static SoundLabFragment newInstance() {return new SoundLabFragment(); }

    @Override
    public void onDetach() {
        super.onDetach();

        mGoToBeatBoxFragmentCallbackInterface = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sound_lab, container, false);


         mMediaPlayerUtility = MediaPlayerUtility.get(getContext());
         mMediaPlayerUtility.initializeCallback(this);
         mSoundChains = SoundChainLab.get(getContext()).getSoundChains();

         mBinding.recyclerViewSoundChainLab.setLayoutManager(new LinearLayoutManager(getActivity()));
         mSoundChainAdapter = new SoundChainAdapter(mSoundChains);
         mBinding.recyclerViewSoundChainLab.setAdapter(mSoundChainAdapter);

        toggleEmptyMessageVisibility();

        return mBinding.getRoot();
    }

    @Override
    public void notifyFinish() {
        currentlyPlaying = -1;
        notifyDataChange();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof GoToBeatBoxFragmentCallbackInterface){
            mGoToBeatBoxFragmentCallbackInterface = (GoToBeatBoxFragmentCallbackInterface) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement GoToBeatBoxFragmentCallbackInterface");
        }

        ((BeatBoxPagerActivity)getActivity()).setSoundLabFragmentRef(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     if(requestCode == REQUEST_DELETE_CONFIRMATION) {
         if(resultCode == Activity.RESULT_OK) {

             int pos = data.getIntExtra(ChainDeleteConfirmFragment.EXTRA_KEY_POS, -1);
             String name = data.getStringExtra(ChainDeleteConfirmFragment.Extra_Key_STRING);

             SoundChain soundChain = SoundChainLab.get(getContext()).getSoundChain(name);
             File file = soundChain.getFile(getContext());

             if(file.exists())
                 file.delete();

             if(pos != -1) {
                 mSoundChainAdapter.soundChains.remove(pos);
                 mSoundChainAdapter.notifyItemRemoved(pos);
             }



           SavedChainStoragePreferences.saveSoundChainLab(getContext(), SoundChainLab.get(getContext()));

             toggleEmptyMessageVisibility();


             soundChain = null;
         }
     }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSoundChainAdapter.notifyDataSetChanged();
        toggleEmptyMessageVisibility();

    }

    public void notifyDataChange() {
        if(mSoundChainAdapter != null)
        {
            mSoundChains = SoundChainLab.get(getContext()).getSoundChains();
            mSoundChainAdapter.notifyDataSetChanged();


            Log.i("TAG", "  CALLED NOTIFY DATA CHANGE INSIDE FRAGMENT");


        }

        toggleEmptyMessageVisibility();
    }

    public void toggleEmptyMessageVisibility() {

        if(mBinding == null) return;


        if(mSoundChains.isEmpty()) {
            mBinding.emptyMessageTextView.setVisibility(View.VISIBLE);
        } else
        {
            mBinding.emptyMessageTextView.setVisibility(View.GONE);
        }

    }



    public class SoundChainHolder extends RecyclerView.ViewHolder   {

        ListItemChainSoundBinding mBinding;
        SoundChain mSoundChain;

        public SoundChainHolder(ListItemChainSoundBinding binding ){
            super(binding.getRoot());

            mBinding = binding;




            mBinding.deleteChain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    FragmentManager manager = getFragmentManager();
                    ChainDeleteConfirmFragment dialog = ChainDeleteConfirmFragment.newInstance(getAdapterPosition(), mSoundChainAdapter.soundChains.get(getAdapterPosition()).getName());


                    dialog.setTargetFragment(SoundLabFragment.this, REQUEST_DELETE_CONFIRMATION);
                    dialog.show(manager,"TAG" );


getContext();

                }
            });

            mBinding.shareChain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File outFile = mSoundChain.getFile(getContext());


                    final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("audio/mp3");

                    Uri uri = FileProvider.getUriForFile(getContext(), "com.bignerdranch.beatbox.fileprovider", outFile);
                    shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(shareIntent, "Share Audio Clip"));
                }
            });

        }

        public void bind(SoundChain soundChain,int pos) {

          mSoundChain = soundChain;
          mBinding.chainName.setText(mSoundChain.getName());
          mBinding.chainDuration.setText(mSoundChain.getDurationString());





        }







    }

    private class SoundChainAdapter extends RecyclerView.Adapter<SoundLabFragment.SoundChainHolder> {

        public List<SoundChain> soundChains;

        public SoundChainAdapter(List<SoundChain> soundChains) {
            this.soundChains = soundChains;

        }


        @Override
        public SoundLabFragment.SoundChainHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            ListItemChainSoundBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_chain_sound, parent, false);

            return new SoundLabFragment.SoundChainHolder(binding);
        }

        @Override
        public void onBindViewHolder(final SoundChainHolder holder, final int position) {

         final SoundChain soundChain = soundChains.get(position);

            holder.bind(soundChain,position);


            holder.mBinding.playChain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!mMediaPlayerUtility.isMediaPlayerOn()) {
                        try {
                            currentlyPlaying = position;

                           notifyDataSetChanged();



                            mMediaPlayerUtility.playChain(soundChain);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(position == currentlyPlaying) {
                        mMediaPlayerUtility.stopPlaying();
                    }
                }
            });



            if(currentlyPlaying == position)
         {
             holder.mBinding.playChain.setImageResource(R.drawable.ic_pause_icon);
         }
         else
            {
                holder.mBinding.playChain.setImageResource(R.drawable.ic_play_icon);
            }

        }




        @Override
        public int getItemCount() {
            return mSoundChains.size();
        }
    }






}
