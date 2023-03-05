<template>
  <div class="app-container">
    <el-steps :active="stepIndex" finish-status="success">
      <el-step title="基本设置"></el-step>
      <el-step title="表单设置"></el-step>
      <el-step title="流程设置"></el-step>
    </el-steps>

    <div class="tools-div">
      <el-button v-if="stepIndex > 1" icon="el-icon-check" type="primary" size="small" @click="pre()" round>上一步
      </el-button>
      <el-button icon="el-icon-check" type="primary" size="small" @click="next()" round>{{
          stepIndex == 3 ? '提交保存' : '下一步'
        }}
      </el-button>
      <el-button type="primary" size="small" @click="back()">返回</el-button>
    </div>

    <!-- 第一步 -->
    <div v-show="stepIndex == 1" style="margin-top: 20px;">
      <el-form ref="flashPromotionForm" label-width="150px" size="small" style="padding-right: 40px;">
        <el-form-item label="审批类型">
          <el-select v-model="processTemplate.processTypeId" placeholder="请选择审批类型">
            <el-option :key="item.id" v-for="item in processTypeList" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审批名称">
          <el-input v-model="processTemplate.name"/>
        </el-form-item>
        <el-form-item label="审批图标">
          <el-select v-model="processTemplate.iconUrl" placeholder="请选择审批图标">
            <el-option :key="item.id" v-for="item in iconUrlList" :label="item.iconUrl" :value="item.iconUrl">
              <img :src="item.iconUrl" style="width: 30px;height: 30px;vertical-align: text-bottom;">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="processTemplate.description"/>
        </el-form-item>
      </el-form>
    </div>

    <!-- 第二步 -->
    <div v-show="stepIndex == 2" style="margin-top: 20px;">
      <!--表单构建器-->
      <fc-designer class="form-build" ref="designer"/>
    </div>

    <!-- 第三步 -->
    <div v-show="stepIndex == 3" style="margin-top: 20px;">
      <el-upload
        class="upload-demo"
        drag
        action="/dev-api/admin/process/processTemplate/uploadProcessDefinition"
        :headers="uploadHeaders"
        multiple="false"
        :before-upload="beforeUpload"
        :on-success="onUploadSuccess"
        :file-list="fileList"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将Activiti流程设计文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传zip压缩文件，且不超过2048kb</div>
      </el-upload>
    </div>
  </div>
</template>

<script>
import api from '@/api/process/processTemplate'
import processTypeApi from '@/api/process/processType'
import store from '@/store'

const defaultForm = {
  id: '',
  name: '',
  iconUrl: '',
  formProps: '',
  formOptions: '',
  processDefinitionKey: '',
  processDefinitionPath: '',
  description: ''
}
export default {
  data() {
    return {
      stepIndex: 1,
      processTypeList: [],
      processTemplate: defaultForm,
      iconUrlList: [
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1t695CFYqK1RjSZLeXXbXppXa-102-102.png', tag: '请假' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1bHOWCSzqK1RjSZFjXXblCFXa-112-112.png', tag: '出差' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1cbCYCPTpK1RjSZKPXXa3UpXa-112-112.png', tag: '机票出差' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1cbCYCPTpK1RjSZKPXXa3UpXa-112-112.png', tag: '机票改签' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1e76lCOLaK1RjSZFxXXamPFXa-112-112.png', tag: '外出' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1Yfa0CG6qK1RjSZFmXXX0PFXa-112-112.png', tag: '补卡申请' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1Y8PlCNjaK1RjSZKzXXXVwXXa-112-112.png', tag: '加班' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB11X99CNTpK1RjSZFKXXa2wXXa-102-102.png', tag: '居家隔离' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1_YG.COrpK1RjSZFhXXXSdXXa-102-102.png', tag: '请假' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB13ca1CMDqK1RjSZSyXXaxEVXa-102-102.png', tag: '调岗' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1U9iBCSzqK1RjSZPcXXbTepXa-102-102.png', tag: '离职' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB11pS_CFzqK1RjSZSgXXcpAVXa-102-102.png', tag: '费用申请' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1t695CFYqK1RjSZLeXXbXppXa-102-102.png', tag: '用章申请' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB13f_aCQzoK1RjSZFlXXai4VXa-102-102.png', tag: '携章外出' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1_YG.COrpK1RjSZFhXXXSdXXa-102-102.png', tag: '学期内分期' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1_YG.COrpK1RjSZFhXXXSdXXa-102-102.png', tag: '特殊学费' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1Yfa0CG6qK1RjSZFmXXX0PFXa-112-112.png', tag: '充值卡申领' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1e76lCOLaK1RjSZFxXXamPFXa-112-112.png', tag: '礼品申领' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1FNG.CMHqK1RjSZFgXXa7JXXa-102-102.png', tag: '邮寄快递申请' },
        { iconUrl: 'https://gw.alicdn.com/imgextra/i3/O1CN01LLn0YV1LhBXs7T2iO_!!6000000001330-2-tps-120-120.png', tag: '合同审批' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1e76lCOLaK1RjSZFxXXamPFXa-112-112.png', tag: '合同借阅' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1e76lCOLaK1RjSZFxXXamPFXa-112-112.png', tag: '魔点临时开门权限' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1bHOWCSzqK1RjSZFjXXblCFXa-112-112.png', tag: '北京科技园车证审批' },
        { iconUrl: 'https://gw.alicdn.com/tfs/TB1e76lCOLaK1RjSZFxXXamPFXa-112-112.png', tag: '魔点访客提前预约审批' }
      ],

      uploadHeaders: {
        'token': store.getters.token
      },
      fileList: []
    }
  },

  created() {
    let id = this.$route.query.id
    console.log(id)
    if (id > 0) {
      this.fetchDataById(id)
    }
    this.fetchProcessTypeData()
  },

  methods: {
    pre() {
      this.stepIndex -= 1
    },

    next() {
      if (this.stepIndex === 2) {
        this.processTemplate.formProps = JSON.stringify(this.$refs.designer.getRule())
        this.processTemplate.formOptions = JSON.stringify(this.$refs.designer.getOption())
        console.log(JSON.stringify(this.processTemplate))
      }
      if (this.stepIndex === 3) {
        this.saveOrUpdate()
      }

      this.stepIndex += 1
    },

    fetchProcessTypeData() {
      processTypeApi.findAll().then(response => {
        this.processTypeList = response.data
      })
    },
    fetchDataById(id) {
      api.getById(id).then(response => {
        this.processTemplate = response.data
        // 给表单设计器赋值
        this.$refs.designer.setRule(JSON.parse(this.processTemplate.formProps))
        this.$refs.designer.setOption(JSON.parse(this.processTemplate.formOptions))
        this.fileList = [{
          name: this.processTemplate.processDefinitionPath,
          url: this.processTemplate.processDefinitionPath
        }]
      })
    },

    saveOrUpdate() {
      this.saveBtnDisabled = true // 防止表单重复提交
      if (!this.processTemplate.id) {
        this.saveData()
      } else {
        this.updateData()
      }
    },

    // 新增
    saveData() {
      api.save(this.processTemplate).then(response => {
        this.$router.push('/processSet/processTemplate')
      })
    },

    // 根据id更新记录
    updateData() {
      api.updateById(this.processTemplate).then(response => {
        this.$router.push('/processSet/processTemplate')
      })
    },

    // 文件上传限制条件
    beforeUpload(file) {
      const isZip = file.type === 'application/x-zip-compressed'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isZip) {
        this.$message.error('文件格式不正确!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return false
      }
      return true
    },

    // 上传图片成功的回调
    onUploadSuccess(res, file) {
      // 填充上传文件列表
      this.processTemplate.processDefinitionPath = res.data.processDefinitionPath
      this.processTemplate.processDefinitionKey = res.data.processDefinitionKey
    },

    back() {
      this.$router.push('/processSet/processTemplate')
    }
  }
}
</script>