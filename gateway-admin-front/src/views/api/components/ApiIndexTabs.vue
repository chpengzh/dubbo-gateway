<template>
  <el-tabs v-model="tabActivate" :before-leave="select">
    <el-tab-pane label="全部服务" name="all-service"/>
    <el-tab-pane label="发布待审批" name="approving"/>
    <el-tab-pane label="最近发布记录" name="history"/>
  </el-tabs>
</template>

<script>
  export default {
    props: {
      // 当前激活的tab
      tabActivate: {type: String, default: 'all-service'}
    },
    methods: {
      select(activeName) {
        switch (activeName) {
          case 'all-service':
            window.location.href = '/#/api'
            break
          case 'approving':
            window.location.href = '/#/api/approving'
            break
          case 'history':
            window.location.href = '/#/api/versions'
            break
          default:
            break
        }
        return false
      }
    }
  }
</script>