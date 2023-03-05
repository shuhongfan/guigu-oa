<template>
  <div class="app-container">
    <div style="padding: 20px 20px 0 20px;">
      授权角色：{{ $route.query.roleName }}
    </div>
    <el-tree
      style="margin: 20px 0"
      ref="tree"
      :data="sysMenuList"
      node-key="id"
      show-checkbox
      default-expand-all
      :props="defaultProps"
    />
    <div style="padding: 20px 20px;">
      <el-button :loading="loading" type="primary" icon="el-icon-check" size="mini" @click="save">保存</el-button>
      <el-button @click="$router.push('/system/sysRole')" size="mini" icon="el-icon-refresh-right">返回</el-button>
    </div>
  </div>
</template>
<script>
  import api from '@/api/system/sysMenu'
  export default {
    name: 'roleAuth',

    data() {
      return {
        loading: false, // 用来标识是否正在保存请求中的标识, 防止重复提交
        sysMenuList: [], // 所有
        defaultProps: {
          children: 'children',
          label: 'name'
        },
      };
    },

    created() {
      this.fetchData()
    },

    methods: {
      /*
      初始化
      */
      fetchData() {
        const roleId = this.$route.query.id
        api.toAssign(roleId).then(result => {
          const sysMenuList = result.data
          this.sysMenuList = sysMenuList
          const checkedIds = this.getCheckedIds(sysMenuList)
          console.log('getPermissions() checkedIds', checkedIds)
          this.$refs.tree.setCheckedKeys(checkedIds)
        })
      },

      /*
      得到所有选中的id列表
      */
      getCheckedIds (auths, initArr = []) {
        return auths.reduce((pre, item) => {
          if (item.select && item.children.length === 0) {
            pre.push(item.id)
          } else if (item.children) {
            this.getCheckedIds(item.children, initArr)
          }
          return pre
        }, initArr)
      },

      /*
      保存权限列表
      */
      save() {
        //获取到当前子节点
        //const checkedNodes = this.$refs.tree.getCheckedNodes()
        //获取到当前子节点及父节点
        const allCheckedNodes = this.$refs.tree.getCheckedNodes(false, true);
        let idList = allCheckedNodes.map(node => node.id);
        console.log(idList)
        let assginMenuVo = {
          roleId: this.$route.query.id,
          menuIdList: idList
        }
        this.loading = true
        api.doAssign(assginMenuVo).then(result => {
          this.loading = false
          this.$message.success(result.$message || '分配权限成功')
          this.$router.push('/system/sysRole');
        })
      }
    }
  };
</script>