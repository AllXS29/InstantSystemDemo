package com.instantsystem.demo.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DistanceCalculatorTest {

    @Test
    public void distanceKM_shouldSucceed() {
        double distance = DistanceCalculator.distance(46.58595805, 0.35129543, 46.57505318, 0.33712631, "K");
        assertEquals(distance, 1.6256619596830109);
    }

    @Test
    public void distanceKM_positionEquals_shouldReturn0() {
        double distance = DistanceCalculator.distance(46.58595805, 0.35129543, 46.58595805, 0.35129543, "K");
        assertEquals(distance, 0);
    }
}