import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import Antd from 'ant-design-vue'
import ls from '@utils/localstorage'
import request from '@utils/request'
import VueApexCharts from 'vue-apexcharts'
import 'ant-design-vue/dist/antd.css'
import '@utils/install'

Vue.config.productionTip = false
Vue.use(Antd)
Vue.use(ls)
Vue.use({ install (Vue) { Vue.prototype.$bus = new Vue() } })
Vue.use(VueApexCharts)
Vue.component('apexchart', VueApexCharts)

Vue.prototype.$get = request.get
Vue.prototype.$post = request.post
Vue.prototype.$put = request.put
Vue.prototype.$delete = request.delete
Vue.prototype.$export = request.export
Vue.prototype.$download = request.download
Vue.prototype.$upload = request.upload

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
