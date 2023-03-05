<template>
  <div class="container">
    <van-nav-bar
      title="基本信息"
    />
    <div class="detail-wrap">
      <div class="item">
        <h5>用户姓名：{{ user.name }}</h5>
      </div>
      <div class="item">
        <h5>手机号：{{ user.phone }}</h5>
      </div>
      <div class="item">
        <h5>所在部门：{{ user.deptName }}</h5>
      </div>
      <div class="item">
        <h5>岗位：{{ user.postName }}</h5>
      </div>
    </div>

  </div>
</template>

<script>
import api from '@/api/userInfo'
export default {
  name: "process",

  data() {
    return {
      user: { }
    };
  },

  created(){
    this.fetchData();
  },

  methods: {
    fetchData() {
      // debugger
      api.getCurrentUser().then(response => {
        this.user = response.data
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 20px;

  .detail-wrap {
    .item {
      h5 {
        color: #838485;
        margin: 10px;
      }

      p {
        color: #1B1F22;
        margin: 0;
      }
    }
  }
}
</style>
