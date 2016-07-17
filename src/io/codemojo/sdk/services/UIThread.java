package io.codemojo.sdk.services;

/**
 * Created by shoaib on 30/06/16.
 */
public abstract class UIThread {

    public boolean moveTo(Runnable runnable) {
        runnable.run();
        return true;
    }
}
