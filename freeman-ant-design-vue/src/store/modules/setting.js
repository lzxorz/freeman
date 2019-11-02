import ls from '@utils/localstorage'

export default {
  namespaced: true,
  state: {
    sidebar: {
      opened: true
    },
    settingBar: {
      opened: false
    },
    device: 'desktop',
    isMobile: false,
    theme: ls.get('THEME') || 'light',
    layout: ls.get('LAYOUT') || 'side',
    multipage: getBooleanValue(ls.get('MULTIPAGE'), true),
    fixSiderbar: getBooleanValue(ls.get('FIX_SIDERBAR'), true),
    fixHeader: getBooleanValue(ls.get('FIX_HEADER'), true),
    systemName: 'Freeman 管理系统',
    copyright: `${new Date().getFullYear()} <a href="https://www.baicu.com" target="_blank">Baidu</a>`,
    colorList: [
      'rgb(245, 34, 45)',
      'rgb(250, 84, 28)',
      'rgb(250, 173, 20)',
      'rgb(66, 185, 131)',
      'rgb(82, 196, 26)',
      'rgb(24, 144, 255)',
      'rgb(47, 84, 235)',
      'rgb(114, 46, 209)'
    ],
    color: ls.get('COLOR') || 'rgb(24, 144, 255)'
  },
  mutations: {
    setTheme (state, theme) {
      ls.set('THEME', theme)
      state.theme = theme
    },
    setLayout (state, layout) {
      ls.set('LAYOUT', layout)
      state.layout = layout
    },
    setMultipage (state, multipage) {
      ls.set('MULTIPAGE', multipage)
      state.multipage = multipage
    },
    setDevice (state, device) {
      state.device = device
      state.isMobile = device === 'mobile'
      state.sidebar.opened = device !== 'mobile'
    },
    setSidebar (state, type) {
      state.sidebar.opened = type
    },
    setFixSiderbar (state, flag) {
      ls.set('FIX_SIDERBAR', flag)
      state.fixSiderbar = flag
    },
    setFixHeader (state, flag) {
      ls.set('FIX_HEADER', flag)
      state.fixHeader = flag
    },
    setSettingBar (state, flag) {
      state.settingBar.opened = flag
    },
    setColor (state, color) {
      ls.set('COLOR', color)
      state.color = color
    }
  }
}

function getBooleanValue (value, defaultValue) {
  /* if (Object.is(value, null) || Object.is(value, undefined)) {
    return defaultValue
  } */
  return ['true', 'false'].includes(JSON.stringify(value)) ? value : defaultValue
}
