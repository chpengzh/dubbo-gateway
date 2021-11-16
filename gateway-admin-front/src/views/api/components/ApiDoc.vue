<template>
  <div>
    <el-alert
      v-if="currentVersion.versionType === 'approving'"
      title="当前查看版本为待审批发布版本" type="warning" :closable="false"/>
    <el-alert
      v-if="currentVersion.versionType === 'online'"
      title="当前查看版本为线上发布版本" type="success" :closable="false"/>
    <el-alert
      v-if="currentVersion.versionType === 'history'"
      title="当前查看版本为历史发布版本" type="info" :closable="false"/>
    <el-alert
      v-if="currentVersion.versionType === 'reject'"
      title="当前查看版本已驳回发布" type="error" :closable="false"/>
    <el-divider content-position="left" style="margin-top: 40px">基本信息</el-divider>
    <el-descriptions :column="8" style="margin-top: 30px">
      <el-descriptions-item label="【POST】" :span="8">{{clientUrl || ''}}</el-descriptions-item>
      <el-descriptions-item label="上线状态" :span="4">{{trunk.releaseVersion ? '已上线' : '未上线'}}</el-descriptions-item>
      <el-descriptions-item label="上线版本" :span="4">{{trunk.releaseVersion}}</el-descriptions-item>
      <el-descriptions-item label="上次发布" :span="4">{{formatDate(trunk.gmtModify)}}</el-descriptions-item>
      <el-descriptions-item label="发布人" :span="4">{{trunk.modifyUser}}</el-descriptions-item>
      <el-descriptions-item label="服务描述" :span="8">{{trunk.description}}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left" style="margin-top: 40px">版本信息 {{currentVersion.releaseVersion}}</el-divider>
    <el-descriptions :column="8" style="margin-top: 30px">
      <el-descriptions-item label="审批状态" :span="4">
        {{approveResult(currentVersion['raw.approveResult'])}}
      </el-descriptions-item>
      <el-descriptions-item label="发起审批时间" :span="4">{{formatDate(currentVersion['raw.approveStart'])}}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">请求系统参数</el-divider>
    <el-table :data="currentVersion['client.systemParams']" class="row-element" empty-text="无参数"
              style="margin-top: 30px">
      <el-table-column key="sourceName" label="参数名称" :show-overflow-tooltip="true" width="200px">
        <template slot-scope="scope">{{ scope.row.sourceName }}</template>
      </el-table-column>
      <el-table-column key="paramType" label="参数类型" :show-overflow-tooltip="true" min-width="250px">
        <template slot-scope="scope">
          <ApiParamTypeSelector :value.sync="scope.row.paramType" mode="view"/>
        </template>
      </el-table-column>
      <el-table-column key="status" label="参数描述" :show-overflow-tooltip="true" min-width="500px">
        <template slot-scope="scope">{{ toParamDescription(scope.row)}}</template>
      </el-table-column>
    </el-table>
    <el-divider content-position="left">请求业务参数 Content-Type: application/json; charset=UTF-8</el-divider>
    <el-table :data="currentVersion['client.bizParams']" class="row-element" empty-text="无参数" style="margin-top: 30px">
      <el-table-column key="sourceName" label="参数名称" :show-overflow-tooltip="true" width="200px">
        <template slot-scope="scope">{{ scope.row.sourceName }}</template>
      </el-table-column>
      <el-table-column key="paramType" label="参数类型" :show-overflow-tooltip="true" min-width="250px">
        <template slot-scope="scope">
          <ApiParamTypeSelector :value.sync="scope.row.paramType" mode="view"/>
        </template>
      </el-table-column>
      <el-table-column key="status" label="参数描述" :show-overflow-tooltip="true" min-width="500px">
        <template slot-scope="scope">{{ toParamDescription(scope.row)}}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import {formatDate} from '@/common/date'
  import ApiParamTypeSelector from './ApiParamTypeSelector'
  import {getApiSysParam} from '@/api/api'
  import {toParamDescription} from '@/common/param-utils'

  export default {
    name: 'ApiDoc',
    components: {ApiParamTypeSelector},
    props: {
      // --当前版本--
      currentVersion: {type: Object},
      // --当前线上发布版本--
      trunk: {type: Object}
    },
    data() {
      return {
        // 系统参数定义列表
        sysParams: [],
      }
    },
    methods: {
      toParamDescription,
      formatDate,
      approveResult(result) {
        switch (result) {
          case 0:
            return '审批中'
          case 1:
            return '审批通过'
          case 2:
            return '审批驳回'
          default:
            return ''
        }
      }
    },
    computed: {
      clientUrl() {
        return '/gateway' +
          '?api=' + this.currentVersion.apiName
      },
      clientRequireAppAuth() {
        switch (this.currentVersion.requireAppAuth) {
          case 0:
            return ['需要应用授权', 'warning', '客户端需要传递appKey, 且需要为应用添加接口调用授权']
          case 1:
            return ['无需应用授权', 'danger', '内网应用接口, 无需传递 appKey、sign以及timestamp']
          default:
            return ['loading...', 'info', 'loading...']
        }
      }
    },
    created() {
      this.$emit('init', this.initData)
      getApiSysParam(resp => this.sysParams = resp.data.data)
    }
  }
</script>