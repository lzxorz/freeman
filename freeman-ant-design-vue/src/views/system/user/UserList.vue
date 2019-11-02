<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row>
          <div :class="advanced ? null: 'fold'">
            <a-col :md="12" :sm="24">
              <a-form-item label="用户名" :labelCol="{span: 4}" :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.username" />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="性别" :labelCol="{span: 4}" :wrapperCol="{span: 18, offset: 2}">
                <dict-select v-model="queryParams.sex" placeholder="请选择用户性别" dictType="sys_sex" mode="multiple" />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="部门" :labelCol="{span: 4}" :wrapperCol="{span: 18, offset: 2}">
                <org-tree-select v-model="queryParams.deptId" :showSearch="true" />
              </a-form-item>
            </a-col>
            <template v-if="advanced">
              <a-col :md="12" :sm="24">
                <a-form-item label="创建时间" :labelCol="{span: 4}" :wrapperCol="{span: 18, offset: 2}">
                  <range-date @change="handleDateChange" ref="createTime"></range-date>
                </a-form-item>
              </a-col>
            </template>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
            <a @click="toggleAdvanced" style="margin-left: 8px">
              {{advanced ? '收起' : '展开'}}
              <a-icon :type="advanced ? 'up' : 'down'" />
            </a>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add" v-hasPermission="'user:add'">新增</a-button>
        <a-button @click="batchDelete" v-hasPermission="'user:delete'">删除</a-button>
        <a-dropdown v-hasAnyPermission="'user:reset','user:export'">
          <a-menu slot="overlay">
            <a-menu-item v-hasPermission="'user:reset'" key="password-reset" @click="resetPassword" >密码重置</a-menu-item>
            <a-menu-item v-hasPermission="'user:export'" key="export-data" @click="exportExcel" >导出Excel</a-menu-item>
          </a-menu>
          <a-button> 更多操作 <a-icon type="down" /> </a-button>
        </a-dropdown>
      </div>

      <!-- 表格区域 -->
      <a-table
        ref="TableInfo"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="pagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        :scroll="{ x: 900 }"
        @change="handleTableChange"
      >
        <template slot="email" slot-scope="text, record">
          <a-popover placement="topLeft">
            <template slot="content">
              <div>{{text}}</div>
            </template>
            <p style="width: 150px;margin-bottom: 0">{{text}}</p>
          </a-popover>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'user:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改用户" ></a-icon>&nbsp;
          <a-icon v-hasPermission="'user:view'" type="eye" theme="twoTone" twoToneColor="#42b983" @click="view(record)" title="查看" ></a-icon>
          <a-badge v-hasNoPermission="'user:update','user:view'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
    <!-- 用户信息查看 -->
    <user-info modalTitle="用户信息" :modalData="userInfo.data" :visiable="userInfo.visiable" :dicts="dicts" @close="handleUserInfoClose" />
    <!-- 新增用户 -->
    <user-add @close="handleUserAddClose" @success="handleUserAddSuccess" :userAddVisiable="userAdd.visiable" />
    <!-- 修改用户 -->
    <user-edit ref="userEdit" @close="handleUserEditClose" @success="handleUserEditSuccess" :visiable="userEdit.visiable" />
  </a-card>
</template>

<script>
/* eslint-disable */
import UserInfo from './UserInfo'
import RangeDate from '@/components/datetime/RangeDate'
import UserAdd from './UserAdd'
import UserEdit from './UserEdit'
import OrgTreeSelect from '../org/OrgTreeSelect'
import DictSelect from '@/components/dict/DictSelect'
import {initDictOptions, getDictLabelByValue} from '@/components/dict/DictSelectUtil'

import {$getusers, $deleteUserById, $resetPassword} from '@/api'

export default {
  name: 'UserList',
  components: {UserInfo, UserAdd, UserEdit, OrgTreeSelect, RangeDate, DictSelect},
  data () {
    return {
      advanced: false,
      userInfo: {
        visiable: false,
        data: {}
      },
      userAdd: {
        visiable: false
      },
      userEdit: {
        visiable: false
      },
      queryParams: {
        username: '',
        sex: '',
        deptId: '',
        params: {
          createTime: []
        }
      },
      dicts: {}, // 列表用到的字典
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      }
    }
  },
  computed: {
    columns () {
      let { sortedInfo, filteredInfo } = this
      sortedInfo = sortedInfo || {}
      filteredInfo = filteredInfo || {}
      return [{
        title: '用户名',
        dataIndex: 'username',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'username' && sortedInfo.order
      }, {
        title: '性别',
        dataIndex: 'sex',
        customRender: (text, row, index) => {
          return getDictLabelByValue(this.dicts['sys_sex'], text)
        },
        filters: [{text:'男', value: '１'},{text:'女', value: '２'}],
        filterMultiple: false,
        filteredValue: filteredInfo.sex || null,
        onFilter: (value, record) => record.sex.includes(value)
      },
      { title: '邮箱', dataIndex: 'email', scopedSlots: { customRender: 'email' }, width: 100 },
      { title: '部门', dataIndex: 'deptName' },
      { title: '电话', dataIndex: 'phone' },
      {
        title: '状态',
        dataIndex: 'status',
        customRender: (text, row, index) => {
          // return getDictLabelByValue(this.dicts['sys_status'], text)
          switch (text) {
            case '0':
              return <a-tag color="red">锁定</a-tag>
            case '1':
              return <a-tag color="cyan">正常</a-tag>
            default:
              return text
          }
        },
        filters: [ { text: '锁定', value: '0' }, { text: '正常', value: '1' } ],
        filterMultiple: false,
        filteredValue: filteredInfo.status || null,
        onFilter: (value, record) => record.status.includes(value)
      }, {
        title: '创建时间',
        dataIndex: 'createTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'createTime' && sortedInfo.order
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  created () {
    // 获取字典数据
    initDictOptions('sys_sex').then(r => {
      while (!r.status) { }
      this.dicts['sys_sex'] = r.data.data
    })
    /* initDictOptions('sys_status').then(r => {
      while (!r.status) { }
      this.dicts['sys_status'] = r.data.data
    }) */
  },
  mounted () {
    this.fetch()
  },
  watch: {
    queryParams: {
      handler(newName, oldName) {
        console.log('watch==>', JSON.stringify(this.queryParams))
      },
      deep: true
    }
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
      if (!this.advanced) {
        this.queryParams.params.createTime = []
      }
    },
    view (record) {
      this.userInfo.data = record
      this.userInfo.visiable = true
    },
    add () {
      this.userAdd.visiable = true
    },
    handleUserAddClose () {
      this.userAdd.visiable = false
    },
    handleUserAddSuccess () {
      this.userAdd.visiable = false
      this.$message.success('新增用户成功')
      this.search()
    },
    edit (record) {
      this.$refs.userEdit.setFormValues(record)
      this.userEdit.visiable = true
    },
    handleUserEditClose () {
      this.userEdit.visiable = false
    },
    handleUserEditSuccess () {
      this.userEdit.visiable = false
      this.$message.success('修改用户成功')
      this.search()
    },
    handleUserInfoClose () {
      this.userInfo.visiable = false
    },
    handleDateChange (value) {
      if (value) {
        this.queryParams.params.createTime = value
      }
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let userIds = []
          for (let key of that.selectedRowKeys) {
            userIds.push(that.dataSource[key].userId)
          }
          $deleteUserById({}, {ids: userIds.join(',')}).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    resetPassword () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要重置密码的用户')
        return
      }
      let that = this
      this.$confirm({
        title: '确定重置选中用户密码?',
        content: '当您点击确定按钮后，这些用户的密码将会重置为123456',
        centered: true,
        onOk () {
          let userIds = []
          for (let key of that.selectedRowKeys) {
            userIds.push(that.dataSource[key].userId)
          }
          $resetPassword({}, {
            ids: userIds.join(',')
          }).then(() => {
            that.$message.success('重置用户密码成功')
            that.selectedRowKeys = []
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    /* exportExcel () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.$export('user/excel', {
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    }, */
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }

      this.fetch({
        sortField,
        sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {
        username: '',
        sex: '',
        deptId: '',
        params: {
          createTime: []
        }
      }
      // 清空时间选择
      if (this.advanced) {
        this.$refs.createTime.reset()
      }
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data, 用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.userInfo.visiable = false
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...this.filteredInfo
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.pageSize = this.paginationInfo.pageSize
        params.pageNum = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.pageSize = this.pagination.defaultPageSize
        params.pageNum = this.pagination.defaultCurrent
      }
      /* var searchParams = new URLSearchParams()
      Object.keys(params).forEach(key => {
        let value = params[key]
        if (value instanceof Object) {
          Object.keys(value).forEach(k => {
            let v = value[k]
            if (v instanceof Array) {
              v.forEach(arrEle => searchParams.append(`${key}[${k}]`, arrEle))
            } else if (v) {
              searchParams.append(`${key}.${k}`, v)
            }
          })
        } else if (value) {
          searchParams.append(key, value)
        }
      })
      console.log(searchParams.toString()) */
      $getusers(params).then(r => {
        let data = r.data
        const pagination = { ...this.pagination }
        pagination.total = data.total
        this.dataSource = data.data
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  }
}

</script>
<style lang="less" scoped>
@import "../../../../public/less/Common";
</style>
