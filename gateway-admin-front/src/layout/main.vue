<template>
  <div class="window-height main-page">
    <el-row class="tac window-height">
      <el-col :span="3" class="window-height side-nav-bar">
        <div class="logo" :style="{'background': background}">{{title}}</div>
        <el-menu
          router
          default-active="2"
          text-color="#fff"
          :background-color="background"
          style="height: 100%">
          <SubMenu :menu-list="menu"/>
        </el-menu>
      </el-col>
      <el-col :span="21" class="window-height" style="padding-top: 60px">
        <div class="title-divider"/>
        <el-alert
          title="当前访问环境为生产环境，请谨慎操作!!!" type="error"
          :closable="false" effect="dark"
          v-if="env === 'prod'"/>
        <div class="main-wrapper">
          <router-view/>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import SubMenu from './components/SubMenu'
  import router from '@/router'
  import {getEnv} from '@/common/env'

  const env = getEnv()
  export default {
    components: {SubMenu},
    data() {
      return {
        env,
        backgroundColor: {
          prod: '#930b00',
          pre: '#745b18',
          default: '#252857',
        },
        menu: []
      }
    },
    methods: {
      getRenderMenu() {
        function calculate(next) {
          if (next.children && next.children.length > 0) {
            // 为每个子节点元素添加id作为调用索引
            next.children.forEach(it => {
              it.id = next.id + (path => {
                switch (path) {
                  case '':
                  case '/':
                    return ''
                  default:
                    return path.startsWith('/') ? path : ('/' + path)
                }
              })(it.path)
              calculate(it)
            })

            // 如果子节点就1个，直接使用该子节点作为父节点
            next.menuChildren = next.children.filter(it => !it.hidden)
          }
          return next
        }

        return calculate({
          id: '',
          children: JSON.parse(JSON.stringify(router.options.routes))
        }).menuChildren
      }
    },
    computed: {
      title() {
        return 'Dubbo Gateway'
      },
      background() {
        switch (env) {
          case 'pre':
            return this.backgroundColor.pre
          case 'prod':
            return this.backgroundColor.prod
          default:
            return this.backgroundColor.default
          // return this.backgroundColor.pre
          // return this.backgroundColor.prod
        }
      }
    },
    created() {
      this.menu = this.getRenderMenu()
      console.log(this.menu)
    }
  }
</script>

<style lang="stylus">
  .logo
    padding-left: 16px;
    height: 68px;
    line-height: 68px;
    font-family: PingFangSC-Medium;
    font-size: 24px;
    color: #fff;
    text-align: left;
    font-weight: 500;
    margin-top: -70px

  .el-menu
    border 0

  .window-height
    height 100%

  .side-nav-bar
    height 100%
    padding-top 70px

  .top-nav-bar
    height: 60px;
    margin-top: -60px;
    padding-right: 60px

  .title-divider
    background: #f4f4f5;
    height: 10px;
    width: 100%

  .main-wrapper
    overflow-y auto;
    overflow-x hidden
    height: 100%

  .main-page
    overflow hidden;
</style>
