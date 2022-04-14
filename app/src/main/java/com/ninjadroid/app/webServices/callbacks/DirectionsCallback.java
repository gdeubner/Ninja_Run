package com.ninjadroid.app.webServices.callbacks;

import com.ninjadroid.app.utils.containers.DirectionsContainers.DirectionsContainer;

public interface DirectionsCallback {
    void onSuccess(DirectionsContainer directions);
}

