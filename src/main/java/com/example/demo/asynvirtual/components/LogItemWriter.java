package com.example.demo.asynvirtual.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class LogItemWriter implements ItemWriter<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogItemWriter.class);

    @Override
    public void write(Chunk<? extends Integer> chunk) {
        for (var item : chunk) {
            LOGGER.info("Writer item {}", item);
        }
    }
}
