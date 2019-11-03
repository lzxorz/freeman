/**
 * 设置值
 * @param {*} key 键
 * @param {*} value 值
 * @param {*} maxAge 秒
 */
const set = function (key, value, maxAge = null) {
  if (!key) {
    return
  }
  if (typeof value !== 'string') {
    value = JSON.stringify(value)
  }

  window.localStorage.setItem(key, value)
  if (maxAge && !isNaN(parseInt(maxAge))) {
    let timeout = parseInt(new Date().getTime() / 1000)
    window.localStorage.setItem(`${key}_expire`, timeout + maxAge)
  }
}
/**
 * 获取值
 * @param {*} key 键
 */
const get = function (key) {
  if (!key) {
    return
  }
  let content = window.localStorage.getItem(key)
  let expire = window.localStorage.getItem(`${key}_expire`)

  if (expire) {
    let now = parseInt(new Date().getTime() / 1000)
    if (now > expire) {
      this.remove(key)
      return
    }
  }

  try {
    return JSON.parse(content)
  } catch (e) {
    return content
  }
}
/**
 * 根据键删除
 * @param {*} key 键
 */
const remove = function (key) {
  if (!key) {
    return
  }
  window.localStorage.removeItem(key)
  window.localStorage.removeItem(`${key}_expire`)
}
/**
 * 清空
 */
const clear = function () {
  window.localStorage.clear()
}

export default {
  set,
  get,
  remove,
  clear,
  install (Vue) { Vue.prototype.$ls = { set, get, remove, clear } }
}
