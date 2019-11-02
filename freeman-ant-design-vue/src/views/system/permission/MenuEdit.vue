<template>
  <a-drawer
    title="修改菜单"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="menuEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='菜单名称' v-bind="formItemLayout">
        <a-input v-decorator="['name',
                   {rules: [ { required: true, message: '菜单名称不能为空'}, { max: 10, message: '长度不能超过10个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='菜单URL' v-bind="formItemLayout">
        <a-input v-decorator="['path',
                 {rules: [ { required: true, message: '菜单URL不能为空'}, { max: 50, message: '长度不能超过50个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='组件地址' v-bind="formItemLayout">
        <a-input v-decorator="['component',
                 {rules: [ { required: true, message: '组件地址不能为空'}, { max: 100, message: '长度不能超过100个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='相关权限' v-bind="formItemLayout">
        <a-input v-decorator="['perms', {rules: [ { max: 50, message: '长度不能超过50个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='菜单图标' v-bind="formItemLayout">
        <icons v-decorator="['icon']" />
      </a-form-item>
      <a-form-item label='菜单排序' v-bind="formItemLayout">
        <a-input-number v-decorator="['sortNo']" style="width: 100%"/>
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='上级菜单' style="margin-bottom: 2rem" v-bind="formItemLayout">
        <a-tree
          :treeData="treeData" :key="menuTreeKey" ref="menuTree"
          :checkable="true" :checkStrictly="true" @check="handleCheck"
          @expand="handleExpand" :expandedKeys="expandedKeys"
          :defaultCheckedKeys="defaultCheckedKeys">
        </a-tree>
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
import {$getPermissions, $editPermission} from '@/api'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'MenuEdit',
  components: {Icons},
  props: {
    menuEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      menuTreeKey: +new Date(),
      menu: {},
      checkedKeys: [],
      expandedKeys: [],
      treeData: [],
      defaultCheckedKeys: []
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.menuTreeKey = +new Date()
      this.expandedKeys = this.checkedKeys = this.defaultCheckedKeys = []
      this.menu = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
    },
    expandAll () {
      this.expandedKeys = this.allTreeKeys
    },
    closeAll () {
      this.expandedKeys = []
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    setFormValues ({...menu}) {
      let fields = ['path', 'component', 'perms', 'sortNo', 'icon', 'status']
      Object.keys(menu).forEach((key) => {
        if (fields.includes(key)) {
          this.form.getFieldDecorator(key)
          this.form.setFieldsValue({[key]: menu[key]})
        }
      })
      this.form.getFieldDecorator('name')
      this.form.setFieldsValue({'name': menu.label})
      if (menu.parentId !== '0') {
        this.defaultCheckedKeys.push(menu.parentId)
        this.checkedKeys = this.defaultCheckedKeys
        this.expandedKeys = this.checkedKeys
      }
      this.menu.id = menu.id
      this.menuTreeKey = +new Date()
    },
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length > 1) {
        this.$message.error('最多只能选择一个上级菜单，请修改')
        return
      }
      if (checkedArr[0] === this.menu.id) {
        this.$message.error('不能选择自己作为上级菜单，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let menu = this.form.getFieldsValue()
          menu.id = this.menu.id
          menu.parentId = checkedArr.length ? checkedArr[0] : ''
          // 1 菜单 2 按钮
          menu.type = '1'
          $editPermission({...menu}).then(() => {
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
    menuEditVisiable () {
      if (this.menuEditVisiable) {
        $getPermissions({type: '1'}).then((r) => {
          this.treeData = r.data.data.children
          this.allTreeKeys = r.data.data.ids
          this.menuTreeKey = +new Date()
        })
      }
    }
  }
}
</script>
