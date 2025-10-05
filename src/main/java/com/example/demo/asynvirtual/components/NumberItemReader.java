package com.example.demo.asynvirtual.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class NumberItemReader implements ItemReader<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberItemReader.class);

    private static final Integer UPPER_BOUND = 50;
    private int currentIndex = 0;

    @Override
    public Integer read() {
        LOGGER.info("Reading item {}", currentIndex);
        return (currentIndex < UPPER_BOUND) ? currentIndex++ : null;
    }
}
