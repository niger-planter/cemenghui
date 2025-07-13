<script setup lang="ts">
import TheWelcome from '../components/TheWelcome.vue'
</script>

<template>
  <div>
    <h1>首页</h1>
    <ul>
      <li><router-link to="/meetings">会议管理</router-link></li>
      <li><router-link to="/courses">课程管理</router-link></li>
    </ul>
  </div>
</template>
