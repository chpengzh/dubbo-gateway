package org.apache.dubbo.gateway.server.codec.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.gateway.server.codec.RequestCodec;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service
public class ApplicationJsonRequestCodec implements RequestCodec {

    @Override
    public boolean supportMedia(@Nonnull HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.CONTENT_TYPE)).orElse("")
                .startsWith("application/json");
    }

    @Nonnull
    @Override
    public Map<String, Object> decodeRequest(@Nonnull HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try (InputStream input = request.getInputStream()) {
            String requestBody = IOUtils.toString(input, StandardCharsets.UTF_8.name());
            result.putAll(JSON.parseObject(requestBody));
        }
        result.putAll(request.getParameterMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue()[0], (a, b) -> a)));
        return result;
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
