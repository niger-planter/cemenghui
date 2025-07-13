<template>
  <div class="ai-helper-container">
    <h2>智能课程助手</h2>
    <el-form v-if="formConfig.length" :model="formData" label-width="100px">
      <template v-for="item in formConfig" :key="item.variable">
        <el-form-item v-if="item.type === 'text' || item.type === 'paragraph'" :label="item.label">
          <el-input v-model="formData[item.variable]" :type="item.type === 'paragraph' ? 'textarea' : 'text'" />
        </el-form-item>
        <el-form-item v-else-if="item.type === 'select'" :label="item.label">
          <el-select v-model="formData[item.variable]">
            <el-option v-for="opt in item.options" :key="opt" :label="opt" :value="opt" />
          </el-select>
        </el-form-item>
        <!-- 文件上传等其他类型可后续扩展 -->
      </template>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </el-form-item>
    </el-form>
    <div v-else>正在加载表单配置...</div>
    <el-divider />
    <div v-if="result">
      <h3>AI助手结果：</h3>
      <pre>{{ result }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import axios from 'axios';

const formConfig = ref<any[]>([]);
const formData = reactive<any>({});
const result = ref('');

const API_KEY = 'app-NtFMcgysqOoEM3PJhVteN3Bz';
const BASE_URL = 'https://api.dify.ai/v1';

onMounted(async () => {
  // 获取参数配置
  const res = await axios.get(BASE_URL + '/parameters', {
    headers: { Authorization: `Bearer ${API_KEY}` }
  });
  if (res.data && res.data.user_input_form) {
    formConfig.value = res.data.user_input_form.map((item: any) => {
      if (item.text) return { ...item.text, type: 'text' };
      if (item.paragraph) return { ...item.paragraph, type: 'paragraph' };
      if (item.select) return { ...item.select, type: 'select', options: item.select.options };
      return null;
    }).filter(Boolean);
    formConfig.value.forEach((item: any) => {
      formData[item.variable] = item.default || '';
    });
  }
});

const handleSubmit = async () => {
  result.value = '';
  const payload = {
    inputs: { ...formData },
    response_mode: 'blocking',
    user: sessionStorage.getItem('username') || 'web-user'
  };
  try {
    const res = await axios.post(BASE_URL + '/workflows/run', payload, {
      headers: {
        Authorization: `Bearer ${API_KEY}`,
        'Content-Type': 'application/json'
      }
    });
    result.value = JSON.stringify(res.data, null, 2);
  } catch (e: any) {
    result.value = e?.response?.data?.error || '请求失败';
  }
};
</script>

<style scoped>
.ai-helper-container {
  max-width: 600px;
  margin: 40px auto;
  background: #fff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
</style> 