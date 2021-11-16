package org.apache.dubbo.gateway.admin.repository.model;

import lombok.Data;

/**
 * 接口发布元数据
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiTrunkDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 发布版本
     */
    private String releaseVersion;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口版本
     */
    private String apiVersion;

    /**
     * dubbo接口
     */
    private String dubboService;

    /**
     * dubbo方法
     */
    private String dubboMethod;

    /**
     * 负责人团队
     */
    private String owner;

    /**
     * 上次变更发布用户
     */
    private String modifyUser;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 创建时间
     */
    private String gmtCreate;

    /**
     * 修改时间
     */
    private String gmtModify;

}
