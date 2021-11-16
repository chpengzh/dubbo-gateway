<template>
  <div class="wrapper">
    <h3>
      <el-link class="el-icon-arrow-left" href="/#/api/doc/list" style="font-size: x-large; padding-bottom: 5px"/>
      接口调用测试: {{artifact.apiName}}
    </h3>
    <el-divider content-position="left">请求入参</el-divider>
    <el-row>
      <el-col :span="18">
        <el-form ref="form" class="form-block" :model="request" label-width="150px" size="small">
          <el-form-item label="调用环境" prop="url">
            <ApiHostSelector :value.sync="request.url"/>
          </el-form-item>
          <el-form-item
            v-for="param in request.params"
            :key="param.sourceName"
            :prop="param.sourceName"
            :label="param.sourceName + ':'">
            <el-input
              v-model="param.value" :placeholder="'系统参数 ' + toParamDescription(param)"/>
          </el-form-item>
          <el-form-item
            v-for="param in request.body"
            :key="param.sourceName"
            :prop="param.sourceName"
            :label="param.sourceName + ':'">
            <el-input v-model="param.value" :placeholder="'业务参数 ' + toParamDescription(param)"/>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="4">
        <h4 style="margin-top: 5px; margin-bottom: 5px">我需要帮助:</h4>
        <ul>
          <li>
            <el-link type="primary" :href="GlobalConfig.link.getMyToken" target="_blank" size="small">
              如何获得测试用token? <span class="el-icon-question"></span></el-link>
          </li>
          <li>
            <el-link type="primary" :href="GlobalConfig.link.errorCode" target="_blank" size="small">
              如何查看网关错误码? <span class="el-icon-question"></span></el-link>
          </li>
          <li>
            <el-link type="primary" :href="GlobalConfig.link.searchLog" target="_blank" size="small">
              如何查看我的调用日志? <span class="el-icon-question"></span></el-link>
          </li>
        </ul>
      </el-col>
    </el-row>

    <el-divider content-position="left">请求预览</el-divider>
    <el-form ref="form" class="form-block" size="small">
      <el-form-item prop="url">
        <el-input v-model="url"/>
      </el-form-item>
      <p class="preview-title" v-if="request.headers.length > 0">Request Headers:</p>
      <pre class="code" v-if="request.headers.length > 0">{{headersRaw}}</pre>
      <p class="preview-title">Request Body:</p>
      <pre class="code">{{requestBodyRaw}}</pre>
    </el-form>

    <div v-if="response.body">
      <el-divider content-position="left">请求响应: HttpStatus {{response.status}}</el-divider>
      <div class="form-block">
        <pre class="code">{{responseBodyRaw}}</pre>
      </div>
    </div>

    <el-row style="margin-top: 40px">
      <el-col :span="12">
        <!--<el-button type="warning" icon="el-icon-s-tools">测试小工具</el-button>-->
      </el-col>
      <el-col :span="21" style="text-align: right">
        <el-button type="info" icon="el-icon-edit" @click="toApiEdit">编辑与发布</el-button>
        <el-button type="primary" icon="el-icon-video-play" @click="sendRequest">发送测试请求</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import routerParam from '@/common/url-param'
  import {getApiRelease, getApiVersions} from '@/api/api'
  import ApiHostSelector from './components/ApiHostSelector'
  import {toParamDescription} from '@/common/param-utils'
  import axios from 'axios'
  import GlobalConfig from '@/common/global'

  export default {
    components: {ApiHostSelector},
    data() {
      return {
        GlobalConfig,
        timestamp: '',
        sign: '',
        artifact: {
          apiName: '',
          clientSystemParams: [],
          clientBizParams: []
        },
        headerParams: [],
        request: {
          url: '',
          headers: [{sourceName: 'Content-Type', value: 'application/json; charset=UTF-8'}],
          params: [],
          body: []
        },
        response: {
          code: 0,
          body: ''
        }
      }
    },
    methods: {
      toParamDescription,
      routerParam,
      initData(apiName) {
        this.meta = {apiName}
        getApiRelease({apiName}, resp => {
          if (!resp.data.data) {
            this.$confirm('当前接口未上线, 无法进行测试, 是否去上线发布?', '未上线', {
              confirmButtonText: '去上线',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => window.location.href = '/#/api/editor' +
              '?api=' + apiName)
            return
          }
          const releaseVersion = resp.data.data['raw.releaseVersion']
          getApiVersions({releaseVersion}, resp => {
            if (resp.data.data && resp.data.data.length > 0) {
              this.artifact = resp.data.data[0]
              this.request.params = this.request.params
                .concat(this.artifact['client.systemParams'])
                .filter(it => ['appKey', 'sign', 'timestamp'].indexOf(it.sourceName) === -1)
              this.request.body = this.request.body.concat(this.artifact['client.bizParams'])
            } else {
              this.$confirm('当前接口未上线, 无法进行测试, 是否去上线发布?', '未上线', {
                confirmButtonText: '去上线',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => window.location.href = '/#/api/editor' +
                '?api=' + apiName)
            }
          })
        })
      },
      toApiEdit() {
        window.location.href = '/#/api/editor' +
          '?api=' + this.artifact.apiName
      },
      requestHeaders() {
        let result = [].concat(this.request.headers)
        this.request.params
          .filter(it => this.headerParams.indexOf(it.sourceName) >= 0)
          .forEach(it => result.push(it))
        return result
      },
      requestBody() {
        const result = {}
        console.log(this.request.body)
        this.request.body.forEach(it => result[it.sourceName] = it.value)
        return result
      },
      sendRequest() {
        axios.post(this.url, this.requestBody(), {
          headers: Object.fromEntries(new Map(this.requestHeaders().map(it => [it.sourceName, it.value])))
        }).then(resp => {
          this.response.body = JSON.stringify(resp.data, null, '    ')
          this.response.status = resp.status
        }).catch((err) => {
          this.$message.error(err)
        })
      }
    },
    computed: {
      url() {
        let result = (this.request.url || '/gateway')
          + '?api=' + this.meta.apiName
        this.request.params
          .filter(it => it.value && this.headerParams.indexOf(it.sourceName) === -1)
          .forEach(param => result += '&' + param.sourceName + '=' + param.value)
        return result
      },
      headersRaw() {
        let result = ''
        this.requestHeaders().forEach(header => result += (header.sourceName + ': ' + (header.value || '') + '\n'))
        return result
      },
      requestBodyRaw() {
        return JSON.stringify(this.requestBody(), null, '    ')
      },
      responseBodyRaw() {
        return this.response.body
      }
    },
    created() {
      const apiName = routerParam('api')
      this.initData(apiName)
    }
  }
</script>

<style scoped lang="stylus">
  .wrapper
    background white
    padding 2% 3% 3%

  .form-block
    background white
    padding 0 3% 0

  .preview-title
    color: #1a1c3d

  .code
    width 100%;
    background #f4f4f4;
    padding: 30px 20px 30px 20px
    white-space: pre-wrap
    word-wrap: break-word
</style>