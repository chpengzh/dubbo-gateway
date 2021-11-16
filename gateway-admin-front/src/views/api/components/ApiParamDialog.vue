<template>
  <div>
    <el-dialog :title="editParamForm.modify ? '编辑参数' : '新增参数'" :visible="true" :show-close="false">
      <el-form ref="form" class="form-block" label-width="150px" size="small" :model="editParamForm" :rules="rules">
        <el-form-item label="客户端参数" prop="sourceName">
          <el-row>
            <el-col :span="20">
              <el-input v-model.lazy="editParamForm.sourceName" placeholder="输入客户端参数."
                        :disabled="editParamForm.modify"/>
            </el-col>
            <el-col :span="4">
              <el-tag type="primary" v-if="isSysParam(editParamForm)"
                      style="width: 100%;margin-left: 10px;text-align: center">
                系统参数
              </el-tag>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="参数类型" prop="paramType">
          <ApiParamTypeSelector :value.sync="editParamForm.paramType" class="full-line"/>
        </el-form-item>
        <el-form-item label="服务端参数" prop="destName">
          <el-input v-model="editParamForm.destName" placeholder="输入服务端参数."/>
        </el-form-item>
        <el-form-item label="是否必须" prop="required">
          <ApiParamRequiredSelector :value.sync="editParamForm.required" class="full-line"/>
        </el-form-item>
        <el-form-item label="默认值" prop="defaultValue">
          <el-input v-model="editParamForm.defaultValue" placeholder="输入默认值."/>
        </el-form-item>
        <el-form-item label="参数描述" prop="description">
          <el-input v-model="editParamForm.description" placeholder="输入参数描述."/>
        </el-form-item>
      </el-form>
      <el-row style="padding-top: 30px; padding-bottom: 30px">
        <el-col :span="24" style="text-align: right">
          <el-button type="info" icon="el-icon-close" @click="dismissDialog" size="small">取消</el-button>
          <el-button type="primary" icon="el-icon-check" @click="() => saveParam(editParamForm)" size="small">
            确认
          </el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
  import ApiParamTypeSelector from './ApiParamTypeSelector'
  import ApiParamRequiredSelector from './ApiParamRequiredSelector'
  import GlobalConfig from '@/common/global'

  export default {
    name: 'ApiParamDialog',
    components: {ApiParamRequiredSelector, ApiParamTypeSelector},
    props: {
      // 变更参数表单
      initParam: {type: Object, default: Object({})},
      // 系统参数列表
      sysParams: {type: Array, default: []}
    },
    data: function () {
      return {
        GlobalConfig,
        // 表单参数
        editParamForm: {},
        showSystemDialog: false,
        rules: {
          sourceName: [
            {required: true, message: '请输入客户端参数', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {required: true, pattern: /^[a-zA-Z0-9]+$/, message: '参数名只支持数字与大小写字母', trigger: 'blur'}
          ],
          destName: [
            {required: true, message: '请输入客户端参数', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {required: true, pattern: /^[a-zA-Z0-9]+$/, message: '参数名只支持数字与大小写字母', trigger: 'blur'}
          ]
        }
      }
    },
    methods: {
      //----------------------------------------- 参数弹框相关逻辑 -----------------------------------------
      dismissDialog() {
        this.$emit('dismiss-dialog')
      },
      saveParam(param) {
        this.$refs.form.validate().then(() => {
          this.$emit('save-param', param)
        })
      },
      addSysParam(param) {
        this.editParamForm.sourceName = param.name
        this.editParamForm.paramType = param.paramType
        this.editParamForm.description = param.description
        this.showSystemDialog = false
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
      }
    },
    created() {
      // 初始化数据做对象深拷贝，防止对象引用污染
      this.editParamForm = Object.assign({
        sourceName: '',
        params: [],
        paramType: 20,
        required: 1,
        description: ''
      }, JSON.parse(JSON.stringify(this.initParam)))
    }
  }
</script>

<style lang="stylus">
  .full-line
    width: 100%
</style>