package org.apache.karaf.decanter.collector.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.Test;

public class TestMapAttribute {

    @Test
    public void testOperatingSystemMBean() throws MalformedObjectNameException, Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        JmxCollector jmxCollector = new JmxCollector(null);
        Map<String, Object> data = jmxCollector.harvestBean(server, new ObjectName("java.lang:type=OperatingSystem"));
        Assert.assertTrue(data.size() >= 15);
        Object freeMem = data.get("FreePhysicalMemorySize");
        Assert.assertTrue(freeMem != null);
        Assert.assertTrue(freeMem instanceof Long);
        Assert.assertTrue((Long)freeMem > 10000);
        System.out.println(data);
    }
}
