<template>
  <div>
    <div class="container">
      <van-nav-bar
        title="OA审批"
      />
      <van-collapse v-model="activeNames">
        <van-collapse-item
          v-for="(item,key) in list"
		  :key="key"
          :title="item.name"
          :name="item.key">
          <van-row>
            <van-col v-for="template in item.processTemplateList" :key="template.id" span="6">
              <div class="item" @click="apply(template.id)">
                <img :src="template.iconUrl"/>
                <span>{{template.name}}</span>
              </div>
            </van-col>
          </van-row>
        </van-collapse-item>
      </van-collapse>
    </div>
  </div>
</template>

<script>
import api from '@/api/process'
export default {
  name: "process",

  data() {
    return {
      list: [],
      activeNames: [0,1,2,3,4]
    };
  },

  created(){
    this.fetchData();
  },

  methods: {
    fetchData() {
      api.findProcessType().then(response => {
        console.log(response.data);
        this.list = response.data;
        //全部展开
        this.activeNames = []
        this.list.forEach((item,index) => {
          debugger
          this.activeNames.push(index)
        })
      });
    },

    apply(id) {
      this.$router.push({ path: '/apply/'+id })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding-bottom: 50px;
  .item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    img {
      width: 40px;
      height: 40px;
      border-radius: 10px;
    }
    span {
      font-size: 12px;
      padding: 8px 4px;
      text-align: center;
    }
  }
}
</style>
