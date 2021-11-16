package org.apache.dubbo.gateway.server.chain.invoker;

import org.apache.dubbo.gateway.api.constants.ApiParamType;

import java.util.HashMap;
import java.util.Map;

enum ApiParamTypeCache {

    INSTANCE;

    private final Map<Integer, String> cache = new HashMap<>();

    ApiParamTypeCache() {
        ApiParamType[] types = ApiParamType.values();
        for (ApiParamType t : types) {
            cache.put(t.getCode(), t.getClassName());
        }
    }

    public String getClassName(Integer key) {
        return cache.get(key);
    }
}