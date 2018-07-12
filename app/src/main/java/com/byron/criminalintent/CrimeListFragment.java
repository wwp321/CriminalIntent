package com.byron.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private static final String TAG = "CrimeListFragment";
    private static final int START_CRIMEPAGE_CODE = 1;
    RecyclerView mRecyclerView;
    CrimeLab mCrimeLab;
    CrimeAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        Log.d(TAG, "onCreateView: ");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                int size = mCrimeLab.getCrimeList().size();
                Log.d(TAG, "onOptionsItemSelected: size=" + size);
                mCrimeLab.setPosition(size - 1);
                mAdapter.notifyItemInserted(size - 1);
                Intent intent = CrimePageActivity.newIntent(getActivity(), crime.getId());
                startActivityForResult(intent, START_CRIMEPAGE_CODE);
                return true;
            case R.id.show_subtitle:
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case START_CRIMEPAGE_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    int position = data.getIntExtra(CrimeFragment.DELETE_CRIME, -1);
                    if(position != -1) {
                        mAdapter.notifyItemRemoved(position);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        mCrimeLab = CrimeLab.get(getActivity());
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(mCrimeLab.getCrimeList());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "updateUI: position=" + getCrimePosition());
//            mAdapter.notifyItemChanged(getCrimePosition());
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();

    }

    private int getCrimePosition() {
        return CrimeLab.get(getActivity()).getPosition();
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimeList().size();

//        String subtitle = getString(R.string.subtitle_format, crimeCount);

        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mCrimeTitle;
        private TextView mCrimeDate;
        public ImageView mImageView;
        Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);

            mCrimeTitle = itemView.findViewById(R.id.crime_title);
            mCrimeDate = itemView.findViewById(R.id.crime_date);
            mImageView = itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mCrimeDate.setText(crime.getFormatDate());
            mCrimeTitle.setText(crime.getTitle());
        }

        @Override
        public void onClick(View v) {
            //Intent intent = CrimeActivity.newIntent(getContext(), mCrime.getId());
            Intent intent = CrimePageActivity.newIntent(getContext(), mCrime.getId());
            Log.d(TAG, "onClick: position=" + getAdapterPosition());
            CrimeLab.get(getActivity()).setPosition(getAdapterPosition());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        List<Crime> mCrimes;
        CrimeHolder mCrimeHolder;

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = R.layout.list_item_crime;

            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId,
                    parent, false);
            CrimeHolder holder = new CrimeHolder(view);
            if (0 == viewType) {
                holder.mImageView.setVisibility(View.INVISIBLE);
            }
            mCrimeHolder = holder;
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            if (crime.isRequiresPolice()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
