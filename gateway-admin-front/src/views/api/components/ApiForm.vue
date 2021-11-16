<template>
  <div>
    <!-- 版本提示 -->
    <template v-if="!editable">
      <el-alert
        v-if="form.versionType === 'approving'"
        title="当前查看版本为待审批发布版本" type="warning" :closable="false"/>
      <el-alert
        v-if="form.versionType === 'online'"
        title="当前查看版本为线上发布版本" type="success" :closable="false"/>
      <el-alert
        v-if="form.versionType === 'history'"
        title="当前查看版本为历史发布版本" type="info" :closable="false"/>
      <el-alert
        v-if="form.versionType === 'reject'"
        title="当前查看版本已驳回发布" type="error" :closable="false"/>
    </template>

    <!-- 基本信息相关表单 -->
    <el-divider content-position="left">接口基本信息</el-divider>
    <el-form ref="form" class="form-block" :model="form" label-width="150px" size="small" :rules="rules">
      <el-form-item label="服务名称" prop="apiName">
        <ApiNameInput :value.sync="form.apiName" :disabled="mode !== 'create'" :mode="mode"/>
      </el-form-item>

      <el-divider content-position="left">服务信息</el-divider>
      <el-form-item label="Dubbo接口名称" prop="dubboService">
        <el-input v-model="form.dubboService" :disabled="!editable"
                  placeholder="输入dubbo接口类全限定名称, 如com.didichuxing.framework.HelloRemoteService"/>
      </el-form-item>
      <el-form-item label="Dubbo方法名" prop="dubboMethod">
        <el-input v-model="form.dubboMethod" placeholder="输入dubbo接口方法名, 如sayHello" :disabled="!editable"/>
      </el-form-item>
      <el-form-item label="Dubbo服务版本" prop="dubboVersion">
        <el-input v-model="form.dubboVersion" placeholder="输入dubbo服务发布版本号, 如 1.0.0" :disabled="!editable"/>
      </el-form-item>
    </el-form>

    <!-- 参数列表组件 -->
    <el-divider content-position="left">服务端参数列表</el-divider>
    <el-table :data="getParams()" class="row-element" empty-text="无参数列表" row-key="index"
              :row-class-name="paramContentClass"
              default-expand-all :tree-props="{children: 'params', hasChildren: true}">
      <el-table-column prop="sourceName" label="客户端参数" width="200px" show-overflow-tooltip>
        <template slot-scope="scope">{{scope.row.sourceName}}</template>
      </el-table-column>
      <el-table-column prop="paramType" label="参数类型" width="130px" show-overflow-tooltip>
        <template slot-scope="scope">
          <ApiParamTypeSelector class="full-line" :value.sync="scope.row.paramType" mode="view"/>
        </template>
      </el-table-column>
      <el-table-column prop="destName" label="服务端参数" width="150px" show-overflow-tooltip>
        <template slot-scope="scope">{{scope.row.destName}}</template>
      </el-table-column>
      <el-table-column prop="description" label="参数描述" min-width="300px" show-overflow-tooltip>
        <template slot-scope="scope">{{toParamDescription(scope.row)}}</template>
      </el-table-column>
      <el-table-column>
        <template slot-scope="scope" v-if="editable">
          <el-row>
            <el-link type="primary" @click="showEditParamDialog(scope.row)">编辑</el-link>
            <el-popconfirm :title="'是否要删除参数: ' + scope.row.sourceName" @confirm="removeParam(scope.row)">
              <el-link type="primary" style="margin-left: 10px" slot="reference">删除</el-link>
            </el-popconfirm>
          </el-row>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对象新增/编辑弹窗逻辑 -->
    <div v-if="editable" style="margin-top: 10px">
      <el-button class="full-line" icon="el-icon-folder-add" @click="showAddObjectDialog">新增对象参数</el-button>
      <ApiObjectParamDialog
        v-if="showEditObjectParamDialogFlag"
        :init-param="editObjectParamForm"
        :sys-params.sync="sysParams"
        @save-param="saveParam"
        @dismiss-dialog="dismissDialog"/>
    </div>

    <!-- 参数新增/编辑弹窗逻辑 -->
    <div v-if="editable" style="margin-top: 10px">
      <el-button class="full-line" icon="el-icon-plus" @click="showAddParamDialog">新增参数列表</el-button>
      <ApiParamDialog
        v-if="showEditParamDialogFlag"
        :init-param="editParamForm"
        :sys-params.sync="sysParams"
        @save-param="saveParam"
        @dismiss-dialog="dismissDialog"/>
    </div>
  </div>
</template>

<script>
  import ApiParamTypeSelector from './ApiParamTypeSelector'
  import ApiParamDialog from './ApiParamDialog'
  import ApiObjectParamDialog from './ApiObjectParamDialog'
  import GlobalConfig from '@/common/global'
  import {toParamDescription} from '@/common/param-utils'
  import ApiNameInput from "./ApiNameInput";

  export default {
    name: 'ApiForm',
    components: {
      ApiNameInput,
      ApiObjectParamDialog,
      ApiParamDialog,
      ApiParamTypeSelector
    },
    props: {
      form: {type: Object},
      // 是否可编辑态
      editable: {type: Boolean},
      // 表单模式: 'edit' - 接口编辑模式, 'create' - 接口新建模式
      mode: {type: String, default: 'edit'},
      // 表单校验器，用于触发校验结果, 接受一个callback参数作为成功回调
      validator: {type: Function}
    },
    data: function () {
      return {
        GlobalConfig,
        // 基本信息 + 服务信息表单
        rules: {
          apiName: [
            {required: true, message: '请输入接口名称', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {
              required: true,
              pattern: /^[a-zA-Z0-9.]*[a-zA-Z0-9]$/,
              message: '参数名只支持数字、大小写字母与".", 不可以"."结尾',
              trigger: 'blur'
            },
          ],
          dubboService: [
            {required: true, message: '请输入客户端参数', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {
              required: true, message: '请输入合法的全限定接口名称', trigger: 'blur',
              pattern: /^([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*$/
            }
          ],
          dubboMethod: [
            {required: true, message: '请输入客户端参数', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {required: true, pattern: /^[a-zA-Z0-9]+$/, message: '请输入正确的dubbo方法名称', trigger: 'blur'}
          ]
        },
        // 系统参数定义列表
        sysParams: [],
        // 对话框展示: 参数编辑/新增
        showEditParamDialogFlag: false,
        editParamForm: {},
        // 对话框展示: 对象参数编辑/新增
        showEditObjectParamDialogFlag: false,
        editObjectParamForm: {}
      }
    },
    methods: {
      toParamDescription,
      //----------------------------------------- 编辑页主页相关逻辑 -----------------------------------------
      leaveEdit() {
        this.$emit('leave-edit')
      },
      getParams() {
        // 因为要处理列表的唯一键row-key，所以计算参数列表时设置一个index主键
        if (this.form.params && this.form.params.length > 0) {
          let count = 0
          const attachIndex = next => {
            next.index = count++
            if (next.params && next.params.length > 0) {
              next.params.forEach(it => attachIndex(it))
            }
          }
          attachIndex(this.form)
        }
        return this.form.params
      },
      isMethodParam(item) {
        // 判断参数是否为函数签名参数
        for (let i = 0; i < this.form.params.length; ++i) {
          if (this.form.params[i].index === item.index) {
            return true
          }
        }
        return false
      },
      isSysParam(item) {
        if (!item) {
          return false
        }
        // 判断参数是否为系统参数
        for (let i = 0; i < this.sysParams.length; ++i) {
          if (this.sysParams[i].name === item.sourceName) {
            return true
          }
        }
        return false
      },
      paramContentClass({row}) {
        // 行高亮风格控制
        if (this.isMethodParam(row)) {
          return 'line-method-param'
        } else {
          return 'line-object-field'
        }
      },
      //----------------------------------------- 参数变更相关逻辑 -----------------------------------------
      showAddObjectDialog() {
        // 展示新增对象弹窗
        this.editObjectParamForm = {modify: false}
        this.showEditObjectParamDialogFlag = true
      },
      showAddParamDialog() {
        // 展示新增参数弹窗
        this.editParamForm = {modify: false}
        this.showEditParamDialogFlag = true
      },
      showEditParamDialog(param) {
        // 写入一个编辑标记, 当保存后删除编辑标记
        param.edit = true
        // 展示编辑弹窗
        if (param.paramType === 60) {
          this.editObjectParamForm = {...param, modify: true}
          this.showEditObjectParamDialogFlag = true
        } else {
          this.editParamForm = {...param, modify: true}
          this.showEditParamDialogFlag = true
        }
      },
      dismissDialog() {
        // 销毁变更标记, 关闭弹框
        const recycleEditFlag = next => {
          next.edit = false
          if (next.params && next.params.length > 0) {
            next.params.forEach(it => recycleEditFlag(it))
          }
        }
        recycleEditFlag(this.form)
        this.showEditParamDialogFlag = this.showEditObjectParamDialogFlag = false
      },
      removeParam(param) {
        // 写入一个删除标记, 让后根据删除标记递归扫描删除
        param.delete = true
        const removeIterable = next => {
          if (next.params && next.params.length > 0) {
            next.params = next.params.filter(it => !it.delete)
            next.params.forEach(it => removeIterable(it))
          }
        }
        removeIterable(this.form)
      },
      saveParam(param) {
        // 1. 根据编辑标记变更元素
        const updateIterable = next => {
          if (next.params && next.params.length > 0) {
            for (let i = 0; i < next.params.length; ++i) {
              if (next.params[i].edit) {
                next.params.splice(i, 1, param)
                return true
              } else if (updateIterable(next.params[i])) {
                return true
              }
            }
            return false
          }
        }
        if (!updateIterable(this.form)) {
          this.form.params.push(param)
        }
        // 2. 关闭弹窗
        this.dismissDialog()
      }
    },
    created() {
      // 校验表单方法通过属性传递给上层
      this.$emit('update:validator', callback => {
        this.$refs.form.validate().then(callback).catch(() => this.$message.error('请补全必要的参数!'))
      })
    }
  }
</script>

<style lang="stylus">
  .inline-input
    margin 0
    padding 0
    border 0

  .full-line
    width: 100%

  .el-table .line-method-param
    background #fbffff

  .el-table .line-object-field
    background #cbdcf5
</style>