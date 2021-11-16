<template>
  <div>
    <el-dialog :title="editObjectParamForm.modify ? '编辑对象参数' : '新增对象参数'"
               :visible="true" width="1300px" :show-close="false">
      <el-form ref="basic" class="form-block" label-width="150px" size="small"
               :model="editObjectParamForm" :rules="rules">
        <el-form-item label="客户端参数" prop="sourceName">
          <el-input v-model="editObjectParamForm.sourceName" placeholder="输入客户端参数."
                    :disabled="editObjectParamForm.modify"/>
        </el-form-item>
        <el-form-item label="参数类名" prop="className">
          <el-input v-model="editObjectParamForm.className" placeholder="输入参数类名"/>
        </el-form-item>
        <el-form-item label="服务端参数" prop="destName">
          <el-input v-model="editObjectParamForm.destName" placeholder="输入服务端参数."/>
        </el-form-item>
      </el-form>
      <el-table :data="editObjectParamForm.params" class="row-element" empty-text="无属性参数">
        <el-table-column prop="sourceName" label="客户端参数" width="150px">
          <template slot-scope="scope">
            <el-form :ref="'row-sourceName-' + scope.$index" class="form-block" label-width="0" size="small"
                     :model="scope.row" :rules="rules">
              <el-form-item prop="sourceName" label-width="0" style="margin: 0">
                <el-input v-model="scope.row.sourceName"/>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="paramType" label="参数类型" width="130px" show-overflow-tooltip>
          <template slot-scope="scope">
            <ApiParamTypeSelector :value.sync="scope.row.paramType" class="full-line"/>
          </template>
        </el-table-column>
        <el-table-column prop="destName" label="服务端参数" width="150px" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-form :ref="'row-destName-' + scope.$index" class="form-block" label-width="0" size="small"
                     :model="scope.row" :rules="rules">
              <el-form-item prop="destName" label-width="0" style="margin: 0">
                <el-input v-model="scope.row.destName"/>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="required" label="是否必须" width="150px" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-form :ref="'row-required-' + scope.$index" class="form-block" label-width="0" size="small">
              <el-form-item label-width="0" style="margin: 0">
                <ApiParamRequiredSelector
                  :value.sync="scope.row.required"
                  class="full-line"/>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="defaultValue" label="默认值" width="150px" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-form :ref="'row-defaultValue-' + scope.$index" class="form-block" label-width="0" size="small">
              <el-form-item label-width="0" style="margin: 0">
                <el-input v-model="scope.row.defaultValue"/>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="参数描述" min-width="200px" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-form :ref="'row-description-' + scope.$index" class="form-block" label-width="0" size="small">
              <el-form-item label-width="0" style="margin: 0">
                <el-input v-model="scope.row.description"/>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column width="160px">
          <template slot-scope="scope">
            <el-row>
              <el-link type="primary" @click="editObjectParamForm.params.splice(scope.$index, 1)">删除</el-link>
            </el-row>
          </template>
        </el-table-column>
      </el-table>
      <el-button style="margin-top: 10px" class="full-line" icon="el-icon-plus" @click="addNewParam">
        新增对象属性
      </el-button>
      <el-row style="padding-top: 30px; padding-bottom: 30px">
        <el-col style="text-align: right" :span="24">
          <el-button type="info" icon="el-icon-close" @click="dismissDialog">取消</el-button>
          <el-button type="primary" icon="el-icon-check" @click="saveParam(editObjectParamForm)">确认</el-button>
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
    name: 'ApiObjectParamDialog',
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
        editObjectParamForm: {},
        showSystemDialog: false,
        modifyParam: {},
        rules: {
          sourceName: [
            {required: true, message: '请输入客户端参数', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {required: true, pattern: /^[a-zA-Z0-9]+$/, message: '参数名只支持数字与大小写字母', trigger: 'blur'}
          ],
          destName: [
            {required: true, message: '请输入客户端参数', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {required: true, pattern: /^[a-zA-Z0-9]+$/, message: '参数名只支持数字与大小写字母', trigger: 'blur'},
            {
              trigger: 'blur', validator: (rule, value, callback) => {
                if (!this.editObjectParamForm.params) {
                  return callback()
                }
                let exists = []
                for (let i = 0; i < this.editObjectParamForm.params.length; ++i) {
                  let param = this.editObjectParamForm.params[i]
                  if (exists.indexOf(param.destName) === -1) {
                    exists.push(param.destName)
                  } else if (value === param.destName) {
                    this.$message.error('属性名称重复(服务端参数' + param.destName + ')')
                    return callback(new Error('属性名称重复' + value))
                  }
                }
                return callback()
              }
            }
          ],
          className: [
            {required: true, message: '请输入参数类名', trigger: 'blur'},
            {max: 128, message: '长度不超过128字符', trigger: 'blur'},
            {
              required: true, message: '请输入合法的全限定接口名称', trigger: 'blur',
              pattern: /^([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*$/
            }
          ],
        }
      }
    },
    methods: {
      dismissDialog() {
        this.$emit('dismiss-dialog')
      },
      saveParam(param) {
        let future = []
        for (const form in this.$refs) {
          const next = this.$refs[form]
          if (next && typeof next.validate === 'function') {
            future.push(next.validate())
          }
        }
        Promise.all(future).then(() => {
          this.$emit('save-param', param)
        }).catch(() => this.$message.error('请仔细检查填写表单信息!'))
      },
      modifyAsSysParam(param) {
        this.modifyParam = param
        this.showSystemDialog = true
      },
      selectSysParam(sysParam) {
        this.modifyParam.sourceName = sysParam.name
        this.modifyParam.paramType = sysParam.paramType
        this.modifyParam.description = sysParam.description
        this.showSystemDialog = false
      },
      addNewParam() {
        this.editObjectParamForm.params.push({
          sourceName: '',
          params: [],
          paramType: 50,
          required: 1,
          description: ''
        })
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
      this.editObjectParamForm = Object.assign({
        sourceName: '',
        params: [],
        paramType: 60,
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