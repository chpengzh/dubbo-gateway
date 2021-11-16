package org.apache.dubbo.gateway.admin.controller;

import org.apache.dubbo.gateway.admin.controller.model.ApprovalReqVO;
import org.apache.dubbo.gateway.admin.controller.model.RpcResult;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessService;
import org.apache.dubbo.gateway.admin.service.model.ApproveActionBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessQueryBO;
import com.google.common.collect.Lists;
import org.apache.dubbo.gateway.admin.utils.RpcResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RestController
@RequestMapping("/management/approval")
public class ApproveProcessController {

    @Autowired
    private ApproveProcessService approveProcessService;

    /**
     * 审批通过
     */
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public RpcResult<ApproveProcessBO> approve(@RequestBody ApprovalReqVO request) {
        ApproveActionBO action = new ApproveActionBO();
        BeanUtils.copyProperties(request, action);
        ApproveProcessBO result = approveProcessService.approve(action);
        return RpcResultUtil.createSuccessResult(result);
    }

    /**
     * 审批回绝
     */
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public RpcResult<ApproveProcessBO> reject(@RequestBody ApprovalReqVO request) {
        ApproveActionBO action = new ApproveActionBO();
        BeanUtils.copyProperties(request, action);
        ApproveProcessBO result = approveProcessService.reject(action);
        return RpcResultUtil.createSuccessResult(result);
    }

    /**
     * 查询审批流
     */
    @RequestMapping(method = RequestMethod.GET)
    public RpcResult<List<ApproveProcessBO>> queryApproveProcess(
            @RequestParam(value = "approveId", defaultValue = "") String approveId,
            @RequestParam(value = "type", defaultValue = "-1") int approveType,
            @RequestParam(value = "result", defaultValue = "-1") int approveResult,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize
    ) {
        ApproveProcessQueryBO condition = new ApproveProcessQueryBO();
        if (!StringUtils.isEmpty(approveId)) {
            condition.setApproveIds(Lists.newArrayList(approveId));
        }
        if (approveType >= 0) {
            condition.setType(approveType);
        }
        if (approveResult >= 0) {
            condition.setResult(approveResult);
        }
        condition.setOffset((page - 1) * pageSize);
        condition.setLimit(pageSize);
        List<ApproveProcessBO> result = approveProcessService.query(condition);
        return RpcResultUtil.createSuccessResult(result);
    }
}
