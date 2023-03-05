<template>
  <div class="app-container">

    <iframe :src="'http://localhost:8800/admin/modeler/create/'+processTemplateId" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" width="100%" height="700"></iframe>

  </div>
</template>

<script>

export default {
  data() {
    return {
      processTemplateId: null
    }
  },

  // 生命周期函数：内存准备完毕，页面尚未渲染
  created() {
    console.log('list created......')
    this.processTemplateId = this.$route.query.id
  },

  // 生命周期函数：内存准备完毕，页面渲染成功
  mounted() {
    console.log('list mounted......')
  },

  methods: {

  }
}
</script>