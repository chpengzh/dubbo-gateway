<template>
  <div class="wrapper">
    <el-row>
      <el-col :span="editable ? 22 : 19">
        <h3>
          <el-link class="el-icon-arrow-left" @click="leaveEdit" style="font-size: x-large; padding-bottom: 5px"/>
          {{this.title()}}
        </h3>
        <ApiForm
          :editable="editable"
          :mode="pageMode"
          :form.sync="currentVersion"
          :validator.sync="validator"
          @leave-edit="leaveEdit"
          style="padding-bottom: 60px"/>

        <!-- 当前版本处于审批状态 -->
        <div style="text-align: right" v-if="approveProcess.result === 0">
          <el-form ref="form">
            <el-form-item>
              <el-input type="textarea" v-model="approveProcess.payload" placeholder="试着说点什么..."/>
            </el-form-item>
          </el-form>
          <div style="text-align: right;">
            <el-button type="primary" icon="el-icon-check" @click="approve" v-if="isSystemAdmin">审批通过</el-button>
            <el-button type="danger" icon="el-icon-close" @click="reject">
              {{ isSystemAdmin ?'审批驳回' :'撤销审批'}}
            </el-button>
            <el-button type="warning" icon="el-icon-edit-outline" @click="toLinkedUrl" v-if="approveProcess.url">
              查看关联审批单
            </el-button>
          </div>
        </div>

        <!-- 当前版本为已发布状态或者审批驳回状态 -->
        <div style="text-align: right" v-if="approveProcess.result !== 0">
          <!-- 编辑中 -->
          <template v-if="editable">
            <el-button type="info" icon="el-icon-close" @click="dismissEdit">放弃编辑</el-button>
            <el-button type="primary" icon="el-icon-check" @click="submitEdit">提交发布</el-button>
          </template>
          <!-- 查看历史版本 -->
          <template style="text-align: right" v-if="!editable">
            <el-button type="danger" icon="el-icon-close" @click="offlineApi" v-if="isSystemAdmin">接口下线</el-button>
            <el-button type="info" icon="el-icon-edit-outline" @click="toLinkedUrl" v-if="approveProcess.url">
              查看审批记录
            </el-button>
            <el-button type="info" icon="el-icon-video-play" @click="visitTest">测试上线版本</el-button>
            <el-button type="warning" icon="el-icon-edit" @click="editable = true">使用当前版本编辑</el-button>
          </template>
        </div>
      </el-col>
      <el-col :span="3" v-if="!editable">
        <ApiRefLog
          :api-name.sync="meta.apiName"
          :release-version.sync="meta.releaseVersion"
          :enable-select.sync="enableVersionSelect"
          @version-loaded="versionLoaded"
          @version-picked="selectVersion"/>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import routerParam from '@/common/url-param'
  import ApiForm from './components/ApiForm'
  import ApiRefLog from "./components/ApiRefLog"
  import {offline, submitApi} from '@/api/api'
  import {submitApprove, submitReject} from '@/api/approval'
  import {getToken} from '@/common/auth'
  import {getEnv} from '@/common/env'

  const env = getEnv()

  export default {
    components: {ApiRefLog, ApiForm},
    data() {
      return {
        isSystemAdmin: true,
        // 页面基础信息，
        meta: {
          apiName: '',
          releaseVersion: ''
        },
        // 当前查看版本
        currentVersion: {
          apiName: '',
          dubboService: '',
          dubboMethod: '',
          dubboVersion: '',
          returnType: 0,
          requireLogin: 0,
          authType: 0,
          params: []
        },
        // 当前发布审批流信息
        approveProcess: {},
        // 生产环境发布版本
        trunk: {},
        // 支持编辑态: 当开启时，支持编辑发布；当关闭时，支持历史版本浏览与选择
        editable: false,
        // 表单提交校验器
        validator: function () {
        }
      }
    },
    methods: {
      routerParam,
      versionLoaded(item) {
        this.currentVersion = item.currentVersion
        this.trunk = item.trunk
        this.approveProcess = item.approveProcess
        window.location.href = '/#/api/editor' +
          '?api=' + item.currentVersion.apiName +
          '&releaseVersion=' + item.currentVersion.releaseVersion
      },
      selectVersion(item) {
        this.editable = false
        this.meta = {
          apiName: item.apiName,
          releaseVersion: item.releaseVersion
        }
      },
      doubleCheckExitEdit(callback, dismiss) {
        // 如果当前正在编辑状态，离开时进行二次确认
        if (this.editable) {
          this.$confirm('无法保存未提交发布接口，是否要放弃编辑？', '放弃编辑', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(callback).catch(dismiss)
        } else {
          callback()
        }
      },
      visitTest() {
        window.location.href = '/#/api/testing' +
          '?api=' + this.currentVersion.apiName
      },
      visitNewVersion(apiName) {
        window.location.href = '/#/api/editor' +
          '?api=' + apiName +
          window.location.reload()
      },
      dismissEdit() {
        this.doubleCheckExitEdit(() => {
          switch (this.pageMode) {
            case 'create':
              window.location.href = '/#/api'
              break
            case 'edit':
              this.selectVersion({
                apiName: this.currentVersion.apiName,
                releaseVersion: this.currentVersion.releaseVersion
              })
              break
          }
        })
      },
      leaveEdit() {
        this.doubleCheckExitEdit(() => window.location.href = '/#/api')
      },
      submitEdit() {
        // 提交发布，并不一定真正执行上线操作
        // 1. 如果 #data.result 值为0 标识当前操作要审批后才能上线
        // 2. 如果 #data.result 值为1 标识当前操作已经自动完成审批上线
        this.validator(() => {
          submitApi({
            apiName: this.currentVersion.apiName,
            dubboService: this.currentVersion.dubboService,
            dubboMethod: this.currentVersion.dubboMethod,
            dubboVersion: this.currentVersion.dubboVersion,
            returnType: this.currentVersion.returnType,
            requireLogin: this.currentVersion.requireLogin,
            authType: this.currentVersion.authType,
            params: this.currentVersion.params
          }, resp => {
            if (resp.data.data.result === 0) {
              this.$alert('当前接口发布需经过审批才能完成上线', '发布审批', {
                confirmButtonText: '去审批',
                type: 'warning'
              }).finally(() => {
                this.visitNewVersion(this.currentVersion.apiName)
              })
            } else {
              this.$confirm('接口发布上线成功', '发布成功', {
                confirmButtonText: '确定',
                type: 'success'
              }).finally(() => {
                this.visitNewVersion(this.currentVersion.apiName)
              })
            }
          })
        })
      },
      toLinkedUrl() {
        window.open(this.approveProcess.url, '_blank')
      },
      approve() {
        submitApprove({
          processId: this.currentVersion['raw.approveId'],
          payload: this.approveProcess.payload
        }, () => window.location.reload())
      },
      reject() {
        submitReject({
          processId: this.currentVersion['raw.approveId'],
          payload: this.approveProcess.payload
        }, () => window.location.reload())
      },
      offlineApi() {
        this.$confirm('是否要下线接口 ' + this.meta.apiName + ' (该操作无法撤销)?', '下线接口', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          offline({apiName: this.meta.apiName}, () => {
            this.$message.success('接口 ' + this.meta.apiName + ' 下线成功!')
            window.location.href = '/#/api'
          })
        })
      },
      initEdit() {
        this.selectVersion({
          apiName: routerParam('api'),
          releaseVersion: routerParam('releaseVersion')
        })
      },
      initCreate() {
        this.editable = true
      },
      title() {
        switch (this.pageMode) {
          case 'create':
            return '新增接口'
          case 'edit':
            return '编辑与发布: ' + routerParam('api')
          default:
            return ''
        }
      }
    },
    computed: {
      enableVersionSelect() {
        return !this.editable
      },
      pageMode() {
        const apiName = routerParam('api')
        if (apiName) {
          return 'edit'
        } else {
          return 'create'
        }
      }
    },
    created() {
      switch (this.pageMode) {
        case 'edit':
          this.initEdit()
          break
        case 'create':
          this.initCreate()
          break
      }
    }
  }
</script>

<style scoped lang="stylus">
  .wrapper
    background white
    padding 2% 3% 3%
</style>