package org.apache.karaf.decanter.appender.redis;

import org.osgi.service.event.Event;
import org.redisson.Redisson;
import org.redisson.connection.RandomLoadBalancer;
import org.redisson.connection.RoundRobinLoadBalancer;
import java.util.concurrent.ConcurrentMap;


public class RedisMasterSlaveAppender extends RedisAppender {

    String masterAdress;
    //TO-DO Change to enum
    public static enum LoadBalancerType {
        ROUNDROBIN,RANDOM
    }
    LoadBalancerType loadBalancerType;

    public RedisMasterSlaveAppender(String host, String map, String masterAdress, LoadBalancerType loadBalancerType){
        setHost(host);
        setMap(map);
        setMasterAdress(masterAdress);
        this.loadBalancerType = loadBalancerType;
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
        getConfig().useMasterSlaveConnection()
                .setMasterAddress(getMasterAdress())
                .addSlaveAddress(getHost())
                .setLoadBalancer(loadBalancerType.equals(LoadBalancerType.RANDOM) ? new RandomLoadBalancer() : new RoundRobinLoadBalancer());

        setRedisson(Redisson.create(getConfig()));
    }

    @Override
    public void close() {
        if(getRedisson() != null){
            getRedisson().shutdown();
        }
    }

    public String getMasterAdress() {
        return masterAdress;
    }

    public void setMasterAdress(String masterAdress) {
        this.masterAdress = masterAdress;
    }
}
