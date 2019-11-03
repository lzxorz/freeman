<template>
  <a-card :bordered="false" class="card-area">
    <!-- 左侧面板 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="12">
          <a-col :md="7" :sm="8">
            <a-form-item label="字典名称" :labelCol="{span: 6}" :wrapperCol="{span: 14, offset: 1}">
              <a-input placeholder="请输入字典名称" v-model="queryParams.name"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
            <a-form-item label="字典类型" :labelCol="{span: 6}" :wrapperCol="{span: 14, offset: 1}">
              <a-input placeholder="请输入字典类型" v-model="queryParams.type"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
            <span style="float: leftoverflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="search" icon="search">查询</a-button>
              <a-button type="primary" @click="reset" icon="reload" style="margin-left: 8px" >重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div>
      <div class="operator">
        <a-button v-hasPermission="'dict:add'" type="primary" ghost @click="add">新增</a-button>
        <a-button v-hasPermission="'dict:delete'" @click="batchDelete">删除</a-button>
        <a-dropdown v-hasPermission="'dict:export'">
          <a-menu slot="overlay">
            <a-menu-item key="export-data" @click="exportExcel">导出Excel</a-menu-item>
          </a-menu>
          <a-button> 更多操作 <a-icon type="down" /> </a-button>
        </a-dropdown>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo" :columns="columns" :dataSource="dataSource"
      :pagination="pagination" :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      @change="handleTableChange" :scroll="{ x: 900 }">
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'dict:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5"
          @click="edit(record)" title="编辑" />
          <!-- <a-badge v-hasNoPermission="'dict:update'" status="warning" text="无权限"></a-badge> -->
          <a-divider type="vertical" />
          <a-icon @click="editDictItem(record.type)" type="bars" title="字典选项" />
          <!-- <a-popconfirm title="确定删除吗?" @confirm="() =>handleDelete(record.id)"> </a-popconfirm> -->
        </template>
      </a-table>
    </div>
    <!-- 新增字典 -->
    <dict-add @close="handleDictAddClose" @success="handleDictAddSuccess" :visiable="dictAddVisiable"> </dict-add>
    <!-- 修改字典 -->
    <dict-edit ref="dictEdit" @close="handleDictEditClose" @success="handleDictEditSuccess" :visiable="dictEditVisiable"> </dict-edit>
    <!-- 字典选项 -->
    <dict-item-list ref="dictItemList"></dict-item-list>
  </a-card>
</template>

<script>
import {$getDicts, $delDict} from '@/api'
import DictItemList from './DictItemList'
import DictAdd from './DictAdd'
import DictEdit from './DictEdit'

export default {
  name: 'DictList',
  components: { DictAdd, DictEdit, DictItemList },
  data () {
    return {
      dataSource: [],
      selectedRowKeys: [],
      paginationInfo: null,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      queryParams: { type: '', name: '' },
      dictAddVisiable: false,
      dictEditVisiable: false,
      loading: false,

      visible: false,
      labelCol: { xs: { span: 8 }, sm: { span: 5 } },
      wrapperCol: { xs: { span: 16 }, sm: { span: 19 } }
    }
  },
  computed: {
    columns () {
      return [
        { title: '字典名称', align: 'left', dataIndex: 'name' },
        { title: '字典类型', align: 'left', dataIndex: 'type' },
        { title: '描述', align: 'left', dataIndex: 'remark' },
        { title: '操作', dataIndex: 'operation', scopedSlots: { customRender: 'operation' }, fixed: 'right', width: 100 }
      ]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
      if (!this.advanced) {
        this.queryParams.fieldName = ''
      }
    },
    handleDictAddSuccess () {
      this.dictAddVisiable = false
      this.$message.success('新增字典成功')
      this.search()
    },
    handleDictAddClose () {
      this.dictAddVisiable = false
    },
    add () {
      this.dictAddVisiable = true
    },
    handleDictEditSuccess () {
      this.dictEditVisiable = false
      this.$message.success('修改字典成功')
      this.search()
    },
    handleDictEditClose () {
      this.dictEditVisiable = false
    },
    edit (record) {
      this.$refs.dictEdit.setFormValues(record)
      this.dictEditVisiable = true
    },
    editDictItem (type) {
      this.$refs.dictItemList.setFormValues({type})
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
          let dictIds = []
          for (let key of that.selectedRowKeys) {
            dictIds.push(that.dataSource[key].dictId)
          }
          $delDict({ids: dictIds.join(',')}).then(() => {
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
    exportExcel () {
      /* this.$export('dict/excel', {
        ...this.queryParams
      }) */
    },
    search () {
      this.fetch({
        ...this.queryParams
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
      this.paginationInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      this.paginationInfo = pagination
      this.fetch({
        ...this.queryParams
      })
    },
    fetch (params = {}) {
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
      $getDicts({...params}).then((r) => {
        let data = r.data
        const pagination = { ...this.pagination }
        pagination.total = data.total
        this.loading = false
        this.dataSource = data.data
        this.pagination = pagination
      })
    }
  },
  watch: {
    /* openKeys (val) {
      console.log('openKeys', val)
    } */
  }
}
</script>
<style scoped>
/* @import "/less/Common" */
@import '../../../assets/less/common.less'
</style>
