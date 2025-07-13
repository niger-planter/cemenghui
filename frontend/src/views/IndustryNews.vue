<template>
  <div class="industry-news-container">
    <div class="page-header">
      <h2>行业新闻</h2>
      <el-button type="primary" @click="showAddNewsModal">发布新闻</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="searchForm.title" placeholder="新闻标题" class="search-item" @change="searchNews"></el-input>
      <el-input v-model="searchForm.author" placeholder="作者" class="search-item" @change="searchNews"></el-input>
      <el-select v-model="searchForm.category" placeholder="新闻分类" class="search-item" clearable @change="searchNews">
        <el-option label="全部" value="" />
        <el-option label="技术动态" value="tech" />
        <el-option label="行业趋势" value="trend" />
        <el-option label="政策解读" value="policy" />
        <el-option label="企业动态" value="company" />
      </el-select>
      <el-date-picker
        v-model="searchForm.publishDate"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        class="search-item"
        @change="searchNews"
      ></el-date-picker>
      <el-button type="primary" @click="searchNews">搜索</el-button>
      <el-button type="default" @click="resetSearch">重置</el-button>
    </div>

    <div class="news-grid">
      <div v-for="news in newsList" :key="news.id" class="news-card">
        <div class="news-image">
          <img v-if="news.coverImage" :src="news.coverImage" :alt="news.title" />
          <div v-else class="no-image">暂无图片</div>
        </div>
        <div class="news-content">
          <h3 class="news-title" @click="showDetailModal(news)">{{ news.title }}</h3>
          <p class="news-summary">{{ news.summary }}</p>
          <div class="news-meta">
            <span class="author">{{ news.author }}</span>
            <span class="category">{{ formatCategory(news.category) }}</span>
            <span class="date">{{ formatDate(news.publishDate) }}</span>
          </div>
          <div class="news-actions">
            <el-button size="small" @click="showDetailModal(news)">查看详情</el-button>
            <el-button size="small" @click="showEditNewsModal(news)">编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(news.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[6, 12, 24]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    ></el-pagination>

    <!-- 新增/编辑新闻弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="2400px" @open="initQuillEditor">
      <el-form :model="newsForm" :rules="rules" ref="newsFormRef" label-width="100px">
        <el-form-item label="新闻标题" prop="title">
          <el-input v-model="newsForm.title"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="newsForm.author"></el-input>
        </el-form-item>
        <el-form-item label="新闻分类" prop="category">
          <el-select v-model="newsForm.category" placeholder="请选择新闻分类">
            <el-option label="技术动态" value="tech" />
            <el-option label="行业趋势" value="trend" />
            <el-option label="政策解读" value="policy" />
            <el-option label="企业动态" value="company" />
          </el-select>
        </el-form-item>
        <el-form-item label="新闻摘要" prop="summary">
          <el-input v-model="newsForm.summary" type="textarea" rows="3"></el-input>
        </el-form-item>
        <el-form-item label="发布日期" prop="publishDate">
          <el-date-picker
            v-model="newsForm.publishDate"
            type="datetime"
            placeholder="选择发布日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="新闻封面">
          <el-upload
            action="/api/news/upload-cover"
            :on-success="handleCoverUpload"
            :show-file-list="false"
          >
            <el-button size="small" type="primary">上传封面</el-button>
          </el-upload>
          <img v-if="newsForm.coverImage" :src="newsForm.coverImage" class="cover-image" />
        </el-form-item>
        <el-form-item label="新闻内容" prop="content">
          <div ref="quillEditor" class="quill-editor"></div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNews">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="800px">
      <span>确定要删除这条新闻吗？</span>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="deleteNews">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新闻详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="新闻详情" width="2400px">
      <div class="news-detail">
        <h2>{{ detailNews.title }}</h2>
        <div class="news-meta-detail">
          <span class="author">作者：{{ detailNews.author }}</span>
          <span class="category">分类：{{ formatCategory(detailNews.category) }}</span>
          <span class="date">发布时间：{{ formatDate(detailNews.publishDate) }}</span>
        </div>
        <div v-if="detailNews.coverImage" class="news-cover">
          <img :src="detailNews.coverImage" :alt="detailNews.title" />
        </div>
        <div class="news-summary-detail">
          <strong>摘要：</strong>{{ detailNews.summary }}
        </div>
        <div class="news-content-detail" v-html="detailNews.content"></div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
  category: '',
  publishDate: null
});

// 新闻列表数据
const newsList = ref([]);
const pageNum = ref(1);
const pageSize = ref(12);
const total = ref(0);

// 弹窗控制
const dialogVisible = ref(false);
const deleteDialogVisible = ref(false);
const detailDialogVisible = ref(false);
const dialogTitle = ref('');

// 表单数据
const newsForm = reactive({
  id: null,
  title: '',
  author: '',
  category: '',
  summary: '',
  content: '',
  coverImage: '',
  publishDate: null
});

// 详情数据
const detailNews = ref({});

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  category: [{ required: true, message: '请选择新闻分类', trigger: 'change' }],
  summary: [{ required: true, message: '请输入新闻摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入新闻内容', trigger: 'blur' }],
  publishDate: [{ required: true, message: '请选择发布日期', trigger: 'change' }]
};

const newsFormRef = ref();
let quillEditor = null;

// 初始化富文本编辑器
const initQuillEditor = () => {
  nextTick(() => {
    const editorContainer = document.querySelector('.quill-editor');
    if (!editorContainer) return;
    if (quillEditor) {
      quillEditor = null;
      editorContainer.innerHTML = '';
    }
    quillEditor = new Quill(editorContainer, {
      theme: 'snow',
      modules: {
        toolbar: [
          ['bold', 'italic', 'underline', 'strike'],
          ['blockquote', 'code-block'],
          [{ 'header': 1 }, { 'header': 2 }],
          [{ 'list': 'ordered'}, { 'list': 'bullet' }],
          [{ 'script': 'sub'}, { 'script': 'super' }],
          [{ 'indent': '-1'}, { 'indent': '+1' }],
          [{ 'direction': 'rtl' }],
          [{ 'size': ['small', false, 'large', 'huge'] }],
          [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
          [{ 'color': [] }, { 'background': [] }],
          [{ 'font': [] }],
          [{ 'align': [] }],
          ['clean'],
          ['link', 'image', 'video']
        ]
      }
    });
    if (newsForm.content) {
      quillEditor.root.innerHTML = newsForm.content;
    }
  });
};

// 获取新闻列表
const getNewsList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    };
    if (searchForm.publishDate && searchForm.publishDate.length === 2) {
      params.publishDateStart = searchForm.publishDate[0];
      params.publishDateEnd = searchForm.publishDate[1];
    }
    delete params.publishDate;
    const response = await axios.get('/api/news', { params });
    if (response.data.success) {
      newsList.value = response.data.records;
      total.value = response.data.total;
    }
  } catch (error) {
    ElMessage.error('获取新闻列表失败');
    console.error(error);
  }
};

// 搜索新闻
const searchNews = () => {
  pageNum.value = 1;
  getNewsList();
};

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = '';
  });
  searchForm.publishDate = null;
  pageNum.value = 1;
  getNewsList();
};

// 显示新增新闻弹窗
const showAddNewsModal = () => {
  dialogTitle.value = '发布新闻';
  Object.keys(newsForm).forEach(key => {
    newsForm[key] = '';
  });
  newsForm.publishDate = new Date();
  dialogVisible.value = true;
};

// 显示编辑新闻弹窗
const showEditNewsModal = (news) => {
  dialogTitle.value = '编辑新闻';
  Object.assign(newsForm, news);
  dialogVisible.value = true;
};

// 显示详情弹窗
const showDetailModal = (news) => {
  detailNews.value = news;
  detailDialogVisible.value = true;
};

// 保存新闻
const saveNews = async () => {
  // 1. 确保内容同步
  if (quillEditor) {
    newsForm.content = quillEditor.root.innerHTML;
  }
  // 2. 手动触发表单校验
  await newsFormRef.value.validate(async (valid) => {
    if (!valid) return;
    const url = newsForm.id ? `/api/news/${newsForm.id}` : '/api/news';
    const method = newsForm.id ? 'put' : 'post';
    try {
      const response = await axios[method](url, newsForm);
      if (response.data.success) {
        ElMessage.success(newsForm.id ? '新闻更新成功' : '新闻发布成功');
        dialogVisible.value = false;
        getNewsList();
      } else {
        ElMessage.error(response.data.message);
      }
    } catch (error) {
      ElMessage.error('操作失败');
      console.error(error);
    }
  });
};

// 确认删除
const confirmDelete = (id) => {
  newsForm.id = id;
  deleteDialogVisible.value = true;
};

// 删除新闻
const deleteNews = async () => {
  try {
    const response = await axios.delete(`/api/news/${newsForm.id}`);
    if (response.data.success) {
      ElMessage.success('新闻删除成功');
      deleteDialogVisible.value = false;
      getNewsList();
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    ElMessage.error('删除失败');
    console.error(error);
  }
};

// 处理封面上传
const handleCoverUpload = (response) => {
  if (response.success) {
    newsForm.coverImage = response.data;
    ElMessage.success('封面上传成功');
  } else {
    ElMessage.error(response.message);
  }
};

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  pageNum.value = 1;
  getNewsList();
};

const handleCurrentChange = (val) => {
  pageNum.value = val;
  getNewsList();
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString('zh-CN');
};

// 格式化分类
const formatCategory = (category) => {
  const categoryMap = {
    'tech': '技术动态',
    'trend': '行业趋势',
    'policy': '政策解读',
    'company': '企业动态'
  };
  return categoryMap[category] || category;
};

// 组件挂载时获取数据
onMounted(() => {
  getNewsList();
});

// 组件卸载时清理编辑器
onUnmounted(() => {
  if (quillEditor) {
    quillEditor.destroy();
  }
});
</script>

<style scoped>
.industry-news-container {
  padding: 48px 0 48px 0;
  max-width: 2400px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.06);
  border-radius: 18px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
  flex-wrap: wrap;
  align-items: center;
}

.search-item {
  width: 220px;
}

.news-grid {
  display: flex;
  flex-direction: column;
  gap: 32px;
  margin-bottom: 32px;
}

.news-card {
  border: 1px solid #e4e7ed;
  border-radius: 14px;
  overflow: hidden;
  transition: box-shadow 0.3s;
  background: white;
  display: flex;
  flex-direction: row;
  min-height: 260px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.07);
}

.news-card:hover {
  box-shadow: 0 8px 32px 0 rgba(0,0,0,0.12);
}

.news-image {
  width: 420px;
  height: 260px;
  flex-shrink: 0;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 14px;
}

.news-content {
  flex: 1;
  padding: 38px 56px 38px 56px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.news-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
  cursor: pointer;
  line-height: 1.4;
}

.news-title:hover {
  color: #409eff;
}

.news-summary {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 12px;
  color: #999;
}

.news-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  text-align: center;
  margin-top: 20px;
}

.cover-image {
  max-width: 200px;
  max-height: 150px;
  margin-top: 10px;
  border-radius: 4px;
}

.news-detail {
  line-height: 1.6;
}

.news-detail h2 {
  margin-bottom: 15px;
  color: #333;
}

.news-meta-detail {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.news-cover {
  margin-bottom: 20px;
  text-align: center;
}

.news-cover img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
}

.news-summary-detail {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  color: #666;
}

.news-content-detail {
  line-height: 1.8;
  color: #333;
}

.quill-editor {
  min-height: 220px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  margin-top: 4px;
}

@media (max-width: 1024px) {
  .industry-news-container {
    max-width: 100vw;
    border-radius: 0;
    box-shadow: none;
    padding: 20px 0;
  }
  .news-card {
    flex-direction: column;
    min-height: unset;
  }
  .news-image {
    width: 100%;
    height: 180px;
  }
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-item {
    width: 100%;
  }
  
  .news-grid {
    grid-template-columns: 1fr;
  }
  
  .news-meta {
    flex-direction: column;
    gap: 5px;
  }
}
</style> 