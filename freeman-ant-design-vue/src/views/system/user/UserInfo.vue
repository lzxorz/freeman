<template>
  <a-modal :title="modalTitle" v-model="visiable" :centered="true" :keyboard="false" :footer="null"
  :width="750" @cancel="$emit('close')">
    <a-layout class="user-info">
      <a-layout-sider class="user-info-side">
        <a-avatar shape="square" :size="115" icon="user" :src="`avatar/${modalData.avatar}`"/>
      </a-layout-sider>
      <a-layout-content class="user-content-one">
        <p><a-icon type="user"/>账户：{{modalData.username}}</p>
        <p :title="modalData.roleNames"><a-icon type="star"/>角色：{{modalData.roleNames? modalData.roleNames: '暂无角色'}}</p>
        <p><a-icon type="skin"/>性别：{{sex}}</p>
        <p><a-icon type="phone"/>电话：{{modalData.phone ? modalData.phone : '暂未绑定电话'}}</p>
        <p><a-icon type="mail"/>邮箱：{{modalData.email ? modalData.email : '暂未绑定邮箱'}}</p>
      </a-layout-content>
      <a-layout-content class="user-content-two">
        <p><a-icon type="home"/>部门：{{modalData.deptName ? modalData.deptName : '暂无部门信息'}}</p>
        <p>
          <a-icon type="smile" v-if="modalData.status === '1'"/>
          <a-icon type="frown" v-else/>状态：
          <template v-if="modalData.status === '0'">
            <a-tag color="red">锁定</a-tag>
          </template>
          <template v-else-if="modalData.status === '1'">
            <a-tag color="cyan">正常</a-tag>
          </template>
          <template v-else>
            {{modalData.status}}
          </template>
        </p>
        <p><a-icon type="clock-circle"/>创建时间：{{modalData.createTime}}</p>
        <p><a-icon type="login" />最近登录：{{modalData.lastLoginTime}}</p>
        <p :title="modalData.description"><a-icon type="message"/>描述：{{modalData.description}}</p>
      </a-layout-content>
    </a-layout>
  </a-modal>
</template>
<script>
import {getDictLabelByValue} from '@/components/dict/DictSelectUtil'

export default {
  name: 'UserInfo',
  props: {
    modalTitle: { type: String, default: '信息' },
    visiable: { require: true, default: false },
    dicts: {}, // 用到的字典
    modalData: { require: true }
  },
  computed: {
    sex () { return getDictLabelByValue(this.dicts['sys_sex'], this.modalData.sex) }
  },
  methods: {
  }
}
</script>
<style lang="less" scoped>
@import "UserInfo";
</style>
