<template>
  <el-tabs v-model="tabActivate" :before-leave="select">
    <el-tab-pane label="接口文档" name="document"/>
  </el-tabs>
</template>

<script>
  export default {
    props: {
      // 当前激活的tab
      tabActivate: {type: String, default: 'document'}
    },
    methods: {
      select(activeName) {
        switch (activeName) {
          case 'document':
            window.location.href = '/#/api/doc/list'
            break
          default:
            break
        }
        return false
      }
    }
  }
</script>