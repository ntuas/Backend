package com.nt.backend.discovery;

import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class AddressServiceDiscovery {

    @Autowired
    private DiscoveryClient discoveryClient;

    public String getAddresses(String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if(instances.isEmpty()) {
            throw new ServiceNotFoundException(serviceId);
        }
        return toAddresses(instances);
    }

    private String toAddresses(List<ServiceInstance> instances) {
        return Joiner.on(",").join(instances.stream()
                .map(this::toAddress)
                .collect(toList()));
    }

    private String toAddress(ServiceInstance instance) {
        return instance.getHost() + ":" + instance.getPort();
    }
}
