package org.apache.dubbo.gateway.admin.controller;

import org.apache.dubbo.gateway.api.model.NotifyEventMessage;
import org.apache.dubbo.gateway.api.service.EventMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/15 15:06
 */
@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventMessageService eventMessageService;

    @PostMapping("/consume")
    public Mono<NotifyEventMessage> consume(@RequestBody Map<String, Long> watchVersions) {
        return Mono.create(sink -> {
            eventMessageService.consume(watchVersions, (msg, err) -> {
                if (err != null) {
                    sink.error(err);
                } else {
                    sink.success(msg);
                }
            });
        });
    }

    @PostMapping("/produce/{eventKey}")
    public void produce(
            @PathVariable("eventKey") String eventKey,
            @RequestBody List<String> eventContent
    ) {
        eventMessageService.produce(eventKey, eventContent);
    }
}
