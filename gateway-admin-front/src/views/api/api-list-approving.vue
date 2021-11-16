<template>
  <div>
    <el-row class="wrapper">
      <el-row>
        <el-col :span="24">
          <ApiIndexTabs tab-activate="approving"/>
        </el-col>
      </el-row>
      <el-table :data="tableData" fit highlight-current-row class="row-element" empty-text="暂无待发布审批">
        <el-table-column key="approve-result" label="版本状态" :show-overflow-tooltip="true" min-width="80px">
          <template slot-scope="scope">
            <el-tag :type="getDescription(scope.row)[1]">{{getDescription(scope.row)[0]}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column key="releaseVersion" label="发布版本" :show-overflow-tooltip="true" width="100px">
          <template slot-scope="scope">{{scope.row.releaseVersion.length > 7
            ? scope.row.releaseVersion.substring(0,7)
            : scope.row.releaseVersion}}
          </template>
        </el-table-column>
        <el-table-column key="gmtCreate" label="提交时间" :show-overflow-tooltip="true" width="160px">
          <template slot-scope="scope">{{formatDate(scope.row['raw.approveStart'])}}</template>
        </el-table-column>
        <el-table-column key="apiName" label="接口名称" :show-overflow-tooltip="true" width="200px">
          <template slot-scope="scope">{{scope.row.apiName}}</template>
        </el-table-column>
        <el-table-column key="status" label="dubbo服务" :show-overflow-tooltip="true" min-width="400px">
          <template slot-scope="scope">{{scope.row.dubboService + '#' + scope.row.dubboMethod}}</template>
        </el-table-column>
        <el-table-column key="control-panel" label="" width="200px">
          <div slot-scope="scope">
            <el-link type="primary" @click="showVersion(scope.row)">查看版本记录</el-link>
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
  import {formatDate} from '@/common/date'
  import {getApiVersions} from '@/api/api'
  import ApiIndexTabs from './components/ApiIndexTabs'

  export default {
    components: {ApiIndexTabs},
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
        getApiVersions({
          approveResult: 0,
          page: this.page,
          pageSize
        }, resp => {
          if (resp.data.data) {
            this.tableData = resp.data.data
          } else {
            this.tableData = []
          }
        })
      },
      loadPage(page) {
        this.page = page
        this.loadData()
      },
      showVersion(item) {
        window.location.href = '/#/api/editor' +
          '?api=' + item.apiName +
          '&releaseVersion=' + item['raw.releaseVersion']
      },
      getDescription(item) {
        switch (item['raw.approveResult']) {
          case 0:
            return ['审批中', 'warning']
          case 1:
            return ['已发布', 'success']
          case 2:
            return ['已驳回', 'danger']
        }
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