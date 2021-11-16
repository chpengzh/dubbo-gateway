<template>
  <div class="wrapper">
    <el-row>
      <el-col :span="19">
        <h3>
          <el-link class="el-icon-arrow-left" href="/#/api/doc/list" style="font-size: x-large; padding-bottom: 5px"/>
          接口文档: {{currentVersion.apiName}}
        </h3>
        <ApiDoc
          :current-version.sync="currentVersion"
          :trunk.sync="trunk"/>
      </el-col>
      <el-col :span="3">
        <div style="padding-top: 66px; padding-left: 80px">
          <el-button type="primary" icon="el-icon-video-play" @click="toTesting">测试上线版本</el-button>
        </div>
        <ApiRefLog
          :api-name.sync="meta.apiName"
          :release-version.sync="meta.releaseVersion"
          @version-loaded="versionLoaded"
          @version-picked="selectVersion"/>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import routerParam from '@/common/url-param'
  import ApiDoc from './components/ApiDoc'
  import ApiRefLog from './components/ApiRefLog'

  export default {
    components: {ApiRefLog, ApiDoc},
    data() {
      return {
        meta: {
          apiName: '',
          releaseVersion: ''
        },
        // 当前查看版本
        currentVersion: {},
        // 生产环境发布版本
        trunk: {}
      }
    },
    methods: {
      routerParam,
      versionLoaded(item) {
        this.currentVersion = item.currentVersion
        this.trunk = item.trunk
        window.location.href = '/#/api/doc' +
          '?api=' + item.currentVersion.apiName +
          '&releaseVersion=' + item.currentVersion.releaseVersion
      },
      selectVersion(item) {
        this.meta = {
          apiName: item.apiName,
          releaseVersion: item.releaseVersion
        }
      },
      toTesting() {
        window.location.href = '/#/api/testing' +
          '?api=' + this.currentVersion.apiName
      }
    },
    created() {
      this.selectVersion({
        apiName: routerParam('api'),
        releaseVersion: routerParam('releaseVersion')
      })
    }
  }
</script>

<style scoped lang="stylus">
  .wrapper
    background white
    padding 2% 3% 3%
</style>