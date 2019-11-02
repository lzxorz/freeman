<template>
  <a-tree :treeData="treeData" :key="treeKey" :defaultCheckedKeys="checkedKeys" :checkable="true" :checkStrictly="true"
  @check="handleCheck" @expand="handleExpand" :expandedKeys="expandedKeys"> </a-tree>
</template>
<script>
import {$getPermissions} from '@/api'

export default {
  name: 'PermsTree',
  props: {value:  ''},
  data () {
    return {
      treeKey: +new Date(),
      treeData: [],
      allTreeKeys: [],
      expandedKeys: [],
      checkedKeys: []
    }
  },
  /* computed: {
    checkedKeys () {
      this.$message.success(JSON.stringify(this.value))
      return this.value ? this.value : []
    }
  }, */
  created () {},
  methods: {
    initData (type) {
      this.treeKey = +new Date()
      this.treeData = this.allTreeKeys = this.expandedKeys = []

      let params = type === '1' ? {type: '1'} : {}
      $getPermissions({...params}).then((r) => {
        this.treeData = r.data.data.children
        this.allTreeKeys = r.data.data.ids
        this.checkedKeys = Object.is(this.value, undefined) ? [] : this.value
        // console.log('init==checkedKeys==>', JSON.stringify(this.checkedKeys))
      })
    },
    handleCheck (checkedKeys) {
      let arr = Object.is(checkedKeys, undefined) ? [] : checkedKeys.checked
      this.checkedKeys = arr
      // alert(JSON.stringify(arr))
      this.$emit('change', arr)
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    expandAll () {
      this.handleExpand(this.allTreeKeys)
    },
    closeAll () {
      this.handleExpand([])
    }
  },
  /* watch: {
    checkedKeys () {
      console.log('watch==checkedKeys==>',JSON.stringify(this.checkedKeys))
    }
  }, */
  model: { prop: 'value', event: 'change' }
}
</script>
