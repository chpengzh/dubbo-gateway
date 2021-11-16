<template>
  <div>
    <el-row class="wrapper">
      <el-row>
        <el-col :span="12">
          <ApiDocTabs tab-activate="document"/>
        </el-col>
        <el-col :span="7" style="padding-left: 20px; padding-right: 10px">
          <el-input v-model="searchInput" placeholder="输入接口名称,dubbo服务进行搜索"
                    @keyup.enter.native="loadPage(1)"/>
        </el-col>
        <el-col :span="5">
          <el-button type="primary" icon="el-icon-search" @click="loadPage(1)">搜索</el-button>
          <el-button type="warning" icon="el-icon-plus" @click="toApiCreate">新增接口</el-button>
        </el-col>
      </el-row>
      <el-table :data="tableData" fit highlight-current-row class="row-element" empty-text="暂无服务">
        <el-table-column key="apiName" label="接口名称" :show-overflow-tooltip="true" width="300px">
          <template slot-scope="scope">{{ scope.row.apiName }}</template>
        </el-table-column>
        <el-table-column key="status" label="dubbo服务" :show-overflow-tooltip="true" min-width="500px">
          <template slot-scope="scope">{{ scope.row.dubboService + '#' + scope.row.dubboMethod }}</template>
        </el-table-column>
        <el-table-column key="gmtCreate" label="变更时间" width="200px">
          <template slot-scope="scope">{{ formatDate(scope.row.gmtModify) }}</template>
        </el-table-column>
        <el-table-column key="control-panel" label="" width="200px">
          <div slot-scope="scope">
            <el-link type="primary" :href="toApiDoc(scope.row)">接口文档</el-link>
            <el-link type="primary" :href="toApiTesting(scope.row)" style="margin-left: 20px">测试调用</el-link>
          </div>
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="loadPage"
        :current-page="page"
        :page-size="100"
        layout="prev, pager, next, jumper"
        :total="100000">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
  import {getApi, getApiByKeyword} from '@/api/api'
  import {formatDate} from '@/common/date'
  import ApiDocTabs from "./components/ApiDocTabs";

  export default {
    components: {ApiDocTabs},
    data: function () {
      return {
        searchInput: '',
        tableData: [],
        page: 1
      }
    },
    methods: {
      formatDate,
      loadData() {
        const pageSize = 10
        this.tableData = []
        if (this.searchInput) {
          getApiByKeyword({
            keyword: this.searchInput,
            page: this.page,
            pageSize
          }, resp => {
            if (resp.data.data) {
              this.tableData = resp.data.data
            }
          })
        } else {
          getApi({
            page: this.page,
            pageSize
          }, resp => {
            if (resp.data.data) {
              this.tableData = resp.data.data
            }
          })
        }
      },
      loadPage(page) {
        this.page = page
        this.loadData()
      },
      toApiDoc(item) {
        return '/#/api/doc?api=' + item.apiName
      },
      toApiEdit(item) {
        return '/#/api/editor?api=' + item.apiName
      },
      toApiCreate() {
        window.location.href = '/#/api/editor'
      },
      toApiTesting(item) {
        return '/#/api/testing?api=' + item.apiName
      }
    },
    created() {
      this.loadData()
    }
  }
</script>

<style scoped lang="stylus">
  .wrapper
    background white
    padding 2% 3% 0
    height 100%

  .row-element
    padding-top 30px
    margin-bottom 60px

  .el-pagination
    background white
    text-align: center
    bottom: 30px
    padding-top 30px
    padding-bottom 30px
    right 2%
</style>