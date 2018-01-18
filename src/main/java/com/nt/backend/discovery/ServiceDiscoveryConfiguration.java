package com.nt.backend.discovery;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("cloud")
@Configuration
@EnableDiscoveryClient(autoRegister = false)
public class ServiceDiscoveryConfiguration {
}
