<template>
  <a-drawer title="修改按钮" :maskClosable="false" width=650 placement="right"
    :closable="false" @close="onClose" :visible="orgEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='部门名称' v-bind="formItemLayout">
        <a-input v-decorator="['name', {rules: [ { required: true, message: '部门名称不能为空'}, { max: 20, message: '长度不能超过20个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='机构类型' v-bind="formItemLayout">
         <dict-radio v-decorator="['type',{rules: [{ required: true, message: '请选择机构类型' }]}]" placeholder="请选择机构类型" dictType="sys_org_type" />
      </a-form-item>
      <a-form-item label='部门排序' v-bind="formItemLayout">
        <a-input-number v-decorator="['sortNo']" style="width: 100%"/>
      </a-form-item>
      <a-form-item label='上级部门' style="margin-bottom: 2rem" v-bind="formItemLayout">
        <a-tree :key="treeKey" :checkable="true" :checkStrictly="true"
          @check="handleCheck" @expand="handleExpand" :expandedKeys="expandedKeys"
          :defaultCheckedKeys="defaultCheckedKeys" :treeData="treeData">
        </a-tree>
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
import {$getOrgs, $editOrg} from '@/api'
import DictRadio from '@/components/dict/DictRadio'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'OrgEdit',
  props: {
    orgEditVisiable: {
      default: false
    }
  },
  components: {DictRadio},
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      treeKey: +new Date(),
      org: {},
      checkedKeys: [],
      expandedKeys: [],
      defaultCheckedKeys: [],
      treeData: []
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.treeKey = +new Date()
      this.expandedKeys = this.checkedKeys = this.defaultCheckedKeys = []
      this.button = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    setFormValues ({...org}) {
      this.form.getFieldDecorator('name')
      this.form.setFieldsValue({'name': org.label})
      this.form.getFieldDecorator('type')
      this.form.setFieldsValue({'type': org.type})
      this.form.getFieldDecorator('sortNo')
      this.form.setFieldsValue({'sortNo': org.sortNo})
      if (org.parentId !== '0') {
        this.defaultCheckedKeys.push(org.parentId)
        this.checkedKeys = this.defaultCheckedKeys
        this.expandedKeys = this.checkedKeys
      }
      this.org.id = org.id
    },
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length > 1) {
        this.$message.error('最多只能选择一个上级部门，请修改')
        return
      }
      if (checkedArr[0] === this.org.id) {
        this.$message.error('不能选择自己作为上级部门，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let org = this.form.getFieldsValue()
          org.parentId = checkedArr[0]
          if (Object.is(org.parentId, undefined)) {
            org.parentId = 0
          }
          org.id = this.org.id
          $editOrg({ ...org }).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    orgEditVisiable () {
      if (this.orgEditVisiable) {
        $getOrgs().then((r) => {
          this.treeData = r.data.data.children
          this.treeKey = +new Date()
        })
      }
    }
  }
}
</script>
