package com.firstaidkit.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class CiGateProbeTest {
    @Test
    void failsOnPurpose() {
        fail("temporary probe: CI must block the image build");
    }
}
