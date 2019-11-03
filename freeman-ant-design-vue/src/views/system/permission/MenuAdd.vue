<template>
  <a-drawer title="新增菜单" :maskClosable="false" width=650 placement="right" :closable="false" @close="onClose"
    :visible="menuAddVisiable" style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='菜单名称' v-bind="formItemLayout">
        <a-input v-decorator="['name',
                   {rules: [ { required: true, message: '菜单名称不能为空'}, { max: 10, message: '长度不能超过10个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='菜单URL' v-bind="formItemLayout">
        <a-input v-decorator="['path', {rules: [ { required: true, message: '菜单URL不能为空'}, { max: 50, message: '长度不能超过50个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='组件地址' v-bind="formItemLayout">
        <a-input v-decorator="['component',
                 {rules: [ { required: true, message: '组件地址不能为空'}, { max: 100, message: '长度不能超过100个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='相关权限' v-bind="formItemLayout">
        <a-input v-decorator="['perms', {rules: [ { max: 50, message: '长度不能超过50个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='菜单图标' v-bind="formItemLayout">
        <icons v-decorator="['icon', {}]" />
      </a-form-item>
      <a-form-item label='菜单排序' v-bind="formItemLayout">
        <a-input-number v-decorator="['sortNo', {}]" style="width: 100%"/>
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='上级菜单' style="margin-bottom: 2rem" v-bind="formItemLayout">
        <perms-tree ref="ptree" v-decorator="['parentId']" />
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-dropdown style="float: left" :trigger="['click']" placement="topCenter">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="expandAll">展开所有</a-menu-item>
          <a-menu-item key="2" @click="closeAll">合并所有</a-menu-item>
        </a-menu>
        <a-button> 树操作 <a-icon type="up" /> </a-button>
      </a-dropdown>
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
import Icons from '@/components/icon/Icons'
// import {$getPermissions, $addPermission} from '@/api'
import {$addPermission} from '@/api'
import PermsTree from './PermsTree'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'MenuAdd',
  components: {Icons, PermsTree},
  props: {
    menuAddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      menu: {}
    }
  },
  created () {
    this.form.getFieldDecorator('parentId')
  },
  methods: {
    reset () {
      this.loading = false
      this.menu = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    expandAll () {
      this.$refs.ptree.expandAll()
    },
    closeAll () {
      this.$refs.ptree.closeAll()
    },
    handleSubmit () {
      let parentId = this.form.getFieldValue('parentId')
      if (parentId.length > 1) {
        this.$message.error('只能选择一个上级菜单，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let menu = this.form.getFieldsValue()
          menu.parentId = parentId ? parentId[0] : ''
          // 1 菜单 2 按钮
          menu.type = '1'
          $addPermission({...menu}).then(() => {
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
    menuAddVisiable () {
      if (this.menuAddVisiable) {
        this.$refs.ptree.initData('1')
      }
    }
  }
}
</script>
