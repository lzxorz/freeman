import ls from '@utils/localstorage'
// import { $login, $phoneLogin, $logout } from '@/api'
import { $login, $logout } from '@/api'
// import { welcome } from '../../utils/util'

export default {
  namespaced: true,
  state: {
    accessToken: ls.get('ACCESS_TOKEN'),
    expireTime: ls.get('EXPIRE_TIME'),
    user: ls.get('USER'),
    permissions: ls.get('PERMISSIONS'),
    roles: ls.get('ROLES')
  },
  mutations: {
    setAccessToken (state, val) {
      state.accessToken = val
      if (val) {
        ls.set('ACCESS_TOKEN', val)
      } else {
        ls.remove('ACCESS_TOKEN')
      }
    },
    setUser (state, val) {
      ls.set('USER', val)
      state.user = val
    },
    setRoles (state, val) {
      ls.set('ROLES', val)
      state.roles = val
    },
    setPermissions (state, val) {
      ls.set('PERMISSIONS', val)
      state.permissions = val
    }
  },
  actions: {
    // 登录
    Login ({ commit }, loginInfo) {
      $login(loginInfo).then(r => {
        let user = r.data.data
        let config = user.config
        commit('setAccessToken', r.headers.authorization)
        commit('setRoles', user.roleNames.split(','))
        commit('setPermissions', user.permissions)
        commit('setUser', user)
        commit('setting/setTheme', config.theme, {root: true})
        commit('setting/setColor', config.color, {root: true})
        commit('setting/setLayout', config.layout, {root: true})
        commit('setting/setMultipage', config.multiPage === '1', {root: true})
        commit('setting/setFixHeader', config.fixHeader === '1', {root: true})
        commit('setting/setFixSiderbar', config.fixSiderbar === '1', {root: true})
        setTimeout(() => { window.location.href = '/' }, 1500)
      }).catch(error => {
        console.log('登录出错了...', error)
      })
    },
    // 手机号登录
    PhoneLogin ({ commit }, userInfo) {
      /* phoneLogin(userInfo).then(r => {
      }).catch(error => {
        console.log(error)
      }) */
    },
    // 获取用户权限信息
    /* GetPermissionList ({ commit }) {
      return new Promise((resolve, reject) => {
        let vToken = ls.get('ACCESS_TOKEN')
        let params = {token: vToken}
        queryPermissionsByUser(params).then(response => {
          const menuData = response.result.menu
          const authData = response.result.auth
          const allAuthData = response.result.allAuth
          // ls.set('USER_AUTH',authData);
          sessionStorage.setItem('USER_AUTH', JSON.stringify(authData))
          sessionStorage.setItem('SYS_BUTTON_AUTH', JSON.stringify(allAuthData))
          if (menuData && menuData.length > 0) {
            commit('SET_PERMISSIONLIST', menuData)
          } else {
            // eslint-disable-next-line prefer-promise-reject-errors
            reject('getPermissionList: permissions must be a non-null array !')
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    }, */

    // 登出
    Logout ({ commit, state }) {
      // let token = state.token
      // console.log('token: '+ token)
      $logout().then(() => {
        commit('setAccessToken', '')
        commit('setPermissions', [])
        window.location.reload()
      }).catch(() => {
        commit('setAccessToken', '')
        commit('setPermissions', [])
        window.location.reload()
      })
    }

  }
}
