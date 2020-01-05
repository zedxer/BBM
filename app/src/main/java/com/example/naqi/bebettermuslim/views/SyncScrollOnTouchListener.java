package com.example.naqi.bebettermuslim.views;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

public class SyncScrollOnTouchListener implements View.OnTouchListener {

    private final View syncedView;

    public SyncScrollOnTouchListener(@NonNull View syncedView) {
        this.syncedView = syncedView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        MotionEvent syncEvent = MotionEvent.obtain(motionEvent);
        float width1 = view.getWidth();
        float width2 = syncedView.getWidth();

        //sync motion of two view pagers by simulating a touch event
        //offset by its X position, and scaled by width ratio
        syncEvent.setLocation(syncedView.getX() + motionEvent.getX() * width2 / width1,
                motionEvent.getY());
        syncedView.onTouchEvent(syncEvent);
        return false;
    }
}