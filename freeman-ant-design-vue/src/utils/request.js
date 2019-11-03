import axios from 'axios'
// import qs from 'qs'
import { message, Modal, notification } from 'ant-design-vue'
import moment from 'moment'
import store from '../store'
import ls from '@utils/localstorage'
import { isPlainObject, isEmptyObject } from '@utils/util'
moment.locale('zh-cn')

// 创建 axios 实例 ,统一配置
let AXIOS = axios.create({
  baseURL: 'http://127.0.0.1:9081/',
  responseType: 'json',
  validateStatus (status) {
    // 200 外的状态码都认定为失败
    return status === 200
  }
})

// 拦截请求
AXIOS.interceptors.request.use((config) => {
  // 有 token就带上
  // const token = store.state.account.accessToken
  const token = ls.get('ACCESS_TOKEN')
  if (token && !config.url.includes('login')) {
    config.headers.Authorization = token
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

// 拦截响应
AXIOS.interceptors.response.use((config) => {
  // 当后端自动刷新令牌后,响应头中会有’isNewToken‘, 拿到新的令牌就保存一下
  if (config.headers.isnewtoken) {
    // store.commit('account/setAccessToken', config.headers.authorization)
    ls.set('ACCESS_TOKEN', config.headers.authorization)
  }
  return config
}, (error) => {
  if (error.response) {
    const token = store.state.account.accessToken
    console.log('>>>>>>>>>异常AccessToken>>>>>>>>>', token)
    console.error('>>>>>>>>>异常响应status>>>>>>>>>', error.response.status)
    let errorMessage = error.response.data === null ? '系统内部异常，请联系网站管理员' : error.response.data.message
    switch (error.response.status) {
      case 401: // 无权限
        // notification.error({ message: '系统提示', description: '未授权，请重新登录', duration: 4 })
        if (token) {
          Modal.error({
            title: '登录已过期',
            content: '未授权，请重新登录',
            okText: '重新登录',
            mask: false,
            onOk: () => {
              store.dispatch('account/Logout')
            }
          })
        }
        break
      case 403: // 拒绝访问
        notification.error({ message: '系统提示', description: '很抱歉，您无法访问该资源，请重新登录', duration: 4 })
        break
      case 404: // 请求资源未找到
        notification.error({ message: '系统提示', description: '很抱歉，资源未找到', duration: 4 })
        break
      case 500: // 服务器发生异常
        if (token && errorMessage === 'Token失效，请重新登录') {
          Modal.error({
            title: '登录已过期',
            content: '很抱歉，登录已过期，请重新登录',
            okText: '重新登录',
            mask: false,
            onOk: () => {
              store.dispatch('account/Logout')
            }
          })
        }
        break
      case 504:
        notification.error({ message: '系统提示', description: '网络响应超时', duration: 4 })
        break
      default:
        notification.error({ message: '系统提示', description: errorMessage, duration: 4 })
        break
    }
  }
  return Promise.reject(error)
})

/**
 * 拼接get请求参数
 * @param {*} params get參數
 * @param {*} urlSearchParams UrlSearchParams 對象
 * @param {*} keys 键
 */
function joinUrlSearchParams (params, urlSearchParams, keys = '') {
  if (params instanceof Array) {
    params.forEach(arrEle => {
      if (arrEle instanceof Object) {
        joinUrlSearchParams(arrEle, urlSearchParams, `${keys}`)
      } else if (arrEle instanceof Array) {
        joinUrlSearchParams(arrEle, urlSearchParams, `[${keys}]`)
      } else if (arrEle && keys) {
        urlSearchParams.append(`${keys}`, arrEle)
      }
    })
  } else if (params instanceof Object) {
    Object.keys(params).forEach(key => {
      let value = params[key]
      if (value instanceof Array) {
        value.forEach(arrEle => {
          if (arrEle instanceof Array) {
            joinUrlSearchParams(arrEle, urlSearchParams, keys ? `${keys}[${key}]` : key)
          } else if (arrEle instanceof Object) {
            joinUrlSearchParams(arrEle, urlSearchParams, keys ? `${keys}.${key}` : key)
          } else if (arrEle) {
            urlSearchParams.append(keys ? `${keys}[${key}]` : key, arrEle)
          }
        })
      } else if (value instanceof Object) {
        joinUrlSearchParams(value, urlSearchParams, keys ? `${keys}.${key}` : key)
      } else if (value) {
        urlSearchParams.append(keys ? `${keys}.${key}` : key, value)
      }
    })
  }
}

/**
 * 解析替换restful url中的变量
 * @param url 比如==> /sys/menu/:a/:id/:b
 * @param pathVariable 路径变量的参数对象 比如==> {id:123,a:'aaa',b:'bbb'}
 * @returns 解析结果==> /sys/menu/aaa/123/bbb
 */
export function parsingUrl (url, pathVariable) {
  if (pathVariable && isPlainObject(pathVariable)) {
    url = url.replace(/(:\w+)/g, ($1) => {
      if ($1 !== '') return pathVariable[$1.slice(1)]
      return $1
    })
  }
  return url
}

const $ = {
  get (url, params = {}, pathVariable = {}) {
    if (!isEmptyObject(pathVariable)) url = parsingUrl(url, pathVariable)
    let _params
    if (Object.is(params, undefined)) {
      _params = ''
    } else {
      var urlSearchParams = new URLSearchParams()
      joinUrlSearchParams(params, urlSearchParams)
      const qp = urlSearchParams.toString()
      _params = qp + (qp ? '&_t=' : '_t=') + new Date().getTime()
      console.log(`get请求==> ${url}?${_params}`)
    }
    return AXIOS.get(`${url}?${_params}`)
  },
  post (url, params = {}, pathVariable = {}) {
    if (!isEmptyObject(pathVariable)) url = parsingUrl(url, pathVariable)
    // 子属性(不是数组)用[]包装了, a.b ==> a[b]
    // return AXIOS.post(url, qs.stringify(params, {indices: false}))

    // 直接提交的json ==> springmvc @RespeonseBody
    /* return AXIOS({
      url,
      method: 'POST',
      headers: { 'content-type': 'application/json;charset=UTF-8' },
      data: params
    }) */

    return AXIOS.post(url, params, {
      transformRequest: [(params) => {
        let urlSearchParams = new URLSearchParams()
        joinUrlSearchParams(params, urlSearchParams)
        return urlSearchParams.toString()
      }],
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },
  put (url, params = {}, pathVariable = {}) {
    if (!isEmptyObject(pathVariable)) url = parsingUrl(url, pathVariable)
    return AXIOS.put(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },
  delete (url, params = {}, pathVariable = {}) {
    if (!isEmptyObject(pathVariable)) url = parsingUrl(url, pathVariable)
    let _params
    if (Object.is(params, undefined)) {
      _params = ''
    } else {
      _params = '?'
      for (let key in params) {
        if (params.hasOwnProperty(key) && params[key] !== null) {
          _params += `${key}=${params[key]}&`
        }
      }
    }
    return AXIOS.delete(`${url}${_params}`)
  },
  export (url, params = {}, pathVariable = {}) {
    if (!isEmptyObject(pathVariable)) url = parsingUrl(url, pathVariable)
    message.loading('导出数据中')
    return AXIOS.post(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      responseType: 'blob'
    }).then((r) => {
      const content = r.data
      const blob = new Blob([content])
      const fileName = `${new Date().getTime()}_导出结果.xlsx`
      if ('download' in document.createElement('a')) {
        const elink = document.createElement('a')
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href)
        document.body.removeChild(elink)
      } else {
        navigator.msSaveBlob(blob, fileName)
      }
    }).catch((r) => {
      console.error(r)
      message.error('导出失败')
    })
  },
  download (url, params, filename) {
    message.loading('文件传输中')
    return AXIOS.post(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      responseType: 'blob'
    }).then((r) => {
      const content = r.data
      const blob = new Blob([content])
      if ('download' in document.createElement('a')) {
        const elink = document.createElement('a')
        elink.download = filename
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href)
        document.body.removeChild(elink)
      } else {
        navigator.msSaveBlob(blob, filename)
      }
    }).catch((r) => {
      console.error(r)
      message.error('下载失败')
    })
  },
  upload (url, params) {
    return AXIOS.post(url, params, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default $
export {
  $,
  AXIOS as axios
}
