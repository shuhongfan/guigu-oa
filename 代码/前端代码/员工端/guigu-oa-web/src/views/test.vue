<template>
  <div>
    <div>账号切换</div>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <button @click="zzjl()" type="default" size="mini">张总经理</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3tzQxNDM01VEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQAjn-tfQgAAAA.dKczZ7BKISa_s3zV1VxVN2Yk7bSqRawbIUp1GVXDhpxD4n-jbrE-3UmruA_MM47H_OcLODk1R2TxI6HTPxnA-Q'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3tzQxNLMw1VEqLU4t8kxRsjKHMPMSc1OBWqoyEvPSjZVqAZJgwolDAAAA.eAGrymiYi_6Sb4KK3huGbxWXbu6DdLDQT1sGZUDaLa-OzUZTQhlEG9yS3LOjJEztmIJxAJvH5StubFlyzyqqhg'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3NjMwNLYw11EqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagGHebUNQQAAAA.FcCkD6QfaP8bd_3MYsRD6Tp9wr9JSRFWIUuIdG5O7XxhIeyWfzGLp40_Lesx_kWMMaaskh_ba3NrTlj8-nP6OA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3t7Q0MLU01VEqLU4t8kxRsjKDMPMSc1OBWnIyTZRqAZwbCQlAAAAA.tDNYSawJxFs7eYKZEcYreYVukYEpdY6pJhvzgE47Qpics4XEyGy_tXdgkJDb2afbVqJA4u1rn7VwVMi4pseRnw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
