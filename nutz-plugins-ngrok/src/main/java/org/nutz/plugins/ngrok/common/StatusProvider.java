package org.nutz.plugins.ngrok.common;

public interface StatusProvider<T> {

    T getStatus();
}
