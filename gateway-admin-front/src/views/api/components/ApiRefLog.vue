<template>
  <div>
    <h3 style="text-align: right; padding-top: 20px">历史发布记录</h3>
    <div style="margin-left: 30%; width: 300px">
      <el-steps direction="vertical" :space="70">
        <el-step
          v-for="item in data.versions"
          :title="maskVersion(item.releaseVersion)"
          :key="item.releaseVersion"
          :description="versionDescription(item)"
          :status="versionStatus(item)"
          :style="{cursor: enableSelect ? 'pointer': ''}"
          @click.native="versionPicked(item)"/>
      </el-steps>
    </div>
  </div>
</template>

<script>
  import {getApi, getApiVersions} from '@/api/api'
  import {getApproveProcess} from '@/api/approval'
  import {formatDate} from '@/common/date'

  export default {
    name: 'ApiRefLog',
    props: {
      apiName: '',
      releaseVersion: '',
      // 支持点选
      enableSelect: {type: Boolean, default: true}
    },
    data: function () {
      return {
        data: {
          // 生产环境发布版本
          trunk: {},
          // 当前查看版本, 以及当前查看版本对应的审批单
          currentVersion: {},
          approveProcess: {},
          // 历史发布版本集合
          versions: []
        }
      }
    },
    methods: {
      formatDate,
      reloadData() {
        // 1.获取定义
        const apiName = this.apiName
        const releaseVersion = this.releaseVersion
        if (!apiName) {
          return
        }

        // 2.加载完毕后的事件回调
        let loadCount = 0
        const next = () => {
          if (++loadCount === 3) {
            this.$emit('version-loaded', {
              trunk: this.data.trunk,
              approveProcess: {...this.data.approveProcess},
              currentVersion: {
                ...this.data.currentVersion,
                versionType: this.getVersionType()
              }
            })
          }
        }

        // 3.加载数据
        getApi({apiName}, resp => {
          if (resp.data.data) {
            this.data.trunk = resp.data.data[0]
          } else {
            this.data.trunk = {}
          }
          next()
        })
        getApiVersions({apiName, pageSize: 15}, resp => {
          if (resp.data.data) {
            this.data.versions = resp.data.data
          } else {
            this.data.versions = []
          }
          next()
        })
        getApiVersions(releaseVersion ? {releaseVersion} : {apiName}, resp => {
          if (resp.data.data) {
            const currentVersion = resp.data.data[0]
            this.data.currentVersion = currentVersion
            getApproveProcess({approveId: currentVersion['raw.approveId']}, resp => {
              if (resp.data.data && resp.data.data.length > 0) {
                this.data.approveProcess = resp.data.data[0]
              } else {
                this.data.approveProcess = {}
              }
              next()
            })
          } else {
            this.data.currentVersion = {}
            this.data.approveProcess = {}
            next()
          }
        })
      },
      versionPicked(item) {
        this.$emit('version-picked', item)
      },
      maskVersion(releaseVersion) {
        let result = (releaseVersion && releaseVersion.length > 7)
          ? releaseVersion.substring(0, 7)
          : releaseVersion
        if (this.data.currentVersion && this.data.currentVersion.releaseVersion === releaseVersion) {
          result += '【当前查看版本】'
        }
        return result
      },
      versionDescription(item) {
        if (item['raw.approveResult'] === 0) {
          return formatDate(item['raw.approveStart'], 'YYYY-MM-dd') + ' (审批中)'
        } else if (item['raw.approveResult'] === 2) {
          return formatDate(item['raw.approveStart'], 'YYYY-MM-dd') + ' (已驳回)'
        } else if (this.data.trunk.releaseVersion === item.releaseVersion) {
          return formatDate(item['raw.approveStart'], 'YYYY-MM-dd') + ' (上线版本)'
        }
        return formatDate(item['raw.approveStart'])
      },
      getVersionType() {
        if (!this.data.currentVersion.releaseVersion) {
          return ''
        }
        if (this.data.trunk.releaseVersion &&
          this.data.trunk.releaseVersion === this.data.currentVersion.releaseVersion) {
          return 'online'
        }
        if (this.data.currentVersion['raw.approveResult'] === 0) {
          return 'approving'
        }
        if (this.data.currentVersion['raw.approveResult'] === 2) {
          return 'reject'
        }
        return 'history'
      },
      versionStatus(item) {
        if (item.releaseVersion === this.data.trunk.releaseVersion) {
          return 'success'
        } else if (item['raw.approveResult'] === 0) {
          return 'finish'
        } else if (item['raw.approveResult'] === 2) {
          return 'error'
        }
        return 'process'
      }
    },
    watch: {
      apiName() {
        this.reloadData()
      },
      releaseVersion() {
        this.reloadData()
      }
    },
    created() {
      this.reloadData()
    }
  }
</script>