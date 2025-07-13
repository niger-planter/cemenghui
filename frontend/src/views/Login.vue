<template>
  <div class="login-container">
    <el-form :model="form" ref="formRef" label-width="80px">
      <el-form-item label="账号" prop="username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onLogin">登录</el-button>
        <el-button @click="toRegister">没有账号？去注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import userApi from '../services/user';
import { ElMessage } from 'element-plus';

const router = useRouter();
const formRef = ref();
const form = ref({
  username: '',
  password: ''
});

const onLogin = async () => {
  try {
    const res = await userApi.login(form.value);
    if (res.data && (res.data.success || res.data.code === 0)) {
      localStorage.setItem('user', JSON.stringify(res.data.data));
      ElMessage.success('登录成功');
      router.push('/meetings');
    } else {
      ElMessage.error(res.data.message || '登录失败');
    }
  } catch (e) {
    ElMessage.error('请求失败，请检查网络或联系管理员');
    console.error(e);
  }
};

const toRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 80px auto;
  background: #fff;
  padding: 32px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.08);
}
</style> 