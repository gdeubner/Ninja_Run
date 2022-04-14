package com.ninjadroid.app.webServices.callbacks;

import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.utils.containers.UserRouteContainer;

public interface SearchRoutesCallback {
    void onSuccess(RouteContainer[] routes);

}
