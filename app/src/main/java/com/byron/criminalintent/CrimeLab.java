package com.byron.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.byron.criminalintent.database.CrimeBaseHelper;
import com.byron.criminalintent.database.CrimeCursorWrapper;
import com.byron.criminalintent.database.CrimeDbSchema;
import com.byron.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private int mPosition;
//    private List<Crime> mCrimeList;
//    private Map<UUID,Crime> mCrimeMap = new ArrayMap<>();
    private Context mContext;
    private SQLiteDatabase mDatabase;
    List<Crime> mCrimeList = new ArrayList<>();

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
//        mCrimeList = new ArrayList<>();
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public List<Crime> getCrimeList() {
//        return mCrimeList;

        mCrimeList.clear();
        CrimeCursorWrapper wrapper = queryCrimes(null, null);

        try {
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()) {
                mCrimeList.add(wrapper.getCrime());
                wrapper.moveToNext();
            }
        }finally {
            wrapper.close();
        }
        return mCrimeList;
    }

    public Crime getCrime(UUID uuid) {
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[]{uuid.toString()});
        Crime crime = null;
        try {
            if(cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            crime = cursor.getCrime();
        }finally {
            cursor.close();
        }

        return crime;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition(Crime crime) {
        getCrimeList();

        for (int i = 0; i < mCrimeList.size() ; i++) {
            if(mCrimeList.get(i).getId().equals(crime.getId())){
                return i;
            }
        }

        return -1;
    }

    public void addCrime(Crime crime) {
//        mCrimeList.add(crime);
//        mCrimeMap.put(crime.getId(), crime);
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
        mCrimeList.add(crime);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());

        return values;
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME,values, CrimeTable.Cols.UUID + " = ?",new String[]{uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);

        return new CrimeCursorWrapper(cursor);
    }

    public void deleteCrime(Crime crime) {
        deleteCrime(crime.getId());
    }

    public void deleteCrime(UUID id) {
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "  = ?", new String[]{id.toString()});

    }
}
