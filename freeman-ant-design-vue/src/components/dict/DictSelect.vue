<template>
  <a-select :placeholder="placeholder" :value="localValue" @change="handleChange" :mode="mode" :disabled="disabled">
    <a-select-option v-for="(item, index) in dictOptions" :key="index" :value="item.value">
      <span style="display: inline-block;width: 100%" :title="item.label">
        {{item.label}}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
import {initDictOptions} from './DictSelectUtil'

export default {
  name: 'DictSelect',
  props: {
    mode: {
      type: String,
      default: 'default'
    },
    dictType: String,
    placeholder: String,
    disabled: {
      type: Boolean,
      default: false
    },
    options: Array,
    value: String
  },
  data () {
    return {
      dictOptions: []
    }
  },
  computed: {
    localValue () {
      return !this.value ? [] : this.value.split(',')
    }
  },
  created () {
    if (this.options && this.options.length > 0) {
      this.dictOptions = [...this.options]
      return
    }
    // 获取字典数据
    initDictOptions(this.dictType).then(r => {
      while (!r.status) { }
      this.dictOptions = r.data.data
    })
  },
  methods: {
    handleChange (val) { this.$emit('change', val.join(',')) }
  },
  model: { prop: 'value', event: 'change' }
}
</script>
