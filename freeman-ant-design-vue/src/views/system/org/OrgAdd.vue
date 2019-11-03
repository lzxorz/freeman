<template>
  <a-drawer
    title="新增部门" :maskClosable="false" width=650 placement="right" :closable="false"
    @close="onClose" :visible="orgAddVisiable"
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
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='上级部门' style="margin-bottom: 2rem" v-bind="formItemLayout">
        <a-tree :key="treeKeys" :checkable="true" :checkStrictly="true" @check="handleCheck" @expand="handleExpand" :expandedKeys="expandedKeys" :treeData="treeData">
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
import {$getOrgs, $addOrg} from '@/api'
import DictRadio from '@/components/dict/DictRadio'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'OrgAdd',
  props: {
    orgAddVisiable: {
      default: false
    }
  },
  components: {DictRadio},
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      checkedKeys: [],
      expandedKeys: [],
      treeData: [],
      treeKeys: +new Date()
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.treeKeys = +new Date()
      this.expandedKeys = this.checkedKeys = []
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleCheck (checkedKeys, e) {
      // function(checkedKeys, e:{checked: bool, checkedNodes, node, event})
      // console.log('check>>', e)
      this.checkedKeys = checkedKeys
      this.form.setFieldValue({'parentId': e.checkedNodes[0].data.props.parentIds || ''})
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length > 1) {
        this.$message.error('最多只能选择一个上级部门，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let org = this.form.getFieldsValue()
          org.parentId = checkedArr.length ? checkedArr[0] : ''
          $addOrg({...org}).then(() => {
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
    orgAddVisiable () {
      if (this.orgAddVisiable) {
        $getOrgs().then((r) => {
          this.treeData = r.data.data.children
        })
      }
    }
  }
}
</script>
