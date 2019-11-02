<template>
  <a-card :bordered="false">
    <!-- 抽屉 -->
    <a-drawer title="字典列表" :width="screenWidth" @close="onClose" :visible="visible">
      <!-- 抽屉内容的border -->
      <div :style="{ padding:'5 10px', border: '1px solid #e9e9e9', background: '#fff', }">
        <div class="table-page-search-wrapper">
          <a-form layout="inline" :form="form">
            <a-row >
              <a-col :md="8" :sm="12">
                <a-form-item label="显示文字">
                  <a-input style="width: 90px" placeholder="请输入显示文字" v-model="queryParams.label"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="12">
                <a-form-item label="状态" style="width: 170px" :labelCol="labelCol" :wrapperCol="wrapperCol" >
                  <a-select placeholder="请选择" v-model="queryParams.status">
                    <a-select-option value="1">正常</a-select-option>
                    <a-select-option value="0">禁用</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="12">
                <span style="float: left" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="search">搜索</a-button>
                  <a-button type="primary" @click="reset" style="margin-left: 8px">重置</a-button>
                </span>
              </a-col>
            </a-row>
            <a-row>
              <a-col :md="2" :sm="24">
                <a-button style="margin-bottom: 10px" type="primary" @click="handleAdd">新增</a-button>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div>
          <a-table ref="TableInfo" rowKey="id" size="middle" :columns="columns" :dataSource="dataSource"
          :pagination="ipagination" :loading="loading" @change="handleTableChange" >
            <span slot="operation" slot-scope="text, record">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)"> <a>删除</a> </a-popconfirm>
            </span>
          </a-table>
        </div>
      </div>
    </a-drawer>
    <dict-item-modal ref="modalForm" @ok="fetch({type})"></dict-item-modal>
  </a-card>
</template>

<script>
import {$getDictItems} from '@/api'
import DictItemModal from './DictItemModal'

export default {
  name: 'DictItemList',
  components: { DictItemModal },
  data () {
    return {
      form: this.$form.createForm(this),
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
      queryParams: { label: '', status: '' },
      type: '',
      visible: false,
      screenWidth: 800,
      labelCol: { xs: { span: 5 }, sm: { span: 5 } },
      wrapperCol: { xs: { span: 12 }, sm: { span: 12 } },
      validatorRules: {
        itemText: { rules: [{ required: true, message: '请输入名称!' }] },
        itemValue: { rules: [{ required: true, message: '请输入数据值!' }] }
      }
    }
  },
  created () {
    // 当页面初始化时,根据屏幕大小来给抽屉设置宽度
    this.resetScreenSize()
  },
  computed: {
    columns () {
      return [
        { title: '显示文字', align: 'center', dataIndex: 'label' },
        { title: '数据值', align: 'center', dataIndex: 'value' },
        { title: '操作', dataIndex: 'operation', align: 'center', scopedSlots: { customRender: 'operation' } }
      ]
    }
  },
  methods: {
    setFormValues (record) {
      this.form.resetFields()
      this.queryParams = {}
      this.type = record.type
      this.fetch({type: this.type})
      this.visible = true
    },
    // 添加字典数据
    handleAdd () {
      this.$refs.modalForm.add(this.type)
      this.$refs.modalForm.title = '新增'
    },
    // 编辑字典数据
    handleEdit (record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    showDrawer () {
      this.visible = true
    },
    onClose () {
      this.visible = false
      this.form.resetFields()
      this.dataSource = []
    },
    search () {
      this.fetch({ ...this.queryParams })
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
      this.fetch({ ...this.queryParams })
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
      $getDictItems({...params}).then((r) => {
        let data = r.data
        const pagination = { ...this.pagination }
        pagination.total = data.total
        this.loading = false
        this.dataSource = data.data
        this.pagination = pagination
      })
    },
    // 抽屉的宽度随着屏幕大小来改变
    resetScreenSize () {
      let screenWidth = document.body.clientWidth
      if (screenWidth < 600) {
        this.screenWidth = screenWidth
      } else {
        this.screenWidth = 600
      }
    }
  }
}
</script>
<style scoped>
</style>
