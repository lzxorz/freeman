<template>
  <a-drawer
    title="修改用户" :maskClosable="false" width=650 placement="right" :closable="false" @close="onClose" :visible="visiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='用户名' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['username']"/>
      </a-form-item>
      <a-form-item label='邮箱' v-bind="formItemLayout">
        <a-input v-decorator="['email',{rules: [
            { type: 'email', message: '请输入正确的邮箱' },
            { max: 50, message: '长度不能超过50个字符'} ]}
        ]"/>
      </a-form-item>
      <a-form-item label="手机" v-bind="formItemLayout">
        <a-input v-decorator="['phone', {rules: [
            { pattern: '^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$', message: '请输入正确的手机号'}
          ]}]"/>
      </a-form-item>
      <a-form-item label='角色' v-bind="formItemLayout">
        <dict-select v-decorator="['roleIds',{rules: [{ required: true, message: '请选择角色' }]}]"
        placeholder="请选择用户角色" dictType="sys_role" mode="multiple" />
      </a-form-item>
      <a-form-item label='部门' v-bind="formItemLayout">
        <org-tree-select v-decorator="['deptId']" />
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group v-decorator="[ 'status', {rules: [{ required: true, message: '请选择状态' }]} ]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='性别' v-bind="formItemLayout">
        <a-radio-group v-decorator="[ 'sex', {rules: [{ required: true, message: '请选择性别' }]} ]">
          <a-radio value="1">男</a-radio>
          <a-radio value="2">女</a-radio>
          <a-radio value="3">保密</a-radio>
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
import {mapState, mapMutations} from 'vuex'
import OrgTreeSelect from '../org/OrgTreeSelect'
import DictSelect from '@/components/dict/DictSelect'
import { isArray } from '@/utils/util'
import {$getUser, $editUser} from '@/api'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'UserEdit',
  props: {
    visiable: {
      default: false
    }
  },
  components: {OrgTreeSelect, DictSelect},
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      treeData: [],
      roleData: [],
      userDeptId: [],
      userId: '',
      loading: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  methods: {
    ...mapMutations({
      setUser: 'account/setUser'
    }),
    onClose () {
      this.loading = false
      this.form.resetFields()
      this.$emit('close')
    },
    setFormValues ({...user}) {
      this.userId = user.userId
      let fields = ['username', 'deptId', 'roleIds', 'email', 'status', 'sex', 'phone']
      Object.keys(user).forEach((key) => {
        if (fields.includes(key)) {
          this.form.getFieldDecorator(key)
          if (key === 'roleIds') {
            this.form.setFieldsValue({[key]: user[key].split(',')})
          }
          this.form.setFieldsValue({[key]: user[key]})
        }
      })
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let user = this.form.getFieldsValue()
          user.userId = this.userId
          user.roleIds = isArray(user.roleIds) ? user.roleIds.join(',') : user.roleIds
          $editUser({ ...user }).then((r) => {
            this.loading = false
            this.$emit('success')
            // 如果修改用户就是当前登录用户的话，更新其state
            if (user.username === this.currentUser.username) {
              $getUser({}, {userId: `${user.id}`}).then((r) => {
                this.setUser(r.data)
              })
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    userEditVisiable () {
      if (this.userEditVisiable) {
        /* this.$get('role').then((r) => {
          this.roleData = r.data.data
        })
        this.$get('org').then((r) => {
          this.treeData = r.data.data.children
        }) */
      }
    }
  }
}
</script>
