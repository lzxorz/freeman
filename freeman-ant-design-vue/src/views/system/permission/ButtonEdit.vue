<template>
  <a-drawer title="修改按钮" :maskClosable="false" width=650 placement="right" :closable="false"
    @close="onClose" :visible="buttonEditVisiable" style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='按钮名称' v-bind="formItemLayout">
        <a-input v-decorator="['name', {rules: [ { required: true, message: '按钮名称不能为空'}, { max: 10, message: '长度不能超过10个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='相关权限' v-bind="formItemLayout">
        <a-input v-decorator="['perms', {rules: [ { max: 50, message: '长度不能超过50个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='上级菜单' style="margin-bottom: 2rem" v-bind="formItemLayout">
        <a-tree :treeData="menuTreeData" :key="menuTreeKey" :checkable="true" :checkStrictly="true"
          @check="handleCheck" @expand="handleExpand" :expandedKeys="expandedKeys" :defaultCheckedKeys="defaultCheckedKeys">
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
import {$getPermissions, $editPermission} from '@/api'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'ButtonEdit',
  props: {
    buttonEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      menuTreeKey: +new Date(),
      button: {},
      checkedKeys: [],
      expandedKeys: [],
      defaultCheckedKeys: [],
      menuTreeData: []
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.menuTreeKey = +new Date()
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
      this.button.id = menu.id
      this.form.getFieldDecorator('name')
      this.form.setFieldsValue({'name': menu.label})
      this.form.getFieldDecorator('perms')
      this.form.setFieldsValue({'perms': menu.perms})

      this.defaultCheckedKeys.push(menu.parentId)
      this.checkedKeys = this.defaultCheckedKeys
      this.expandedKeys = this.checkedKeys
    },
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (!checkedArr.length) {
        this.$message.error('请为按钮选择一个上级菜单')
        return
      }
      if (checkedArr.length > 1) {
        this.$message.error('最多只能选择一个上级菜单，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let button = this.form.getFieldsValue()
          button.parentId = checkedArr[0]
          // 1 菜单 2 按钮
          button.type = '2'
          button.id = this.button.id
          $editPermission({...button}).then(() => {
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
    buttonEditVisiable () {
      if (this.buttonEditVisiable) {
        // type: '2'
        $getPermissions({}).then((r) => {
          this.menuTreeData = r.data.data.children
          this.allTreeKeys = r.data.data.ids
          this.menuTreeKey = +new Date()
        })
      }
    }
  }
}
</script>
