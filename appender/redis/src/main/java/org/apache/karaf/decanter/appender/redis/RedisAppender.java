package org.apache.karaf.decanter.appender.redis;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.redisson.Config;
import org.redisson.Redisson;

public abstract class RedisAppender implements EventHandler{

    private String host;
    private Redisson redisson;
    private Config config = new Config();
    private String map;


    @Override
    abstract public void handleEvent(Event event);

    abstract public void open();

    abstract public void close();

    public Redisson getRedisson() {
        return redisson;
    }

    public void setRedisson(Redisson redisson) {
        this.redisson = redisson;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Config getConfig() {
        return config;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
