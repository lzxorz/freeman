import enquireJs from 'enquire.js'

const enquireScreen = function (call) {
  // tablet
  enquireJs.register('screen and (max-width: 1087.99px)', {
    match: function () {
      call && call(1)
    },
    unmatch: function () {
      call && call(-1)
    }
  })

  // mobile
  enquireJs.register('only screen and (max-width: 767.99px)', {
    match: () => {
      call && call(2)
    }
  })
}

export default enquireScreen
