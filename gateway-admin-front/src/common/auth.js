import Cookies from 'js-cookie'

const TokenKey = 'session-info'

function getToken() {
  return Cookies.get(TokenKey)
    ? JSON.parse(Cookies.get(TokenKey))
    : undefined
}

function setToken(token) {
  return Cookies.set(TokenKey, token)
}

function removeToken() {
  return Cookies.remove(TokenKey)
}

export {getToken, setToken, removeToken}
