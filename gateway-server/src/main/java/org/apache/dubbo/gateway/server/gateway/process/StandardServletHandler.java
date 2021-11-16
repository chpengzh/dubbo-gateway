package org.apache.dubbo.gateway.server.gateway.process;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;
import org.apache.dubbo.gateway.server.codec.RequestCodec;
import org.apache.dubbo.gateway.server.gateway.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 请求/响应流程解析器
 *
 * @author chpengzh@foxmail.com
 * @date 2020/9/27 20:46
 */
@Service
public class StandardServletHandler implements ServletHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletHandler.class);

    private final List<RequestCodec> requestCodecs;

    public StandardServletHandler(List<RequestCodec> requestCodecs) {
        this.requestCodecs = loadCodec(requestCodecs);
    }

    @Nonnull
    @Override
    public ApiContext readRequest(@Nonnull HttpServletRequest req) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        RequestCodec codec = getCodec(req);
        ApiContext context = new ApiContext();
        context.putBizParams(codec.decodeRequest(req));
        return context;
    }

    @Override
    public Void writeResult(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nullable ApiContext context,
            @Nonnull ApiResult result
    ) throws IOException {
        handleCrossDomain(response);
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            String raw = JSON.toJSONString(result);
            writer.print(raw);
        }
        return null;
    }

    @Override
    public void handleCrossDomain(@Nonnull HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }

    private List<RequestCodec> loadCodec(List<RequestCodec> requestCodecs) {
        List<RequestCodec> result = new ArrayList<>(requestCodecs);
        result.sort(Comparator.comparing(Ordered::getOrder));
        for (RequestCodec requestCodec : result) {
            LOGGER.info("加载codec:{}, order={}",
                    AopUtils.getTargetClass(requestCodec).getSimpleName(),
                    requestCodec.getOrder());
        }
        return result;
    }

    private RequestCodec getCodec(HttpServletRequest req) throws IOException {
        for (RequestCodec codec : requestCodecs) {
            if (codec.supportMedia(req)) {
                return codec;
            }
        }
        throw new IOException("非法的入参协议, Content-Type=" + req.getContentType());
    }
}
