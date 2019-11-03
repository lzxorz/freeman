<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="12" :sm="24" >
              <a-form-item
                label="名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.name"/>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24" >
              <a-form-item
                label="创建时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
              </a-form-item>
            </a-col>
          </a-row>
        </div>
        <span style="float: right; margin-top: 3px;">
          <a-button type="primary" @click="search">查询</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button v-hasPermission="'org:add'" type="primary" ghost @click="add">新增</a-button>
        <a-button v-hasPermission="'org:delete'" @click="batchDelete">删除</a-button>
        <a-dropdown v-hasPermission="'org:export'">
          <a-menu slot="overlay">
            <a-menu-item key="export-data" @click="exportExcel">导出Excel</a-menu-item>
          </a-menu>
          <a-button>
            更多操作 <a-icon type="down" />
          </a-button>
        </a-dropdown>
      </div>
      <!-- 表格区域 -->
      <a-table :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :scroll="{ x: 900 }"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               @change="handleTableChange">
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'org:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改"></a-icon>
          <a-badge v-hasNoPermission="'org:update'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
    <!-- 新增部门 -->
    <org-add @success="handleOrgAddSuccess" @close="handleOrgAddClose" :orgAddVisiable="orgAddVisiable"> </org-add>

    <!-- 修改部门 -->
    <org-edit ref="orgEdit" @success="handleOrgEditSuccess" @close="handleOrgEditClose" :orgEditVisiable="orgEditVisiable"> </org-edit>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {$getOrgs, $deleteOrgById} from '@/api'
import OrgAdd from './OrgAdd'
import OrgEdit from './OrgEdit'

export default {
  name: 'Org',
  components: {OrgAdd, OrgEdit, RangeDate},
  data () {
    return {
      advanced: false,
      dataSource: [],
      selectedRowKeys: [],
      queryParams: {
        name: '',
        params: {
          createTime: []
        }
      },
      sortedInfo: null,
      pagination: {
        defaultPageSize: 10000000,
        hideOnSinglePage: true,
        indentSize: 100
      },
      loading: false,
      orgAddVisiable: false,
      orgEditVisiable: false
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '名称',
        dataIndex: 'label'
      }, {
        title: '排序',
        dataIndex: 'order'
      }, {
        title: '创建时间',
        dataIndex: 'createTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'createTime' && sortedInfo.order
      }, {
        title: '修改时间',
        dataIndex: 'updateTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'updateTime' && sortedInfo.order
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' },
        fixed: 'right',
        width: 120
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    handleOrgAddClose () {
      this.orgAddVisiable = false
    },
    handleOrgAddSuccess () {
      this.orgAddVisiable = false
      this.$message.success('新增部门成功')
      this.fetch()
    },
    add () {
      this.orgAddVisiable = true
    },
    handleOrgEditClose () {
      this.orgEditVisiable = false
    },
    handleOrgEditSuccess () {
      this.orgEditVisiable = false
      this.$message.success('修改部门成功')
      this.fetch()
    },
    edit (record) {
      this.orgEditVisiable = true
      this.$refs.orgEdit.setFormValues(record)
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
        content: '当您点击确定按钮后，这些记录将会被彻底删除，如果其包含子记录，也将一并删除！',
        centered: true,
        onOk () {
          $deleteOrgById({}, {ids: that.selectedRowKeys.join(',')}).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.fetch()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    exportExcel () {
      let {sortedInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.$export('org/excel', {
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams
      })
    },
    search () {
      let {sortedInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      // 清空时间选择
      this.$refs.createTime.reset()
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      this.loading = true
      $getOrgs(params).then((r) => {
        let data = r.data
        this.loading = false
        this.dataSource = data.data.children
      })
    }
  }
}
</script>

<style lang="less" scoped>
@import "../../../../public/less/Common";
</style>
