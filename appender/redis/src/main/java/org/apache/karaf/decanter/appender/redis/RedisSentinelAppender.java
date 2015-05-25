package org.apache.karaf.decanter.appender.redis;

import org.osgi.service.event.Event;
import org.redisson.Redisson;
import java.util.concurrent.ConcurrentMap;

public class RedisSentinelAppender extends RedisAppender {

    String masterName;

    public RedisSentinelAppender(String host,String map, String masterName){
        setHost(host);
        setMap(map);
        setMasterName(masterName);
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
        getConfig().useSentinelConnection()
                .setMasterName(getMasterName())
                .addSentinelAddress(getHost());

        setRedisson(Redisson.create(getConfig()));
    }

    @Override
    public void close() {
        if(getRedisson() != null){
            getRedisson().shutdown();
        }
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }
}
