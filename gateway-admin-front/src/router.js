import Vue from "vue";
import Router from "vue-router"
// 异步加载模块
Vue.use(Router);
const router = new Router({
  mode: 'hash',
  routes: [
    {
      path: `/`,
      redirect: `/api`,
      hidden: true
    },
    {
      path: `/index`,
      redirect: `/api`,
      hidden: true
    },
    {
      path: '/api',
      label: '接口管理',
      icon: 'el-icon-receiving',
      component: () => import('@/layout/main'),
      children: [
        {
          path: '',
          label: '发布与审批',
          component: () => import('@/views/api/api-list')
        },
        {
          path: 'approving',
          label: '发布待审批',
          component: () => import('@/views/api/api-list-approving'),
          hidden: true
        },
        {
          path: 'versions',
          label: '最近发布记录',
          component: () => import('@/views/api/api-list-history'),
          hidden: true
        },
        {
          path: 'doc',
          label: '接口文档',
          component: () => import('@/views/api/api-doc'),
          hidden: true
        },
        {
          path: 'editor',
          label: '编辑与发布',
          component: () => import('@/views/api/api-editor'),
          hidden: true
        },
        {
          path: 'doc/list',
          label: '文档与用例',
          component: () => import('@/views/api/api-list-doc')
        },
        {
          path: 'testing',
          label: '接口测试',
          component: () => import('@/views/api/api-testing'),
          hidden: true
        }
      ]
    },
  ],
});

export default router