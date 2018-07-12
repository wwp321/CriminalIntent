package com.byron.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    public static final String DELETE_CRIME = "delete_crime";

    private static final int REQUEST_DATE = 0;
    private Crime mCrime;
    private EditText mTitleFiled;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    private Button mJumpFirst;
    private Button mJumpLast;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //CrimeLab crimeLab = CrimeLab.get(getActivity());
        //crimeLab.setPosition(crimeLab.getPosition(mCrime));
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        UUID crimeId = (UUID) args.getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleFiled = view.findViewById(R.id.crime_title);
        mTitleFiled.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                CrimeLab.get(getActivity()).updateCrime(mCrime);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = view.findViewById(R.id.crime_date);
        updateDate(mCrime.getFormatDate());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        mTitleFiled.setText(mCrime.getTitle());

        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        Button deleteBtn = view.findViewById(R.id.delete_crime);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                Intent intent = new Intent();
                intent.putExtra(DELETE_CRIME, CrimeLab.get(getActivity()).getPosition(mCrime));
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });

        mJumpFirst = view.findViewById(R.id.jump_to_first_btn);
        mJumpFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CrimePageActivity activity = (CrimePageActivity) getActivity();
                    activity.JumpToPosition(0);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        mJumpLast = view.findViewById(R.id.jump_to_last_btn);
        mJumpLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CrimePageActivity activity = (CrimePageActivity) getActivity();
                    activity.JumpToPosition(CrimeLab.get(getActivity()).getCrimeList().size() - 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Log.d(TAG, "onCreateView: ");

        return view;
    }




    @Override
    public void onStart() {
        super.onStart();
        int position = CrimeLab.get(getActivity()).getPosition(mCrime);
        Log.d(TAG, "onStart: " + position);
        if(position == 0){
            mJumpFirst.setEnabled(false);
        }else {
            mJumpFirst.setEnabled(true);
        }

        if(position == CrimeLab.get(getActivity()).getCrimeList().size() - 1){
            mJumpLast.setEnabled(false);
        }else{
            mJumpLast.setEnabled(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(mCrime.getDate().toString());
        }
    }

    private void updateDate(String s) {
        mDateButton.setText(s);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
