<template>
  <a-radio-group @change="handleChange" :value="value" :disabled="disabled">
    <a-radio v-for="(item, key) in dictOptions" :key="key" :value="item.value" :style="radioStyle">{{ item.label }}</a-radio>
  </a-radio-group>
</template>

<script>
import {initDictOptions} from './DictSelectUtil'

export default {
  name: 'DictRadio',
  props: {
    dictType: String,
    placeholder: String,
    disabled: {
      type: Boolean,
      default: false
    },
    radioStyle: Object,
    options: Array,
    value: String
  },
  data () {
    return {
      dictOptions: []
    }
  },
  computed: {},
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
    handleChange (e) {
      this.$emit('change', e.target.value)
    }
  },
  model: { prop: 'value', event: 'change' }
}
</script>
