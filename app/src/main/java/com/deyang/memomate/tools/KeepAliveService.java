package com.deyang.memomate.tools;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 *  Empty Service binder used by Custom Tabs Intent to bind the calling application session.
 */
public class KeepAliveService extends Service {

    private static final Binder sBinder = new Binder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }
}
