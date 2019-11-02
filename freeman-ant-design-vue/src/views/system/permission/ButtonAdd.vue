<template>
  <a-drawer title="新增按钮" :maskClosable="false" width=650 placement="right" :closable="false"
    @close="onClose" :visible="buttonAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='按钮名称' v-bind="formItemLayout">
        <a-input v-decorator="['name', {rules: [ { required: true, message: '按钮名称不能为空'}, { max: 10, message: '长度不能超过10个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='相关权限' v-bind="formItemLayout">
        <a-input v-decorator="['perms', {rules: [ { max: 50, message: '长度不能超过50个字符'} ]}]"/>
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
        <a-button>
          树操作 <a-icon type="up" />
        </a-button>
      </a-dropdown>
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
// import {$getPermissions, $addPermission} from '@/api'
import {$addPermission} from '@/api'
import PermsTree from './PermsTree'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'ButtonAdd',
  props: {
    buttonAddVisiable: {
      default: false
    }
  },
  components: {PermsTree},
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      menuTreeKey: +new Date()
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.menuTreeKey = +new Date()
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
      if (!parentId.length) {
        this.$message.error('请为按钮选择一个上级菜单')
        return
      }
      if (parentId.length > 1) {
        this.$message.error('最多只能选择一个上级菜单，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let button = this.form.getFieldsValue()
          // alert(JSON.stringify(button.parentId))
          button.parentId = parentId ? parentId[0] : ''
          // 1 菜单 2 表示按钮
          button.type = '2'
          $addPermission({...button}).then(() => {
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
    buttonAddVisiable () {
      if (this.buttonAddVisiable) {
        this.$refs.ptree.initData('2')
      }
    }
  }
}
</script>
