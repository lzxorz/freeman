<template>
  <a-drawer
    title="新增用户"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="userAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='用户名' v-bind="formItemLayout" :validateStatus="validateStatus" :help="help">
        <a-input v-model="user.username" @blur="handleUsernameCheck"
                 v-decorator="['username',{rules: [{ required: true, message: '用户名不能为空'}]}]"/>
      </a-form-item>
      <a-form-item label='密码' v-bind="formItemLayout">
        <a-tooltip title='新用户默认密码为 1234qwer'>
          <a-input type='password' readOnly :value="defaultPassword"/>
        </a-tooltip>
      </a-form-item>
      <a-form-item label='邮箱' v-bind="formItemLayout">
        <a-input v-model="user.email" v-decorator="['email',{rules: [ { type: 'email', message: '请输入正确的邮箱' }, { max: 50, message: '长度不能超过50个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label="手机" v-bind="formItemLayout">
        <a-input v-model="user.phone"
          v-decorator="['phone', {rules: [
            { pattern: '^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$', message: '请输入正确的手机号'}
          ]}]"/>
      </a-form-item>
      <a-form-item label='角色' v-bind="formItemLayout">
        <dict-select v-model="user.roleIds" v-decorator="['roleIds',{rules: [{ required: true, message: '请选择角色' }]}]"
        placeholder="请选择用户角色" dictType="sys_role" mode="multiple" />
      </a-form-item>
      <a-form-item label='部门' v-bind="formItemLayout">
        <org-tree-select v-model="user.deptId" v-decorator="['user.deptId']" />
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group
          v-model="user.status" v-decorator="['status',{rules: [{ required: true, message: '请选择状态'}]}]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">正常</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='性别' v-bind="formItemLayout">
         <dict-radio v-model="user.sex" v-decorator="['sex',{rules: [{ required: true, message: '请选择性别' }]}]" placeholder="请选择性别" dictType="sys_sex" />
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

import {$addUser, $checkUsername} from '@/api'
import OrgTreeSelect from '../org/OrgTreeSelect'
import DictSelect from '@/components/dict/DictSelect'
import DictRadio from '@/components/dict/DictRadio'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'UserAdd',
  props: {
    userAddVisiable: {
      default: false
    }
  },
  components: {OrgTreeSelect, DictSelect, DictRadio},
  data () {
    return {
      user: {
        username: '',
        deptId: ''
      },
      loading: false,
      // roleData: [],
      // orgTreeData: [],
      formItemLayout,
      defaultPassword: '1234qwer',
      form: this.$form.createForm(this),
      validateStatus: '',
      help: ''
    }
  },
  methods: {
    reset () {
      this.validateStatus = ''
      this.help = ''
      this.user.username = ''
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      if (this.validateStatus !== 'success') {
        this.handleUsernameCheck()
      }
      this.form.validateFields((err, values) => {
        if (!err && this.validateStatus === 'success') {
          this.loading = true
          $addUser({
            ...this.user
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    handleUsernameCheck () {
      let username = this.user.username.trim()
      if (username.length) {
        if (username.length > 10) {
          this.validateStatus = 'error'
          this.help = '用户名不能超过10个字符'
        } else if (username.length < 4) {
          this.validateStatus = 'error'
          this.help = '用户名不能少于4个字符'
        } else {
          this.validateStatus = 'validating'
          $checkUsername({}, {username}).then((r) => {
            if (r.data) {
              this.validateStatus = 'success'
              this.help = ''
            } else {
              this.validateStatus = 'error'
              this.help = '抱歉，该用户名已存在'
            }
          })
        }
      } else {
        this.validateStatus = 'error'
        this.help = '用户名不能为空'
      }
    }
  },
  watch: {
    userAddVisiable () {
      if (this.userAddVisiable) {
        /* $getRoles().then((r) => {
          this.roleData = r.data.data
        })
        $getOrgs().then((r) => {
          this.orgTreeData = r.data.data.children
        }) */
      }
    }
  }
}
</script>
