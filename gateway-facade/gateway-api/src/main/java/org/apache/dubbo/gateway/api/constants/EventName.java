package org.apache.dubbo.gateway.api.constants;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public enum EventName {

    UNKNOWN("unknown"),

    API_UPGRADE("api_upgrade"),

    API_OFFLINE("api_offline");

    private final String key;

    EventName(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static List<String> keys() {
        return Stream.of(values())
                .map(EventName::getKey)
                .collect(Collectors.toList());
    }

    public static EventName fromKey(String key) {
        for (EventName name : values()) {
            if (Objects.equals(name.getKey(), key)) {
                return name;
            }
        }
        return UNKNOWN;
    }
}
