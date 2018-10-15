package com.examlple.android.idontknowthename;

public class ContextManager {

    private static volatile ContextManager contextManager;

    /**
     * SingleTon
     * Synchronization
     * DCL(Double Checking Locking)
     * @return ContextManager Instance
     */
    public static ContextManager getInstance () {
        if (contextManager == null) {
            synchronized (ContextManager.class) {
                contextManager = new ContextManager();
            }
        } return contextManager;
    }


}
