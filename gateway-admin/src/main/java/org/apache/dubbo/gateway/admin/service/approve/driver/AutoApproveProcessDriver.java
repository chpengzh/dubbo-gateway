package org.apache.dubbo.gateway.admin.service.approve.driver;

import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessDriver;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessService;
import org.apache.dubbo.gateway.admin.service.model.ApproveActionBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 支持自动审批的审批流
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service
public class AutoApproveProcessDriver implements ApproveProcessDriver<Serializable> {

    @Override
    public void doSubmit(
            @Nonnull ApproveProcessService processService,
            @Nonnull ApproveProcessBO process
    ) {
        ApproveActionBO action = new ApproveActionBO();
        action.setProcessId(process.getApproveId());
        action.setUsername("system_auto");
        action.setPayload("系统自动审批");
        processService.approve(action);
    }
}
