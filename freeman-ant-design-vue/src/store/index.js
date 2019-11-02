// 引入Vuex（前提是已经用Vue脚手架工具构建好项目）

// 1、利用npm包管理工具,进行安装 vuex 在控制命令行中输入下边的命令就可以了
// npm install vuex S
// 如果是用的yarn
// yarn add vuex -S

// 2、src目录下新建一个store文件夹(这个不是必须的),并在文件夹下新建index.js文件(这个可以不一样)，文件中引入我们的vue和vuex,并Vue.use(Vuex)

import Vue from 'vue'
import Vuex from 'vuex'
// import a from './modules/a'
import account from './modules/account'
import setting from './modules/setting'

Vue.use(Vuex)

export default new Vuex.Store({
  // vuex中的全局共享数据源，我们需要保存的数据就保存在这里
  // 可以在页面通过 this.$store.state来获取我们定义的数据
  // state: {
  //   count: 1
  // },
  // Getter相当于vue中的computed计算属性
  // getter 的返回值会根据它的依赖被缓存起来，且只有当它的依赖值发生了改变才会被重新计算，这里我们可以通过定义vuex的Getter来获取，Getters 可以用于监听、state中的值的变化，返回计算后的结果.
  // getters: {
  //   getCount (state) {
  //     return state.count + 1
  //   }
  // },
  // Mutations：需要修改store中的值唯一的方法就是提交mutation来修改
  // 在我们定义的组件中, 可以使用 $store.commit('add'), 直接提交mutations中的方法修改值
  // <button @click="$store.commit('add')">+</button>
  // <button @click="$store.commit('reduce')">-</button>
  // mutations: {
  //   add (state) {
  //     state.count += 1
  //   },
  //   reduce (state) {
  //     state.count -= 1
  //   }
  // },
  // Actions：官方不建议直接提交mutation去修改store里面的值，而是让我们去提交一个actions异步修改，在actions中提交mutation再去修改状态值
  // 在我们定义的组件中, 添加方法
  // methods:{
  //   addFun(){
  //       // 参数‘add'是mutation中的方法名 // 官方不建议直接提交mutation去修改store里面的值
  //       // this.$store.commit('add');
  //
  //       // 参数‘addFun'是action中的方法名  提交一个actions，在actions中提交mutation再去修改状态值
  //       this.$store.dispatch('addFun');
  //   },
  //   reduceFun(){
  //       // 参数‘reduce'是mutation中的方法名  官方不建议直接提交mutation去修改store里面的值
  //       // this.$store.commit('reduce');
  //
  //       // 参数‘reduceFun'是action中的方法名  提交一个actions，在actions中提交mutation再去修改状态值
  //       this.$store.dispatch('reduceFun');
  //   }
  // }
  // actions: { // ontext：上下文对象，这里可以理解称store本身
  //   addFun (context) {
  //     context.commit('add')
  //   },
  //   reduceFun (context) {
  //     context.commit('reduction')
  //   }
  // },
  // 如果我们需要指定加减的数值，那么我们可以传入dispatch中的第二个参数，然后在actions中的对应函数中接受参数传递给mutations中的函数进行计算
  // 在我们定义的组件中, 修改方法中的 this.$store.dispatch(‘addFun’,10086); // 多传一个参数
  // 在actions中修改, 做对应的修改
  //    reductionFun(context,num){  // 多接收一个参数
  //      context.commit(’reduction‘,num); // 把参数传递给mutations中的函数
  //    }

  // 如果我们不喜欢这种在页面上使用“this.$stroe.state.count”和“this.$store.dispatch('funName')”这种很长的写法，那么我们可以使用mapState、mapGetters、mapActions就不会这么麻烦了；
  // 在我们定义的组件中修改：
  //  //...省略...<script>
  //  import {mapState,mapAction,mapGetters} from 'vuex';
  //  //...省略...
  //  computed:{
  //      ...mapState({
  //          count：state=>state.count
  //          xxxx: state=>state.xxxx
  //      })
  //  },
  //  methods: {
  //  //...省略...

  // module模块组
  // 随着项目的复杂性增加，我们共享的状态越来越多，这时候我们就需要把我们状态的各种操作进行一个分组，分组后再进行按组编写
  modules: {
    // a, // 在我们定义的组件中使用时多了a一级 this.$store.a.xxx
    account,
    setting
  }
})

// 在main.js中实例化Vue对象时加入 store 对象
// import store from './store'
// ...省略...
// new Vue({
//   ...省略...
//   store,
//   ...省略...
// })

// 我们自己定义的组件,不完整示例片段:
// <template>
//   <p>
//     <h3>{{$store.state.count}}</h3>
//     <hr>
//     <button @click="$store.commit('add')">+</button>
//     <button @click="$store.commit('reduce')">-</button>
//   </p>
// </template>
// <script>
// import store from '@/store'
// export default{
//  data () {
//   return{
//    msg:'Hello Vuex',
//   }
//  },
//  store
//
// }
//
// </script>
