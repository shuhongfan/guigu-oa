<template>
  <div id="app">
    <router-view />

    <el-dialog title="绑定手机" :visible.sync="dialogVisible" width="80%" >
      <el-form ref="dataForm" :model="bindPhoneVo" size="small">
        <h4>绑定你的手机号，建立云尚办公系统关联关系</h4>
        <el-form-item label="手机号码">
          <el-input v-model="bindPhoneVo.phone"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" icon="el-icon-check" @click="saveBind()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import userInfoApi from '@/api/userInfo'
export default {
  data() {
    return {
      show: true,
      dialogVisible: false,
      bindPhoneVo: {
        openId: '',
        phone: ''
      }
    };
  },

  created() {
    // 处理微信授权登录
    this.wechatLogin();
  },

  methods: {
    clear() {
      window.localStorage.setItem('token', '')
      this.bindPhoneVo = {}
    },
    wechatLogin() {
      // 处理微信授权登录
      let token = this.getQueryString('token') || '';
      let openId = this.getQueryString('openId') || '';
      // token === '' && openId != '' 只要这种情况，未绑定账号
      if(token === '' && openId != '') {
        // 绑定账号
        this.bindPhoneVo.openId = openId
        this.dialogVisible = true
      } else {
        // 如果绑定了，授权登录直接返回token
        if(token !== '') {
          window.localStorage.setItem('token', token);
        }
        token = window.localStorage.getItem('token') || '';
        if (token == '') {
          let url = window.location.href.replace('#', 'guiguoa')
          window.location = 'http://ggkt2.vipgz1.91tunnel.com/admin/wechat/authorize?returnUrl=' + url
        }
      }
    },

    saveBind() {
      if(this.bindPhoneVo.phone.length != 11) {
        alert('手机号码格式不正确')
        return
      }
      userInfoApi.bindPhone(this.bindPhoneVo).then(response => {
        window.localStorage.setItem('token', response.data);
        this.dialogVisible = false
        window.location = 'http://ggkt1.vipgz1.91tunnel.com'
      })
    },

    getQueryString (paramName) {
      if(window.location.href.indexOf('?') == -1) return '';

      let searchString = window.location.href.split('?')[1];
      let i, val, params = searchString.split("&");

      for (i=0;i<params.length;i++) {
        val = params[i].split("=");
        if (val[0] == paramName) {
          return val[1];
        }
      }
      return '';
    }
  }
};
</script>
<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
