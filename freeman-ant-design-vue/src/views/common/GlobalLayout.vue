<template>
  <a-layout>
    <drawer v-if="isMobile" :openDrawer="collapsed" @change="onDrawerChange">
      <sider-menu :theme="theme" :menuData="menuData" :collapsed="true" :collapsible="false" @menuSelect="onMenuSelect"/>
    </drawer>
    <sider-menu :theme="theme" v-else-if="layout === 'side'" :menuData="menuData" :collapsed="collapsed" :collapsible="true" />
    <drawer :open-drawer="settingBar" placement="right">
      <setting />
    </drawer>
    <a-layout :style="{ paddingLeft: paddingLeft }">
      <global-header :menuData="menuData" :collapsed="collapsed" @toggleCollapse="toggleCollapse"/>
      <a-layout-content :style="{minHeight: minHeight, margin: '20px 14px 0'}" :class="fixHeader ? 'fixed-header-content' : null">
        <slot></slot>
      </a-layout-content>
      <a-layout-footer style="padding: .29rem 0" class="copyright">
        <global-footer :copyright="copyright"/>
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script>
import GlobalHeader from './GlobalHeader'
import GlobalFooter from './GlobalFooter'
import Drawer from '@c/tool/Drawer'
import SiderMenu from '@c/menu/SiderMenu'
import Setting from '@c/setting/Setting'
import { mapState, mapMutations } from 'vuex'
import { triggerWindowResizeEvent } from '@utils/util'

const minHeight = window.innerHeight - 64 - 24 - 66

let menuData = []

export default {
  name: 'GlobalLayout',
  components: {Setting, SiderMenu, Drawer, GlobalFooter, GlobalHeader},
  data () {
    return {
      minHeight: minHeight + 'px',
      collapsed: false,
      menuData: menuData
    }
  },
  computed: {
    paddingLeft () {
      return this.fixSiderbar && this.layout === 'side' && !this.isMobile ? `${this.sidebarOpened ? 256 : 80}px` : '0'
    },
    ...mapState('setting', {
      sidebarOpened: state => state.sidebar.opened,
      isMobile: state => state.isMobile,
      theme: state => state.theme,
      layout: state => state.layout,
      copyright: state => state.copyright,
      fixSiderbar: state => state.fixSiderbar,
      fixHeader: state => state.fixHeader,
      settingBar: state => state.settingBar.opened
    })
  },
  methods: {
    ...mapMutations({setSidebar: 'setting/setSidebar'}),
    toggleCollapse () {
      this.collapsed = !this.collapsed
      this.setSidebar(!this.collapsed)
      triggerWindowResizeEvent()
    },
    onDrawerChange (show) {
      this.collapsed = show
    },
    onMenuSelect () {
      this.toggleCollapse()
    }
  },
  beforeCreate () {
    let routers = this.$ls.get('USER_ROUTER')
    menuData = routers.find((item) => item.path === '/').children.filter((menu) => {
      let meta = menu.meta
      if (meta.isShow === null || meta.isShow === undefined) {
        return true
      } else {
        return meta.isShow
      }
    })
  }
}
</script>

<style lang="less" scoped>
  .setting{
    background-color: #1890ff;
    color: #fff;
    border-radius: 5px 0 0 5px;
    line-height: 40px;
    font-size: 22px;
    width: 40px;
    height: 40px;
    box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
  }
  .fixed-header-content {
    margin: 76px 12px 0 !important;
  }
</style>
