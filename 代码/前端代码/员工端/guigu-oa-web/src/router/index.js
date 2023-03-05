import Vue from 'vue';
import VueRouter from 'vue-router';
Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'OA审批',
    component: () =>
      import('../views/index.vue'),
  },
  {
    path: '/apply/:processTemplateId',
    name: 'Apply',
    component: () =>
      import('../views/apply.vue'),
  },
  {
    path: '/list/:activeIndex',
    name: '审批列表',
    component: () =>
      import('../views/list.vue'),
  },
  {
    path: '/show/:id/:taskId',
    name: '审批详情',
    component: () =>
      import('../views/show.vue'),
  },
  {
    path: '/user',
    name: '基本信息',
    component: () =>
      import('../views/user.vue'),
  },
  {
    path: '/about',
    name: '关于我们',
    component: () =>
      import('../views/about.vue'),
  },
  {
    path: '/test',
    name: '测试',
    component: () =>
      import('../views/test.vue'),
  }
];

const router = new VueRouter({
  routes,
});

export default router;
