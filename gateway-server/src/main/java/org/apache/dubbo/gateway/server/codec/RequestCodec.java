package org.apache.dubbo.gateway.server.codec;

import org.springframework.core.Ordered;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 请求解码器
 *
 * @author chpengzh@foxmail.com
 * @date 6/30/21 10:58
 */
public interface RequestCodec extends Ordered {

    /**
     * 是否支持当前类型的请求体
     *
     * @return {@code true} 如果支持当前协议
     */
    boolean supportMedia(@Nonnull HttpServletRequest request);

    /**
     * 请求体解码方法
     *
     * @param request 请求体
     * @return 业务编码
     */
    @Nonnull
    Map<String, Object> decodeRequest(@Nonnull HttpServletRequest request) throws IOException;
}
