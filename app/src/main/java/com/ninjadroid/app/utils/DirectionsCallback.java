package com.ninjadroid.app.utils;

import com.ninjadroid.app.utils.containers.DirectionsContainers.DirectionsContainer;

public interface DirectionsCallback {
    void onSuccess(DirectionsContainer directions);
}

