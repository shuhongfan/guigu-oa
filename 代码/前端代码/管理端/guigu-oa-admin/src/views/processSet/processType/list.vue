<template>
  <div class="app-container">

    <!-- 工具条 -->
    <div class="tools-div">
      <el-button type="success" icon="el-icon-plus" size="mini" @click="add" :disabled="$hasBP('bnt.processType.add')  === false">添 加</el-button>
    </div>

    <!-- banner列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      stripe
      border
      style="width: 100%;margin-top: 10px;"
    >

      <el-table-column
        type="selection"
        width="55"
      />

      <el-table-column
        label="序号"
        width="70"
        align="center"
      >
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="name" label="类型名称"/>
      <el-table-column prop="description" label="描述"/>
      <el-table-column prop="createTime" label="创建时间"/>
      <el-table-column prop="updateTime" label="更新时间"/>

      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="edit(scope.row.id)" :disabled="$hasBP('bnt.processType.update')  === false">修改</el-button>
          <el-button type="text" size="mini" @click="removeDataById(scope.row.id)" :disabled="$hasBP('bnt.processType.remove')  === false">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      :page-sizes="[5, 10, 20, 30, 40, 50, 100]"
      style="padding: 30px 0; text-align: center;"
      layout="sizes, prev, pager, next, jumper, ->, total, slot"
      @current-change="fetchData"
      @size-change="changeSize"
    />

    <el-dialog title="添加/修改" :visible.sync="dialogVisible" width="40%">
      <el-form ref="flashPromotionForm" label-width="150px" size="small" style="padding-right: 40px;">
        <el-form-item label="类型名称">
          <el-input v-model="processType.name"/>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="processType.description"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdate()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/process/processType'

const defaultForm = {
  id: '',
  name: '',
  description: ''
}
export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, // banner列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象

      dialogVisible: false,
      processType: defaultForm,
      saveBtnDisabled: false
    }
  },

  // 生命周期函数：内存准备完毕，页面尚未渲染
  created() {
    console.log('list created......')
    this.fetchData()
  },

  // 生命周期函数：内存准备完毕，页面渲染成功
  mounted() {
    console.log('list mounted......')
  },

  methods: {
    // 当页码发生改变的时候
    changeSize(size) {
      console.log(size)
      this.limit = size
      this.fetchData(1)
    },

    // 加载列表数据
    fetchData(page = 1) {
      console.log('翻页。。。' + page)
      this.page = page

      api.getPageList(this.page, this.limit, this.searchObj).then(response => {
        this.list = response.data.records
        this.total = response.data.total

        // 数据加载并绑定成功
        this.listLoading = false
      })
    },

    // 重置查询表单
    resetData() {
      console.log('重置查询表单')
      this.searchObj = {}
      this.fetchData()
    },

    // 根据id删除数据
    removeDataById(id) {
      // debugger
      this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => { // promise
        // 点击确定，远程调用ajax
        return api.removeById(id)
      }).then((response) => {
        this.fetchData(this.page)
        this.$message.success(response.message)
      }).catch(() => {
        this.$message.info('取消删除')
      })
    },

    // -------------
    add() {
      this.dialogVisible = true
      this.processType = Object.assign({}, defaultForm)
    },

    edit(id) {
      this.dialogVisible = true
      this.fetchDataById(id)
    },

    fetchDataById(id) {
      api.getById(id).then(response => {
        this.processType = response.data
      })
    },

    saveOrUpdate() {
      this.saveBtnDisabled = true // 防止表单重复提交
      if (!this.processType.id) {
        this.saveData()
      } else {
        this.updateData()
      }
    },

    // 新增
    saveData() {
      api.save(this.processType).then(response => {
        this.$message.success(response.message || '操作成功')
        this.dialogVisible = false
        this.fetchData(this.page)
      })
    },

    // 根据id更新记录
    updateData() {
      api.updateById(this.processType).then(response => {
        this.$message.success(response.message || '操作成功')
        this.dialogVisible = false
        this.fetchData(this.page)
      })
    }
  }
}
</script>