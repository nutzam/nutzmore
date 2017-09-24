package org.nutz.integration.jedisque.pubsub;

public interface PubSub {

    void onMessage(String channel, String message);
}
