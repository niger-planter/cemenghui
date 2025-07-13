<template>
  <div class="meeting-list-container">
    <div style="margin-bottom: 16px; text-align: right;">
      <el-button type="primary" @click="showAIHelper = true">智能课程助手</el-button>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.meetingName" placeholder="会议名称" class="search-item"></el-input>
      <el-input v-model="searchForm.creator" placeholder="创建人" class="search-item"></el-input>
      <el-date-picker
        v-model="searchForm.startTime"
        type="date"
        placeholder="开始时间"
        class="search-item"
      ></el-date-picker>
      <el-button type="primary" @click="searchMeetings">搜索</el-button>
      <el-button type="success" @click="exportMeetings">导出</el-button>
      <el-button type="default" @click="resetSearch">重置</el-button>
      <el-button type="primary" @click="showAddMeetingModal" style="margin-left: auto;">新增</el-button>
    </div>

    <el-table :data="meetingList" style="width: 100%">
      <el-table-column prop="meetingName" label="会议名称" width="200"></el-table-column>
      <el-table-column prop="creator" label="创建人" width="120"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180">
        <template #default="scope">{{ formatDate(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180">
        <template #default="scope">{{ formatDate(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success">进行中</el-tag>
          <el-tag v-else type="info">已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditStatus" label="审核状态" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.auditStatus === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.auditStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.auditStatus === 2" type="danger">未通过</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="scope">
          <el-button size="small" @click="showDetailModal(scope.row)">详情</el-button>
          <el-button size="small" @click="showEditMeetingModal(scope.row)">修改</el-button>
          <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    ></el-pagination>

    <!-- 新增/编辑会议弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @open="initQuillEditor">
      <el-form :model="meetingForm" :rules="rules" ref="meetingFormRef" label-width="100px">
        <el-form-item label="会议名称" prop="meetingName">
          <el-input v-model="meetingForm.meetingName"></el-input>
        </el-form-item>
        <el-form-item label="创建人" prop="creator">
          <el-input v-model="meetingForm.creator"></el-input>
        </el-form-item>
        <el-form-item label="用户类型" prop="creatorType">
          <el-select v-model="meetingForm.creatorType" placeholder="请选择用户类型">
            <el-option label="普通用户" value="user" />
            <el-option label="企业用户" value="enterprise" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="meetingForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="meetingForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="会议封面">
          <el-upload
            action="/api/meetings/upload-cover"
            :on-success="handleCoverUpload"
            :show-file-list="false"
          >
            <el-button size="small" type="primary">上传封面</el-button>
          </el-upload>
          <img v-if="meetingForm.coverImage" :src="meetingForm.coverImage" class="cover-image" />
        </el-form-item>
        <el-form-item label="会议内容" prop="content">
          <div ref="quillEditor" style="height: 300px;"></div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMeeting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="30%">
      <span>确定要删除这个会议吗？</span>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="deleteMeeting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 会议详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="会议详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="会议名称">{{ detailMeeting.meetingName }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailMeeting.creator }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ formatCreatorType(detailMeeting.creatorType) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDate(detailMeeting.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDate(detailMeeting.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailMeeting.status === 0" type="success">进行中</el-tag>
          <el-tag v-else type="info">已结束</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="detailMeeting.auditStatus === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailMeeting.auditStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="detailMeeting.auditStatus === 2" type="danger">未通过</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核备注">{{ detailMeeting.auditRemark }}</el-descriptions-item>
        <el-descriptions-item label="会议内容">
          <div v-html="detailMeeting.content"></div>
        </el-descriptions-item>
        <el-descriptions-item label="会议封面">
          <img v-if="detailMeeting.coverImage" :src="detailMeeting.coverImage" class="cover-image" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button v-if="isAdmin && detailMeeting.auditStatus === 0" type="primary" @click="showAuditDialog(detailMeeting)">审核</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="会议审核" width="400px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.auditRemark" type="textarea" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAIHelper" title="智能课程助手" width="600px">
      <el-form :model="aiForm" label-width="100px">
        <el-form-item label="请输入你的问题">
          <el-input v-model="aiForm.input" type="textarea" rows="4" placeholder="如：我想学习数值分析相关课程" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAISubmit">提交</el-button>
        </el-form-item>
      </el-form>
      <el-divider />
      <div v-if="aiResult">
        <h4>AI助手结果：</h4>
        <div v-html="formatAIResult(aiResult)"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';

// 搜索表单
const searchForm = reactive({
  meetingName: '',
  creator: '',
  startTime: null
});

// 会议列表数据
const meetingList = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);

// 弹窗相关
const dialogVisible = ref(false);
const dialogTitle = ref('新增会议');
const deleteDialogVisible = ref(false);
const currentMeetingId = ref(null);

// 表单数据
const meetingForm = reactive({
  id: null,
  meetingName: '',
  creator: '',
  creatorType: 'user',
  startTime: null,
  endTime: null,
  content: '',
  coverImage: ''
});

// 表单验证规则（已取消所有必填项验证）
const rules = {
  meetingName: [],
  creator: [],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' },
    { validator: (rule, value, callback) => {
      if (meetingForm.startTime && value && meetingForm.startTime > value) {
        callback(new Error('结束时间不能早于开始时间'));
      } else {
        callback();
      }
    }, trigger: 'change' }],
  content: []
};

const meetingFormRef = ref(null);
const quillEditor = ref(null);
const quillInstance = ref(null);

// 页面加载时获取会议列表
onMounted(() => {
  searchMeetings();
});

const initQuillEditor = () => {
  nextTick(() => {
    if (!quillInstance.value && quillEditor.value) {
      quillInstance.value = new Quill(quillEditor.value, {
        theme: 'snow',
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'header': [1, 2, 3, false] }],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            [{ 'align': [] }],
            ['link', 'image'],
            ['clean']
          ]
        }
      });
      // 监听内容变化同步到表单
      quillInstance.value.on('text-change', () => {
        meetingForm.content = quillInstance.value.root.innerHTML;
      });
    }
    // 设置编辑器内容
    if (quillInstance.value) {
      if (meetingForm.id) {
        quillInstance.value.root.innerHTML = meetingForm.content;
      } else {
        quillInstance.value.root.innerHTML = '';
      }
    }
  });
};

watch(() => dialogVisible.value, (newVal) => {
  if (newVal) {
    initQuillEditor();
  } else {
    // 关闭弹窗时销毁Quill实例
    if (quillInstance.value) {
      quillInstance.value.destroy();
      quillInstance.value = null;
    }
  }
});

onUnmounted(() => {
  if (quillInstance.value) {
    quillInstance.value = null;
  }
});

// 搜索会议
const searchMeetings = async () => {
  try {
    const response = await axios.get('/api/meetings', {
      params: {
        meetingName: searchForm.meetingName,
        creator: searchForm.creator,
        startTime: searchForm.startTime ? searchForm.startTime.toISOString() : null,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    });
    meetingList.value = response.data.records;
    total.value = response.data.total;
  } catch (error) {
    ElMessage.error('获取会议列表失败');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  searchForm.meetingName = '';
  searchForm.creator = '';
  searchForm.startTime = null;
  pageNum.value = 1;
  searchMeetings();
};

// 导出会议
const exportMeetings = async () => {
  try {
    const response = await axios.get('/api/meetings/export', {
      responseType: 'blob'
    });
    const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'meetings.xlsx';
    a.click();
    URL.revokeObjectURL(url);
  } catch (error) {
    ElMessage.error('导出会议失败');
    console.error(error);
  }
};

// 显示新增会议弹窗
const showAddMeetingModal = () => {
  dialogTitle.value = '新增会议';
  meetingForm.id = null;
  meetingForm.meetingName = '';
  meetingForm.creator = '';
  meetingForm.startTime = null;
  meetingForm.endTime = null;
  meetingForm.content = '';
  meetingForm.coverImage = '';
  dialogVisible.value = true;
};

// 显示编辑会议弹窗
const showEditMeetingModal = async (meeting) => {
  dialogTitle.value = '修改会议';
  currentMeetingId.value = meeting.id;
  // 深拷贝会议数据
  meetingForm.id = meeting.id;
  meetingForm.meetingName = meeting.meetingName;
  meetingForm.creator = meeting.creator;
  meetingForm.startTime = new Date(meeting.startTime);
  meetingForm.endTime = new Date(meeting.endTime);
  meetingForm.content = meeting.content;
  meetingForm.coverImage = meeting.coverImage;
  dialogVisible.value = true;
};

// 保存会议
const saveMeeting = async () => {
  if (!meetingFormRef.value) return;
  try {
    meetingForm.content = quillInstance.value.root.innerHTML;
    await meetingFormRef.value.validate();
    const meetingData = {
      ...meetingForm,
      startTime: meetingForm.startTime ? meetingForm.startTime.toISOString() : null,
      endTime: meetingForm.endTime ? meetingForm.endTime.toISOString() : null,
      creatorType: meetingForm.creatorType
    };
    if (meetingForm.id) {
      await axios.put(`/api/meetings/${meetingForm.id}`, meetingData);
      ElMessage.success('会议更新成功');
    } else {
      await axios.post('/api/meetings', meetingData);
      ElMessage.success('会议创建成功');
    }
    dialogVisible.value = false;
    searchMeetings();
  } catch (error) {
    if (error.name === 'ValidationError') return;
    const errorMsg = error.response?.data?.message || error.message || '未知错误';
    ElMessage.error(`${meetingForm.id ? '会议更新失败' : '会议创建失败'}: ${errorMsg}`);
  }
};

// 确认删除
const confirmDelete = (id) => {
  currentMeetingId.value = id;
  deleteDialogVisible.value = true;
};

// 删除会议
const deleteMeeting = async () => {
  try {
    await axios.delete(`/api/meetings/${currentMeetingId.value}`);
    ElMessage.success('会议删除成功');
    deleteDialogVisible.value = false;
    searchMeetings();
  } catch (error) {
    ElMessage.error('会议删除失败');
    console.error(error);
  }
};

// 处理封面上传
const handleCoverUpload = (response) => {
  if (response.success) {
    meetingForm.coverImage = response.data;
    ElMessage.success('封面上传成功');
  } else {
    ElMessage.error('封面上传失败');
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val;
  searchMeetings();
};

const handleCurrentChange = (val) => {
  pageNum.value = val;
  searchMeetings();
};

const detailDialogVisible = ref(false);
const detailMeeting = reactive({});
const auditDialogVisible = ref(false);
const auditForm = reactive({ auditStatus: 1, auditRemark: '' });
const isAdmin = ref(false); // TODO: 根据实际登录用户身份判断

const showDetailModal = async (meeting) => {
  try {
    const response = await axios.get(`/api/meetings/${meeting.id}`);
    Object.assign(detailMeeting, response.data.data);
    detailDialogVisible.value = true;
  } catch (error) {
    ElMessage.error('获取会议详情失败');
  }
};

const showAuditDialog = (meeting) => {
  auditForm.auditStatus = 1;
  auditForm.auditRemark = '';
  detailMeeting.id = meeting.id;
  auditDialogVisible.value = true;
};

const submitAudit = async () => {
  try {
    await axios.put(`/api/meetings/${detailMeeting.id}/audit`, {
      auditStatus: auditForm.auditStatus,
      auditRemark: auditForm.auditRemark
    });
    ElMessage.success('审核成功');
    auditDialogVisible.value = false;
    detailDialogVisible.value = false;
    searchMeetings();
  } catch (error) {
    ElMessage.error('审核失败');
  }
};

const formatCreatorType = (type) => {
  if (type === 'enterprise') return '企业用户';
  if (type === 'admin') return '管理员';
  return '普通用户';
};

const showAIHelper = ref(false);
const aiForm = reactive({ input: '' });
const aiResult = ref('');

const API_KEY = 'app-NtFMcgysqOoEM3PJhVteN3Bz';
const BASE_URL = 'https://api.dify.ai/v1';

const handleAISubmit = async () => {
  aiResult.value = '';
  const payload = {
    inputs: { input: aiForm.input },
    response_mode: 'blocking',
    user: 'web-user'
  };
  try {
    const res = await axios.post(BASE_URL + '/workflows/run', payload, {
      headers: {
        Authorization: `Bearer ${API_KEY}`,
        'Content-Type': 'application/json'
      }
    });
    // 只展示主要输出内容
    aiResult.value = res.data?.data?.outputs?.text || JSON.stringify(res.data, null, 2);
  } catch (e) {
    aiResult.value = e?.response?.data?.error || '请求失败';
  }
};

function formatAIResult(text) {
  if (!text) return '';
  // 先替换\n为<br>，再将分号后加<br>，避免重复换行
  return text
    .replace(/\n/g, '<br>')
    .replace(/；/g, '；<br>')
    .replace(/;/g, ';<br>');
}
</script>

<style scoped>
.meeting-list-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
  align-items: center;
}

.search-item {
  width: 200px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.cover-image {
  width: 100px;
  height: 100px;
  margin-top: 10px;
  object-fit: cover;
}
</style>