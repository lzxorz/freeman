<template>
  <a-drawer
    title="修改角色" :maskClosable="false" width=650
    placement="right" :closable="false" @close="onClose" :visible="roleEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='角色名称' v-bind="formItemLayout">
        <a-input v-decorator="['name']"/>
      </a-form-item>
      <a-form-item label='角色编码' v-bind="formItemLayout" >
        <a-input readOnly v-decorator="['code']" placeholder="请输入字母、数字组合" />
      </a-form-item>
      <a-form-item label='角色描述' v-bind="formItemLayout">
        <a-textarea :rows="4" v-decorator="[ 'remark', {rules: [ { max: 50, message: '长度不能超过50个字符'} ]}]">
        </a-textarea>
      </a-form-item>
       <a-form-item label='数据范围' v-bind="formItemLayout">
         <dict-radio v-decorator="['dataScope',{rules: [{ required: true, message: '请选择数据范围' }]}]" placeholder="请选择数据范围" dictType="sys_data_scope" />
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='权限选择' style="margin-bottom: 2rem" :validateStatus="menuSelectStatus" :help="menuSelectHelp" v-bind="formItemLayout">
        <a-tree :treeData="menuTreeData"
          :key="menuTreeKey" ref="menuTree" :checkable="true" :checkStrictly="checkStrictly"
          @check="handleCheck" @expand="handleExpand"
          :expandedKeys="expandedKeys" :defaultCheckedKeys="defaultCheckedKeys">
        </a-tree>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-dropdown style="float: left" :trigger="['click']" placement="topCenter">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="expandAll">展开所有</a-menu-item>
          <a-menu-item key="2" @click="closeAll">合并所有</a-menu-item>
          <a-menu-item key="3" @click="enableRelate">父子关联</a-menu-item>
          <a-menu-item key="4" @click="disableRelate">取消关联</a-menu-item>
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
import {$getPermissions, $getRolePerms, $editRole} from '@/api'
import DictRadio from '@/components/dict/DictRadio'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}

export default {
  name: 'RoleEdit',
  props: {
    roleEditVisiable: {
      default: false
    },
    roleInfoData: {
      require: true
    }
  },
  components: {DictRadio},
  data () {
    return {
      menuTreeKey: +new Date(),
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      menuSelectStatus: '',
      menuSelectHelp: '',
      role: {
        permissionIds: ''
      },
      menuTreeData: [],
      allTreeKeys: [],
      checkedKeys: [],
      defaultCheckedKeys: [],
      expandedKeys: [],
      checkStrictly: true
    }
  },
  methods: {
    reset () {
      this.menuTreeKey = +new Date()
      this.expandedKeys = this.checkedKeys = this.defaultCheckedKeys = []
      this.menuSelectStatus = this.menuSelectHelp = ''
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    expandAll () {
      this.expandedKeys = this.allTreeKeys
    },
    closeAll () {
      this.expandedKeys = []
    },
    enableRelate () {
      this.checkStrictly = false
    },
    disableRelate () {
      this.checkStrictly = true
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
      let checkedArr = Object.is(checkedKeys.checked, undefined) ? checkedKeys : checkedKeys.checked
      if (checkedArr.length) {
        this.menuSelectStatus = ''
        this.menuSelectHelp = ''
      } else {
        this.menuSelectStatus = 'error'
        this.menuSelectHelp = '请选择相应的权限'
      }
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    setFormValues ({...role}) {
      let fields = ['name', 'code', 'remark', 'dataScope', 'status']
      Object.keys(role).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          this.form.setFieldsValue({[key]: role[key]})
        }
      })
    },
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length === 0) {
        this.menuSelectStatus = 'error'
        this.menuSelectHelp = '请选择相应的权限'
      } else {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            let role = this.form.getFieldsValue()
            role.id = this.roleInfoData.id
            role.permissionIds = checkedArr.join(',')
            $editRole({...role}).then((r) => {
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
      }
    }
  },
  watch: {
    roleEditVisiable () {
      if (this.roleEditVisiable) {
        $getPermissions().then((r) => {
          this.menuTreeData = r.data.data.children
          this.allTreeKeys = r.data.data.ids
          $getRolePerms({}, {id: this.roleInfoData.id}).then((r) => {
            let checkData = r.data.data || []
            this.defaultCheckedKeys.splice(0, checkData.length, ...checkData)
            this.expandedKeys = this.checkedKeys = this.defaultCheckedKeys
            this.menuTreeKey = +new Date()
            // console.log(JSON.stringify(this.defaultCheckedKeys))
          })
        })
      }
    }
  }
}
</script>
