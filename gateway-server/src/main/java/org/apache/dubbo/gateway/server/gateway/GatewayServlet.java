package org.apache.dubbo.gateway.server.gateway;

import org.apache.dubbo.gateway.server.chain.ApiPipelineHandler;
import org.apache.dubbo.gateway.server.chain.exception.ApiHandlerException;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 网关的请求处理器
 *
 * @author chpengzh@foxmail.com
 */
@Component
public class GatewayServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayServlet.class);

    @Autowired
    private ServletHandler servletHandler;

    @Autowired
    private ApiPipelineHandler apiHandleProcess;

    /**
     * 执行业务请求响应
     *
     * @param req  请求对象
     * @param resp 响应对象
     * @throws ApiHandlerException 业务异常
     */
    private Void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AsyncContext async = req.startAsync();
        try {
            ApiContext context = servletHandler.readRequest(req);
            return apiHandleProcess.handle(context, result -> {
                try {
                    return servletHandler.writeResult(req, resp, context, result);
                } finally {
                    async.complete();
                }
            });
        } catch (Throwable e) {
            LOGGER.error("Unexpected error", e);
            try {
                return servletHandler.writeResult(req, resp, null, ApiResults.get(e));
            } finally {
                async.complete();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        servletHandler.handleCrossDomain(resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        servletHandler.handleCrossDomain(resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        servletHandler.handleCrossDomain(resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        servletHandler.handleCrossDomain(resp);
    }

    @Override
    protected void doTrace(HttpServletRequest arg0, HttpServletResponse resp) {
        servletHandler.handleCrossDomain(resp);
    }
}
