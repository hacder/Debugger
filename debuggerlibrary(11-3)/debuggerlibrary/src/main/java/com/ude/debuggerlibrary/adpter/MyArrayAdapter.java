package com.ude.debuggerlibrary.adpter;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by ude on 2017-10-18.
 */

public class MyArrayAdapter extends ArrayAdapter<String>{

    public MyArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }
}
