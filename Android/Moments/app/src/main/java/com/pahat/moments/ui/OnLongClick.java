package com.pahat.moments.ui;

import android.view.View;

public interface OnLongClick<T> {
    boolean onLongClick(View v, T data);
}
