<template>
  <div class="app-container">

    <!-- 工具条 -->
    <div class="tools-div">
      <el-button type="success" icon="el-icon-plus" size="mini" @click="add()" :disabled="$hasBP('bnt.processTemplate.templateSet')  === false">添加审批模板</el-button>
    </div>

    <!-- 列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      stripe
      border
      style="width: 100%;margin-top: 10px;"
    >

      <el-table-column
        label="序号"
        width="70"
        align="center"
      >
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>iconPath

      <el-table-column prop="name" label="审批名称"/>
      <el-table-column label="图标">
        <template slot-scope="scope">
          <img :src="scope.row.iconUrl" style="width: 30px;height: 30px;vertical-align: text-bottom;">
        </template>
      </el-table-column>
      <el-table-column prop="processTypeName" label="审批类型"/>
      <el-table-column prop="description" label="描述"/>
      <el-table-column prop="createTime" label="创建时间"/>
      <el-table-column prop="updateTime" label="更新时间"/>

      <el-table-column label="操作" width="250" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="show(scope.row)">查看审批模板</el-button>
          <el-button type="text" v-if="scope.row.status == 0" size="mini" @click="edit(scope.row.id)" :disabled="$hasBP('bnt.processTemplate.templateSet')  === false">修改审批设置</el-button>
          <el-button type="text" v-if="scope.row.status == 0" size="mini" @click="removeDataById(scope.row.id)" :disabled="$hasBP('bnt.processTemplate.remove')  === false">删除</el-button>

          <el-button v-if="scope.row.status == 0" type="text" size="mini" @click="publish(scope.row.id)" :disabled="$hasBP('bnt.processTemplate.publish')  === false">发布</el-button>
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

    <el-dialog title="查看审批设置" :visible.sync="formDialogVisible" width="35%">
      <h3>基本信息</h3>
      <el-divider/>
      <el-form ref="flashPromotionForm" label-width="150px" size="small" style="padding-right: 40px;">
        <el-form-item label="审批类型" style="margin-bottom: 0px;">{{ processTemplate.processTypeName }}</el-form-item>
        <el-form-item label="名称" style="margin-bottom: 0px;">{{ processTemplate.name }}</el-form-item>
        <el-form-item label="创建时间" style="margin-bottom: 0px;">{{ processTemplate.createTime }}</el-form-item>
      </el-form>
      <h3>表单信息</h3>
      <el-divider/>
      <div>
        <form-create
          :rule="rule"
          :option="option"
        ></form-create>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="formDialogVisible = false" size="small">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/process/processTemplate'

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, // banner列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象

      rule: [],
      option: {},
      processTemplate: {},
      formDialogVisible: false
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
    onlineSet(row) {
  this.$router.push('/processSet/onlineProcessSet?id=' + row.id)
},

    //发布
    publish(id) {
      api.publish(id).then(response => {
        this.$message.success('发布成功')
        this.fetchData()
      })
    },
    show(row) {
      this.rule = JSON.parse(row.formProps)
      this.option = JSON.parse(row.formOptions)
      this.processTemplate = row
      this.formDialogVisible = true
    },
    // 当页码发生改变的时候
    changeSize(size) {
      console.log(size)
      this.limit = size
      this.fetchData(1)
    },

    // 加载banner列表数据
    fetchData(page = 1) {
      console.log('翻页。。。' + page)
      // 异步获取远程数据（ajax）
      this.page = page

      api.getPageList(this.page, this.limit, this.searchObj).then(
        response => {
          this.list = response.data.records
          this.total = response.data.total

          // 数据加载并绑定成功
          this.listLoading = false
        }
      )
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

    add() {
      
      this.$router.push('/processSet/templateSet')
    },

    edit(id) {
      
      this.$router.push('/processSet/templateSet?id=' + id)
    }
  }
}
</script>