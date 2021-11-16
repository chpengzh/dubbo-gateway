package org.apache.dubbo.gateway.client.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.*;
import org.apache.dubbo.gateway.api.model.NotifyEventMessage;
import org.apache.dubbo.gateway.api.service.EventMessageService;
import org.apache.dubbo.gateway.client.utils.InnerJsonUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 事件消息客户端
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/15 20:57
 */
public class EventMessageClient implements EventMessageService {

    private final OkHttpClient produceClient;

    private final OkHttpClient consumeClient;

    private final String host;

    public EventMessageClient(String host) {
        this.host = host;
        produceClient = new OkHttpClient.Builder()
                .connectTimeout(500L, TimeUnit.MILLISECONDS)
                .readTimeout(2L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
        consumeClient = new OkHttpClient.Builder()
                .connectTimeout(500L, TimeUnit.MILLISECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    @Nonnull
    @Override
    public Long produce(@Nonnull String eventKey, @Nonnull List<String> eventContent) {
        try {
            String bodyString = InnerJsonUtils.toJson(eventContent);
            Call call = produceClient.newCall(new Request.Builder()
                    .url(String.format("%s/event/produce/%s", host, eventKey))
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json"), bodyString))
                    .build());
            Response response = call.execute();
            if (response.body() == null) {
                return -1L;
            }
            String respBody = response.body().string();
            return Long.parseLong(respBody);
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public NotifyEventMessage consumeSync(@Nonnull Map<String, Long> watchEvents) throws IOException {
        String bodyString = InnerJsonUtils.toJson(watchEvents);
        Call call = consumeClient.newCall(new Request.Builder()
                .url(String.format("%s/event/consume", host))
                .header("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), bodyString))
                .build());

        String respBody = "<none>";
        try {
            Response response = call.execute();
            if (response.body() == null) {
                return null;
            }
            respBody = response.body().string();
            if (StringUtils.isEmpty(respBody)) {
                return null;
            }
            return InnerJsonUtils.fromJson(respBody, NotifyEventMessage.class);
        } catch (JsonProcessingException err) {
            throw new IOException("Illegal response=" + respBody);
        } catch (SocketTimeoutException err) {
            return null;
        }
    }
}
