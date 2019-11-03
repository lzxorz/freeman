import Vue from 'vue'
import Router from 'vue-router'
import MenuView from '@/views/common/MenuView'
import PageView from '@/views/common/PageView'
import LoginView from '@/views/login/Common'
import EmptyPageView from '@/views/common/EmptyPageView'
import HomePageView from '@/views/HomePage'
import ls from '@utils/localstorage'
import { $getUserRouter } from '@/api'

Vue.use(Router)

let routes = [
  { path: '/index', name: '首页', redirect: '/home' },
  { path: '/login', name: '登录页', component: LoginView }
]

let router = new Router({ routes })

const whiteList = ['/login']

let asyncRouter

// 导航守卫，渲染动态路由
router.beforeEach((to, from, next) => {
  if (whiteList.includes(to.path)) {
    next()
  }
  // debugger
  let user = ls.get('USER')
  let userRouter = ls.get('USER_ROUTER')
  if (ls.get('ACCESS_TOKEN') && user) {
    if (!asyncRouter) {
      if (!userRouter) {
        $getUserRouter({}, {userId: `${user.userId}`}).then((res) => {
          asyncRouter = res.data
          ls.set('USER_ROUTER', asyncRouter)
          go(to, next)
        })
      } else {
        asyncRouter = userRouter
        go(to, next)
      }
    } else {
      next()
    }
  } else {
    next('/login')
    /* next({
      path: '/login',
      query: {redirect: from.fullPath} // 将跳转的路由path作为参数，登录成功后跳转到该路由
    }) */
  }
})

/* router.afterEach(() => {
}) */

function go (to, next) {
  asyncRouter = filterAsyncRouter(asyncRouter)
  router.addRoutes(asyncRouter)
  next({ ...to, replace: true })
}

function filterAsyncRouter (routes) {
  return routes.filter((route) => {
    let component = route.component
    if (component) {
      switch (route.component) {
        case 'MenuView':
          route.component = MenuView
          break
        case 'PageView':
          route.component = PageView
          break
        case 'EmptyPageView':
          route.component = EmptyPageView
          break
        case 'HomePageView':
          route.component = HomePageView
          break
        default:
          route.component = view(component)
      }
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children)
      }
      return true
    }
  })
}

function view (path) {
  return function (resolve) {
    import(`@/views/${path}.vue`).then(mod => {
      resolve(mod)
    })
  }
}

export default router
