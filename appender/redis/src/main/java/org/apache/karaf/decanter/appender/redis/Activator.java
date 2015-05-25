package org.apache.karaf.decanter.appender.redis;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator implements BundleActivator{

    private static final String CONFIG_PID = "org.apache.karaf.decanter.appender.redis";
    RedisAppender redisAppender;
    private ServiceRegistration serviceRegistration;
    private BundleContext bundleContext;

    @Override
    public void start(BundleContext bundleContext) throws Exception {

        Dictionary<String, String> properties = new Hashtable<>();
        properties.put(Constants.SERVICE_PID, CONFIG_PID);
        serviceRegistration = bundleContext.registerService(ManagedService.class.getName(), new ConfigUpdater(bundleContext), properties);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if(redisAppender != null){
            redisAppender.close();
        }
        if(serviceRegistration != null){
            serviceRegistration.unregister();
        }

    }

    private final class ConfigUpdater implements ManagedService {
        private BundleContext bundleContext;
        private ServiceRegistration<?> serviceReg;

        public ConfigUpdater(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }

        @Override
        public void updated(Dictionary config) throws ConfigurationException {
            if (redisAppender != null) {
                redisAppender.close();
                serviceReg.unregister();
            }

            String host = config != null ? (String) config.get("host") : "localhost";
            String map = config != null ? (String) config.get("map") : "Decanter";
            String mode;


            if(config != null) {
                switch ((String) config.get("mode")){
                    case "Master_Slave": mode="Master_Slave";
                        String masterAddress = config != null ? (String) config.get("masterAddress") : "localhost:6379";
                        RedisMasterSlaveAppender.LoadBalancerType loadBalancerType = config != null ? RedisMasterSlaveAppender.LoadBalancerType.valueOf((String) config.get("loadBalancerType")) : RedisMasterSlaveAppender.LoadBalancerType.ROUNDROBIN;
                        redisAppender = new RedisMasterSlaveAppender(host,map,masterAddress,loadBalancerType);
                        break;
                    case "Sentinel": mode="Sentinel";
                        String masterName = config != null ? (String) config.get("masterName") : "myMaster";
                        redisAppender = new RedisSentinelAppender(host,map,masterName);
                        break;
                    case "Cluster": mode="Cluster";
                        int scanInterval = config != null ? (Integer) config.get("scanInterval") : 200;
                        redisAppender = new RedisClusterAppender(host,map,scanInterval);
                        break;
                    default: mode = "Single";
                        redisAppender = new RedisSingleAppender(host,map);
                }
            }


            redisAppender.open();
            Dictionary<String, String> properties = new Hashtable<>();
            properties.put(EventConstants.EVENT_TOPIC, "decanter/*");
            serviceReg = bundleContext.registerService(EventHandler.class, redisAppender, properties);
        }
    }
}
