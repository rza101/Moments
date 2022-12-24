package com.pahat.moments.ui;

import android.view.View;

public interface OnItemClick<T> {
    void onClick(View v, T data);
}
