package com.drake1804.f1feedler.utils;

import rx.Subscription;

/**
 * Created by Pavel.Shkaran on 6/15/2016.
 */
public class RxUtils {

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
