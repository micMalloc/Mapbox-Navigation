package com.examlple.android.idontknowthename;

import android.content.Context;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

public class ContextManager {

    private static volatile ContextManager contextManager;
    private static GoogleApiClient client;

    private ContextManager (Context context) {
        client = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    /**
     * SingleTon
     * Synchronization
     * DCL(Double Checking Locking)
     * @return ContextManager Instance
     */
    public static ContextManager getInstance (Context context) {
        if (contextManager == null) {
            synchronized (ContextManager.class) {
                contextManager = new ContextManager(context);
            }
        } return contextManager;
    }


}
