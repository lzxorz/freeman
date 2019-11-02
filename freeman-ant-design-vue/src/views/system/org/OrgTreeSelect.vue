<template>
  <a-tree-select allowClear :placeholder='placeholder' :treeData="localTreeData" :value="value" @change="handleChange"
    :style="style" :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"
    :treeCheckable="treeCheckable"
    :showSearch="showSearch"
    :treeNodeLabelProp="treeNodeLabelProp"
    :treeNodeFilterProp="treeNodeFilterProp"
    :disabled="disabled" />
</template>

<script>
import {$getOrgs} from '@/api'

export default {
  name: 'OrgTreeSelect',
  props: {
    placeholder: String,
    style: String,
    dropdownStyle: Object,
    treeData: Array,
    value: Number,
    treeCheckable: {
      type: Boolean,
      default: false
    },
    showSearch: {
      type: Boolean,
      default: false
    },
    treeNodeLabelProp: {
      type: String,
      default: 'label'
    },
    treeNodeFilterProp: {
      type: String,
      default: 'label'
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      localTreeData: []
    }
  },
  computed: {},
  created () {
    if (this.treeData && this.treeData.length > 0) {
      this.localTreeData = [...this.treeData]
      return
    }
    $getOrgs().then((r) => {
      this.localTreeData = r.data.data.children
    })
  },
  methods: {
    handleChange (value) { this.$emit('change', value) }
  },
  mounted () { },
  model: { prop: 'value', event: 'change' }
}
</script>
