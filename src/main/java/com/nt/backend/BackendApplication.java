package com.nt.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableConfigurationProperties
public class BackendApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@PostConstruct
	public void logHostIp() throws UnknownHostException {
		LOGGER.info("Host Ip Address: {}", InetAddress.getLocalHost().getHostAddress());
	}
}
