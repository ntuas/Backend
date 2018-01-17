package com.nt.backend.messaging;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
@Getter
public class Receiver {

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(byte[] message) {
        log.info("Received <" + new String(message) + ">");
        latch.countDown();
    }
}
