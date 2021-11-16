import axios from 'axios'
import {errorHandle} from './error-handler'

export function submitApprove(param, success) {
  return axios.post('/management/approval/approve', param).then(success).catch(errorHandle)
}

export function submitReject(param, success) {
  return axios.post('/management/approval/reject', param).then(success).catch(errorHandle)
}

export function getApproveProcess(param, success) {
  return axios.get('/management/approval', {params: param}).then(success).catch(errorHandle)
}