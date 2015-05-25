package org.apache.karaf.decanter.appender.redis;

import org.osgi.service.event.Event;
import org.redisson.Redisson;

import java.util.concurrent.ConcurrentMap;

public class RedisSingleAppender extends RedisAppender {

    public RedisSingleAppender(String host, String map){
        setHost(host);
        setMap(map);
    }

    @Override
    public void handleEvent(Event event) {
        ConcurrentMap<String, Object> map = getRedisson().getMap(getMap());
        for(String name : event.getPropertyNames()){
            map.put(name, event.getProperty(name));
        }
    }

    @Override
    public void open() {
        getConfig().useSingleServer().setAddress(getHost());

        setRedisson(Redisson.create(getConfig()));
    }

    @Override
    public void close() {
        if(getRedisson() != null){
            getRedisson().shutdown();
        }
    }
}
