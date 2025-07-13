import './assets/main.css'

// 应用启动时立即清除所有会话数据，确保在路由初始化前执行
console.log('应用启动，清除会话数据');
sessionStorage.clear();

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

// 应用启动时立即清除所有会话数据，确保从登录页开始
const app = createApp(App)

sessionStorage.clear();
console.log('Session cleared at startup, token:', sessionStorage.getItem('token'));

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app');
