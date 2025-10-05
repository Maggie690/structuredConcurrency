package com.example.demo.asynvirtual.components;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SyncItemProcessor implements ItemProcessor<Integer, Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncItemProcessor.class);

    /**
     *  It simulates a long processing that would take 1 second with Thread.sleep().
     *
     * @param item
     * @return Integer
     * @throws Exception
     */
    @Override
    public Integer process(@NonNull Integer item) throws Exception {
        LOGGER.info("Processing item {}", item);
        Thread.sleep(1000);
        return item;
    }
}
