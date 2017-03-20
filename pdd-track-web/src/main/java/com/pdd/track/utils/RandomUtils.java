package com.pdd.track.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtils {

    public static String UUID() {
        return UUID.randomUUID().toString();
    }
}
