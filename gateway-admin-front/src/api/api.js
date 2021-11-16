import axios from 'axios'
import {errorHandle} from './error-handler'

export function submitApi(param, success) {
  return axios.post('/management/api', param).then(success).catch(errorHandle)
}

export function getApi(param, success) {
  return axios.get('/management/api/list', {params: param}).then(success).catch(errorHandle)
}

export function getApiByKeyword(param, success) {
  return axios.get('/management/api/keyword', {params: param}).then(success).catch(errorHandle)
}

export function getApiRelease(param, success) {
  return axios.get('/management/api/release', {params: param}).then(success).catch(errorHandle)
}

export function getApiVersions(param, success) {
  return axios.get('/management/api/versions', {params: param}).then(success).catch(errorHandle)
}

export function getApiSysParam(success) {
  return axios.get('/management/api/param/system').then(success).catch(errorHandle)
}

export function getApiParamSign(sysParam, bizParam, success) {
  return axios.post('/management/api/param/sign', bizParam, {params: sysParam}).then(success).catch(errorHandle)
}

export function getApiPrefix(success) {
  return axios.get('/management/api/prefix').then(success).catch(errorHandle)
}

export function syncApi(host, param, success) {
  return axios.post(host + '/management/api/sync', param).then(success).catch(errorHandle)
}

export function offline(param, success) {
  return axios.post('/management/api/offline', null, {params: param}).then(success).catch(errorHandle)
}