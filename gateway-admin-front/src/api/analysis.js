import axios from 'axios'
import {errorHandle} from './error-handler'

export function searchLog(host, param, success) {
  return axios.post('//' + host + '/analysis/log', param).then(success).catch(errorHandle)
}

export function getApiMonitor(param, success) {
  return axios.post('/analysis/monitor', param).then(success).catch(errorHandle)
}
