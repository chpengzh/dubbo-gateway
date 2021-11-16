import axios from 'axios'
import {errorHandle} from './error-handler'

export function createOrUpdateApp(param, success) {
  return axios.post('/management/app', param).then(success).catch(errorHandle)
}

export function getApp(param, success) {
  return axios.get('/management/app/list', {params: param}).then(success).catch(errorHandle)
}

export function getAppByKeyword(param, success) {
  return axios.get('/management/app/keyword', {params: param}).then(success).catch(errorHandle)
}

export function getAppAuthByApiAndAppKeyPrefix(param, success) {
  return axios.get('/management/app/auth/list', {params: param}).then(success).catch(errorHandle)
}

export function addAuth(param, success) {
  return axios.post('/management/app/auth', param).then(success).catch(errorHandle)
}

export function removeAuth(param, success) {
  return axios.post('/management/app/unauth', param).then(success).catch(errorHandle)
}