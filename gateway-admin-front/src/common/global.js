const link = {
  systemParam: '',
  userHelp: '',
  errorCode: '',
  searchLog: '',
  getMyToken: '',
  dashboard: ''
}
const env = {
  prod: 'http://localhost:8080',
  pre: 'http://localhost:8080',
  stable: 'http://localhost:8080',
  test: 'http://localhost:8080'
}
const envNav = [
  {name: 'prod', label: '生产环境', host: 'https://' + env.prod},
  {name: 'pre', label: '预发环境', host: 'https://' + env.pre},
  {name: 'stable', label: '线下基准(stable)', host: 'https://' + env.stable},
  {name: 'test', label: '线下测试(test)', host: 'https://' + env.test}
]

const GlobalConfig = {env, link, envNav}
export default GlobalConfig