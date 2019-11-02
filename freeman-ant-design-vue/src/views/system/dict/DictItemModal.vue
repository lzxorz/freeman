<template>
  <a-modal :title='title' :width='800' :visible='visible' :confirmLoading='confirmLoading'
    @ok='handleOk' @cancel='handleCancel' cancelText='关闭' >
    <a-spin :spinning='confirmLoading'>
      <a-form :form='form'>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='显示文字'>
          <a-input placeholder='请输入显示文字' v-decorator="['label', validatorRules.label]" />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='数据值'>
          <a-input placeholder='请输入数据值' v-decorator="['value', validatorRules.value]" />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='备注'>
          <a-input v-decorator="['remark']" />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='排序值'>
          <a-input-number :min='1' v-decorator="['sortNo',{'initialValue':1}]" />值越小越靠前，支持小数
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='状态'>
          <a-radio-group v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
            <a-radio value="0">停用</a-radio>
            <a-radio value="1">启用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
// import pick from 'lodash.pick'
import { $addDictItem, $editDictItem } from '@/api'

export default {
  name: 'DictItemModal',
  data () {
    return {
      title: '操作',
      visible: false,
      id: '',
      type: '',
      status: 1,
      labelCol: { xs: { span: 24 }, sm: { span: 5 } },
      wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        label: { rules: [{ required: true, message: '请输入显示文字!' }] },
        value: { rules: [{ required: true, message: '请输入数据值!' }] }
      }
    }
  },
  created () {},
  methods: {
    add (type) {
      this.id = ''
      this.type = type
      this.form.resetFields()
    },
    edit (record) {
      this.form.getFieldDecorator('label')
      this.form.setFieldsValue({'label': record.label})
      this.form.getFieldDecorator('value')
      this.form.setFieldsValue({'value': record.value})
      this.form.getFieldDecorator('remark')
      this.form.setFieldsValue({'remark': record.remark})
      this.form.getFieldDecorator('sortNo')
      this.form.setFieldsValue({'sortNo': record.sortNo})
      this.form.getFieldDecorator('status')
      this.form.setFieldsValue({'status': record.status})
      this.id = record.id
      this.type = record.type
      this.visible = true
    },
    // 确定
    handleOk () {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let dictItem = this.form.getFieldsValue()
          dictItem.label = (dictItem.label || '').trim()
          dictItem.value = (dictItem.value || '').trim()
          dictItem.remark = (dictItem.remark || '').trim()
          dictItem.id = this.id
          dictItem.type = this.type
          let obj = !this.id ? $addDictItem(dictItem) : $editDictItem(dictItem)
          obj.then(res => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
            that.close()
          })
        }
      })
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.id = this.type = ''
      this.visible = false
    }
  }
}
</script>
