package com.bignerdranch.beatbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Tasin Ishmam on 6/6/2018.
 */
public class BeatBoxPagerActivity extends AppCompatActivity implements BeatBoxFragment.NotifyDataSetChangeUpdateInterface, BeatBoxFragment.GoToSoundLabFragmentCallbackInterface, SoundLabFragment.GoToBeatBoxFragmentCallbackInterface{


ViewPager mViewPager;
BeatBoxFragment mBeatBoxFragmentRef;
SoundLabFragment mSoundLabFragmentRef;
CircleIndicator mCircleIndicator;
    MediaPlayerUtility mMediaPlayerUtility;



    public void setSoundLabFragmentRef(SoundLabFragment soundLabFragmentRef) {
        mSoundLabFragmentRef = soundLabFragmentRef;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beat_box_pager);


        mViewPager = (ViewPager) findViewById(R.id.beat_box_view_pager);
        mViewPager.setOffscreenPageLimit(2);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.indicator);




        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
               if(position == 0)
               {
                   mBeatBoxFragmentRef = BeatBoxFragment.newInstance();
                   return mBeatBoxFragmentRef;
               }
               else {

                   mSoundLabFragmentRef = SoundLabFragment.newInstance();
                   return mSoundLabFragmentRef;
               }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1) {
                    notifyFragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCircleIndicator.setViewPager(mViewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        mMediaPlayerUtility = MediaPlayerUtility.get(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayerUtility.release();
    }

    @Override
    public void notifyFragment() {


        if(mSoundLabFragmentRef != null){

          mSoundLabFragmentRef.notifyDataChange();
      }

    }

    @Override
    public void startSoundLabFragment() {
        if(mViewPager != null)
            mViewPager.setCurrentItem(1);
    }

    @Override
    public void startBeatBoxFragment() {
        if(mViewPager != null)
            mViewPager.setCurrentItem(0);
    }
}
