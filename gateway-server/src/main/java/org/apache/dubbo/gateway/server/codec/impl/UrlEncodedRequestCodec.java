package org.apache.dubbo.gateway.server.codec.impl;

import org.apache.dubbo.gateway.server.codec.RequestCodec;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * URLEncoded 协议传参
 * <ul>
 * <li>支持GET协议: 通过url形式进行参数传递</li>
 * <li>支持POST协议: 通过Content-Type=application/x-www-form-urlencoded形式进行传参</li>
 * </ul>
 *
 * @author chpengzh@foxmail.com
 * @date 6/30/21 12:01
 */
@Service
public class UrlEncodedRequestCodec implements RequestCodec {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean supportMedia(@Nonnull HttpServletRequest request) {
        return isGet(request) || isPostFormBody(request);
    }

    @Nonnull
    @Override
    public Map<String, Object> decodeRequest(@Nonnull HttpServletRequest request) throws IOException {
        return new HashMap<>(request.getParameterMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue()[0], (a, b) -> a)));
    }

    private boolean isGet(@Nonnull HttpServletRequest request) {
        return Objects.equals(request.getMethod().toUpperCase(), HttpMethod.GET.name().toUpperCase());
    }

    private boolean isPostFormBody(@Nonnull HttpServletRequest request) {
        return Objects.equals(request.getMethod().toUpperCase(), HttpMethod.POST.name().toUpperCase())
                && request.getContentType() != null
                && request.getContentType().startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }
}
