package com.byron.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePageActivity extends AppCompatActivity {
    private static final String TAG = "CrimePageActivity";
    private static final String EXTRA_CRIME_ID = "com.byron.criminalintent.crime_id";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
//    private Button mJumpToFirst;
//    private Button mJumpToLast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_page);

        mViewPager = findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimeList();

//        mJumpToFirst = findViewById(R.id.jump_to_first_btn);
//        mJumpToLast = findViewById(R.id.jump_to_last_btn);

//        OnButtonClick listener = new OnButtonClick();
//        mJumpToFirst.setOnClickListener(listener);
//        mJumpToLast.setOnClickListener(listener);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "getItem: position=" + position);

                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.setCurrentItem(CrimeLab.get(this).getPosition());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d(TAG, "onPageScrolled: position=" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position:" + position);

                CrimeLab lab = CrimeLab.get(mViewPager.getContext());
                lab.setPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePageActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

//    private class OnButtonClick implements ViewPager.OnClickListener{
//
//        @Override
//        public void onClick(View v) {
//            if(v.getId() == R.id.jump_to_first_btn) {
//                mViewPager.setCurrentItem(0);
//            } else if(v.getId() == R.id.jump_to_last_btn){
//                mViewPager.setCurrentItem(mCrimes.size() - 1);
//            }
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        setResult(RESULT_OK);
    }

    public void JumpToPosition(int position){
        mViewPager.setCurrentItem(position);
    }
}
