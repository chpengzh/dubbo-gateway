package org.apache.dubbo.gateway.server.repository.sync;

import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.gateway.api.constants.EventName;

/**
 * 配置变更事件处理器
 *
 * @author chpengzh@foxmail.com
 * @date 2020/10/8 09:41
 */
public interface ConfigChangedResolver {

    void resolveChange(EventName eventType, String text, JSONObject event) throws Throwable;

}
