package com.digitalxc.secretsanta.util;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {

    private RandomUtils() {
    }

    public static <T> void shuffle(List<T> list) {
        Collections.shuffle(list, ThreadLocalRandom.current());
    }
}
