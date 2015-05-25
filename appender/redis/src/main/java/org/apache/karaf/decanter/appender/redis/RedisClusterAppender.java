package org.apache.karaf.decanter.appender.redis;

import org.osgi.service.event.Event;
import org.redisson.Redisson;
import java.util.concurrent.ConcurrentMap;

public class RedisClusterAppender extends RedisAppender{

    private int scanInterval;

    public RedisClusterAppender(String host, String map,int scanInterval){
        setHost(host);
        setScanInterval(scanInterval);
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
        getConfig().useClusterServers()
                .setScanInterval(getScanInterval()) // sets cluster state scan interval
                .addNodeAddress(getHost());

        setRedisson(Redisson.create(getConfig()));
    }

    @Override
    public void close() {
        if(getRedisson() != null){
            getRedisson().shutdown();
        }
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }
}
