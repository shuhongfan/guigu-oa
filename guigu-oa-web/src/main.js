import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import Vant from 'vant';
import 'vant/lib/index.css';
import axios from 'axios';
import VueAxios from 'vue-axios';
Vue.use(Vant);
Vue.config.productionTip = false;
Vue.use(VueAxios, axios);

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n
Vue.use(ElementUI, { locale })

import formCreate from '@form-create/element-ui'
Vue.use(formCreate)

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
