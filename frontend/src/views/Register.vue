<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onRegister">注册</el-button>
        <el-button type="text" @click="goLogin">已有账号？去登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios'; // Added axios import

const router = useRouter();
const formRef = ref();
const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
});

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== form.value.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      }, trigger: 'blur' }
  ]
};

const onRegister = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    // 补全后端需要的字段
    const payload = {
      username: form.value.username,
      password: form.value.password,
      nickname: form.value.username, // 用用户名做昵称
      role: 'user',
      enabled: true
    };
    // 这里应调用后端注册接口
    await axios.post('/api/user/register', payload);
    ElMessage.success('注册成功！');
    router.push('/login');
  });
};

const goLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 60px auto;
  background: #fff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
</style> 