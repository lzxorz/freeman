<template>
  <a-drawer title="修改字典" :maskClosable="false" width=650 placement="right" :closable="false"
    @close="onClose" :visible="visiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='字典类型' v-bind="formItemLayout">
        <a-input style="width: 100%"  placeholder="请输入字典类型" v-decorator="['type', {rules: [ { required: true, max: 10,message: '不能为空'} ]}]"/>
      </a-form-item>
      <!-- <a-form-item label='显示文字' v-bind="formItemLayout">
        <a-input v-decorator="['label', {rules: [ { required: true, message: '不能为空'}, { max: 20, message: '长度不能超过20个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='数据值' v-bind="formItemLayout">
        <a-input v-decorator="['value', {rules: [ { required: true, message: '不能为空'}, { max: 20, message: '长度不能超过20个字符'} ]}]"/>
      </a-form-item> -->
      <a-form-item label='字典名称' v-bind="formItemLayout">
        <a-input v-decorator="['name', {rules: [ { required: true, message: '不能为空'}, { max: 20, message: '长度不能超过20个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='备注' v-bind="formItemLayout">
        <a-input v-decorator="['remark', {rules: [ { required: true, message: '不能为空'}, { max: 20, message: '长度不能超过20个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='排序' v-bind="formItemLayout">
        <a-input-number v-decorator="['sortNo']" style="width: 100%"/>
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
import {$editDict} from '@/api'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'DictEdit',
  props: {
    visiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      dict: {}
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues ({...dict}) {
      this.dict.id = dict.id
      let fields = ['name', 'type', 'remark', 'sortNo', 'status']
      Object.keys(dict).forEach((key) => {
        if (fields.includes(key)) {
          this.form.getFieldDecorator(key)
          this.form.setFieldsValue({[key]: dict[key]})
        }
      })
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          let dict = this.form.getFieldsValue()
          dict.id = this.dict.id
          $editDict(dict).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
