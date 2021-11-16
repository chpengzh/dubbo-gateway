package org.apache.dubbo.gateway.server.controller;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.server.repository.ApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RestController
@RequestMapping("/repo")
public class GatewayRepositoryController {

    @Autowired
    private ApiRepository apiRepository;

    @GetMapping("/api")
    public String getApi() {
        return JSON.toJSONString(apiRepository.getAll(), true);
    }

}
