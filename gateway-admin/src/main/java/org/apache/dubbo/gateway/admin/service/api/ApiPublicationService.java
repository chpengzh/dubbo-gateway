package org.apache.dubbo.gateway.admin.service.api;

import org.apache.dubbo.gateway.admin.service.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 工作区接口数据工作流
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApiPublicationService {

    //----------------------------------------- 变更类方法 -------------------------------------------------

    /**
     * 提交一个接口定义.
     * <ul>
     * <li>对于线下、预发环境，接口定义会直接发布</ii>
     * <li>对于生产环境环境，接口定义将会进入到审批流</li>
     * </ul>
     *
     * @param apiArtifact 接口发布版本定义
     */
    @Nonnull
    ApproveProcessBO submitVersion(@Nonnull ApiArtifactBO apiArtifact);

    /**
     * 将接口版本推送至生产数据.
     *
     * @param apiArtifact 接口发布版本定义
     */
    void releaseVersion(@Nonnull ApiArtifactBO apiArtifact);

    /**
     * 废弃某发布接口版本数据.
     *
     * @param apiArtifact 接口发布版本定义
     */
    void rejectVersion(@Nonnull ApiArtifactBO apiArtifact);

    /**
     * 接口下线
     *
     * @param apiName    接口名称
     */
    void offline(@Nonnull String apiName);

    //----------------------------------------- 查询类方法 -------------------------------------------------

    /**
     * 模糊匹配接口信息
     *
     * @param keyword 关键字
     * @param offset  查询偏移
     * @param limit   查询数量
     * @return 匹配结果
     */
    @Nonnull
    List<ApiTrunkBO> queryTrunkByKeyword(@Nonnull String keyword, int offset, int limit);

    /**
     * 精确查询结果
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Nonnull
    List<ApiTrunkBO> queryTrunkByCondition(@Nonnull ApiTrunkQueryBO query);

    /**
     * 查询当前生产数据版本接口
     *
     * @param apiUniqKey 接口唯一标识
     * @return {@code null} 如果接口从未发布
     */
    @Nullable
    ApiArtifactBO queryTrunkVersion(@Nonnull ApiUniqKey apiUniqKey);

    /**
     * 查询接口下历史版本
     *
     * @param query 查询条件
     * @return 满足条件的历史版本
     */
    @Nonnull
    List<ApiArtifactBO> queryVersions(@Nonnull ApiArtifactQueryBO query);

}
