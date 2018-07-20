package com.ude.debugger.adpter.listener;

import android.view.View;

/**
 * Created by ude on 2017-10-12.
 */

public interface OnItemClickListener {
    void OnClick(View view, int position);
    void OnLongClick(View view, int position);
}
