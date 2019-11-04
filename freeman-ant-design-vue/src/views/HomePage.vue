<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'not-menu-page', 'home-page']">
    <a-row :gutter="8" class="head-info">
      <a-card class="head-info-card">
        <a-col :span="12">
          <div class="head-info-avatar">
            <img alt="头像" :src="avatar">
          </div>
          <div class="head-info-count">
            <div class="head-info-welcome">
              {{welcomeMessage}}
            </div>
            <div class="head-info-desc">
              <p>{{user.deptName ? user.deptName : '暂无部门'}} | {{user.roleNames ? user.roleNames : '暂无角色'}}</p>
            </div>
            <div class="head-info-time">上次登录时间：{{user.lastLoginTime ? user.lastLoginTime : '第一次访问系统'}}</div>
          </div>
        </a-col>
        <a-col :span="12">
          <div>
            <a-row class="more-info">
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4">
                <head-info title="今日IP" :content="todayIp" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="今日访问" :content="todayVisitCount" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="总访问量" :content="totalVisitCount" :center="false" />
              </a-col>
            </a-row>
          </div>
        </a-col>
      </a-card>
    </a-row>
    <a-row :gutter="8" class="count-info">
      <a-col :span="12" class="visit-count-wrapper">
        <a-card class="visit-count">
          <apexchart ref="count" type=bar height=300 :options="visitCountOptions" :series="visitCountSeries" />
        </a-card>
      </a-col>
      <a-col :span="12" class="project-wrapper">
        <a-card title="进行中的项目" class="project-card">
          <a href="https://gitee.com/lzxorz/freeman" target="_blank" slot="extra">所有项目</a>
          <table>
            <tr>
              <td>
                <div class="project-avatar-wrapper">
                  <a-avatar class="project-avatar">{{projects[0].avatar}}</a-avatar>
                </div>
                <div class="project-detail">
                  <div class="project-name">
                    {{projects[0].name}}
                  </div>
                  <div class="project-desc">
                    <p>{{projects[0].des}}</p>
                  </div>
                </div>
              </td>
              <td>
                <div class="project-avatar-wrapper">
                  <a-avatar class="project-avatar">{{projects[1].avatar}}</a-avatar>
                </div>
                <div class="project-detail">
                  <div class="project-name">
                    {{projects[1].name}}
                  </div>
                  <div class="project-desc">
                    <p>{{projects[1].des}}</p>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td>
                <div class="project-avatar-wrapper">
                  <a-avatar class="project-avatar">{{projects[2].avatar}}</a-avatar>
                </div>
                <div class="project-detail">
                  <div class="project-name">
                    {{projects[2].name}}
                  </div>
                  <div class="project-desc">
                    <p>{{projects[2].des}}</p>
                  </div>
                </div>
              </td>
              <td>
                <div class="project-avatar-wrapper">
                  <a-avatar class="project-avatar">{{projects[3].avatar}}</a-avatar>
                </div>
                <div class="project-detail">
                  <div class="project-name">
                    {{projects[3].name}}
                  </div>
                  <div class="project-desc">
                    <p>{{projects[3].des}}</p>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td>
                <div class="project-avatar-wrapper">
                  <a-avatar class="project-avatar">{{projects[4].avatar}}</a-avatar>
                </div>
                <div class="project-detail">
                  <div class="project-name">
                    {{projects[4].name}}
                  </div>
                  <div class="project-desc">
                    <p>{{projects[4].des}}</p>
                  </div>
                </div>
              </td>
              <td></td>
            </tr>
          </table>
        </a-card>
      </a-col>
    </a-row>
    <a-row class="bubble-info">
     <!--  <apexchart type=donut width=380 :options="pieOptions" :series="pieSeries" /> -->
     <a-col :span="24" class="bubble-wrapper">
        <a-card class="bubble">
          <apexchart type=bubble height=350 :options="bubbleOptions" :series="bubbleSeries" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import HeadInfo from '@/views/common/HeadInfo'
import {mapState} from 'vuex'
import moment from 'moment'
import {welcome} from '@/utils/util'
import {index} from '@/api'
moment.locale('zh-cn')

// bubble
function generateData (baseval, count, yrange) {
  var i = 0
  var series = []
  while (i < count) {
    var x = Math.floor(Math.random() * (750 - 1 + 1)) + 1
    var y = Math.floor(Math.random() * (yrange.max - yrange.min + 1)) + yrange.min
    var z = Math.floor(Math.random() * (75 - 15 + 1)) + 15

    series.push([x, y, z])
    baseval += 86400000
    i++
  }
  return series
}

export default {
  name: 'HomePage',
  components: {HeadInfo},
  data () {
    return {
      pieSeries: [44, 55, 41, 17, 15],
      pieOptions: {
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      },
      bubbleSeries: [
        { name: 'Bubble1', data: generateData(new Date('11 Feb 2017 GMT').getTime(), 20, { min: 10, max: 60 }) },
        { name: 'Bubble2', data: generateData(new Date('11 Feb 2017 GMT').getTime(), 20, { min: 10, max: 60 }) },
        { name: 'Bubble3', data: generateData(new Date('11 Feb 2017 GMT').getTime(), 20, { min: 10, max: 60 }) },
        { name: 'Bubble4', data: generateData(new Date('11 Feb 2017 GMT').getTime(), 20, { min: 10, max: 60 }) }
      ],
      bubbleOptions: {
        dataLabels: {enabled: false},
        fill: {opacity: 0.8},
        title: {text: 'Simple Bubble Chart'},
        xaxis: {tickAmount: 12, type: 'category'},
        yaxis: {max: 70}
      },
      visitCountSeries: [],
      visitCountOptions: {
        chart: {
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '35%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        fill: {
          opacity: 1

        }
      },
      projects: [
        { name: '项目一', des: '项目一项目一项目一项目一项目一项目一', avatar: 'A' },
        { name: '项目二', des: '项目二项目二项目二项目二项目二项目二', avatar: 'B' },
        { name: '项目三', des: '项目三项目三项目三项目三项目三项目三', avatar: 'C' },
        { name: '项目四', des: '项目四项目四项目四项目四项目四项目四项目四项目四', avatar: 'D' },
        { name: '项目五', des: '项目五项目五项目五项目五项目五项目五项目五项目五项目五项目五', avatar: 'E' },
        { name: '项目六', des: '项目六项目六项目六项目六项目六项目六项目六项目六项目六项目六', avatar: 'F' },
        { name: '项目七', des: '项目七项目七项目七项目七项目七项目七项目七项目七项目七项目七', avatar: 'F' },
        { name: '项目八', des: '项目八项目八项目八项目八项目八项目八项目八项目八项目八项目八项目八', avatar: 'F' },
        { name: '项目九', des: '项目九项目九项目九项目九项目九项目九项目九项目九项目九项目九项目九', avatar: 'F' }
      ],
      todayIp: '',
      todayVisitCount: '',
      totalVisitCount: '',
      userRole: '',
      userDept: '',
      lastLoginTime: '',
      welcomeMessage: ''
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    }),
    avatar () {
      return `avatar/${this.user.avatar}`
    }
  },
  methods: {
  },
  mounted () {
    this.welcomeMessage = welcome(`${this.user.username}`)
    index({}, {userId: `${this.user.id}`}).then((r) => {
      let data = r.data.data
      this.todayIp = data.todayIp
      this.todayVisitCount = data.todayVisitCount
      this.totalVisitCount = data.totalVisitCount
      let sevenVisitCount = []
      let dateArr = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenVisitCount) {
          if (o.days === time) {
            contain = true
            sevenVisitCount.push(o.count)
          }
        }
        if (!contain) {
          sevenVisitCount.push(0)
        }
        dateArr.push(time)
      }
      let sevenUserVistCount = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenUserVisitCount) {
          if (o.days === time) {
            contain = true
            sevenUserVistCount.push(o.count)
          }
        }
        if (!contain) {
          sevenUserVistCount.push(0)
        }
      }
      this.$refs.count.updateSeries([
        {
          name: '您',
          data: sevenUserVistCount
        },
        {
          name: '总数',
          data: sevenVisitCount
        }
      ], true)
      this.$refs.count.updateOptions({
        xaxis: {
          categories: dateArr
        },
        title: {
          text: '近七日系统访问记录',
          align: 'left'
        }
      }, true, true)
    }).catch((r) => {
      console.error(r)
      this.$message.error('获取首页信息失败')
    })
  }
}
</script>
<style lang="less">
  .home-page {
    .head-info {
      margin-bottom: .5rem;
      .head-info-card {
        padding: .5rem;
        border-color: #f1f1f1;
        .head-info-avatar {
          display: inline-block;
          float: left;
          margin-right: 1rem;
          img {
            width: 5rem;
            border-radius: 2px;
          }
        }
        .head-info-count {
          display: inline-block;
          float: left;
          .head-info-welcome {
            font-size: 1.05rem;
            margin-bottom: .1rem;
          }
          .head-info-desc {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
            p {
              margin-bottom: 0;
            }
          }
          .head-info-time {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
          }
        }
      }
    }
    .bubble-info {
      margin-top: 10px;
      .visit-count-wrapper {
        padding-left: 0 !important;
      }
    }
    .count-info {
      .visit-count-wrapper {
        padding-left: 0 !important;
        .visit-count {
          padding: .5rem;
          border-color: #f1f1f1;
          .ant-card-body {
            padding: .5rem 1rem !important;
          }
        }
      }
      .project-wrapper {
        padding-right: 0 !important;
        .project-card {
          border: none !important;
          .ant-card-head {
            border-left: 1px solid #f1f1f1 !important;
            border-top: 1px solid #f1f1f1 !important;
            border-right: 1px solid #f1f1f1 !important;
          }
          .ant-card-body {
            padding: 0 !important;
            table {
              width: 100%;
              td {
                width: 50%;
                border: 1px solid #f1f1f1;
                padding: .6rem;
                .project-avatar-wrapper {
                  display:inline-block;
                  float:left;
                  margin-right:.7rem;
                  .project-avatar {
                    color: #42b983;
                    background-color: #d6f8b8;
                  }
                }
              }
            }
          }
          .project-detail {
            display:inline-block;
            float:left;
            text-align:left;
            width: 78%;
            .project-name {
              font-size:.9rem;
              margin-top:-2px;
              font-weight:600;
            }
            .project-desc {
              color:rgba(0, 0, 0, 0.45);
              p {
                margin-bottom:0;
                font-size:.6rem;
                white-space:normal;
              }
            }
          }
        }
      }
    }
  }
</style>
