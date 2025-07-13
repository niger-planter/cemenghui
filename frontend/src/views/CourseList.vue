<template>
  <div class="course-list-container">
    <div style="margin-bottom: 16px; text-align: right;">
      <el-button type="primary" @click="showAddCourseModal">新增课程</el-button>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.courseName" placeholder="课程名称" class="search-item"></el-input>
      <el-input v-model="searchForm.author" placeholder="作者" class="search-item"></el-input>
      <el-button type="primary" @click="searchCourses">搜索</el-button>
      <el-button type="default" @click="resetSearch">重置</el-button>
    </div>
    <el-table :data="courseList" style="width: 100%; margin-top: 16px;">
      <el-table-column prop="coverImage" label="封面" width="100">
        <template #default="scope">
          <img v-if="scope.row.coverImage" :src="scope.row.coverImage" style="width: 60px; height: 60px; object-fit: cover;" />
        </template>
      </el-table-column>
      <el-table-column prop="courseName" label="课程名称" width="180" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="description" label="简介" />
      <el-table-column prop="orderNum" label="排序" width="80" />
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
          <el-button size="small" @click="showEditCourseModal(scope.row)">修改</el-button>
          <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
          <el-button v-if="scope.row.auditStatus === 0" size="small" type="primary" @click="showAuditDialog(scope.row)">审核</el-button>
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
      style="margin-top: 20px; text-align: right;"
    ></el-pagination>

    <!-- 新增/编辑课程弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="courseForm" label-width="100px">
        <el-form-item label="课程名称" required>
          <el-input v-model="courseForm.courseName" />
        </el-form-item>
        <el-form-item label="作者" required>
          <el-input v-model="courseForm.author" />
        </el-form-item>
        <el-form-item label="简介" required>
          <el-input v-model="courseForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="排序" required>
          <el-input-number v-model="courseForm.orderNum" :min="1" />
        </el-form-item>
        <el-form-item label="视频地址">
          <el-upload
            class="video-uploader"
            action="/api/courses/upload-video"
            :show-file-list="false"
            :on-success="handleVideoUpload"
            :before-upload="beforeVideoUpload"
            accept="video/mp4,video/avi,video/mov"
          >
            <el-button type="primary">上传视频</el-button>
            <span v-if="courseForm.videoUrl" style="margin-left: 10px; color: #409EFF;">已上传</span>
          </el-upload>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="avatar-uploader"
            action="/api/courses/upload-cover"
            :show-file-list="false"
            :on-success="handleCoverUpload"
            :before-upload="beforeUpload"
            accept="image/png,image/jpeg,image/jpg"
          >
            <img v-if="courseForm.coverImage" :src="courseForm.coverImage" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="30%">
      <span>确定要删除这个课程吗？</span>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="deleteCourse">确定</el-button>
      </template>
    </el-dialog>

    <!-- 课程详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="课程详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="课程名称">{{ detailCourse.courseName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ detailCourse.author }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ detailCourse.description }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailCourse.orderNum }}</el-descriptions-item>
        <el-descriptions-item label="视频">
          <a :href="detailCourse.videoUrl" target="_blank">{{ detailCourse.videoUrl }}</a>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="detailCourse.auditStatus === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailCourse.auditStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="detailCourse.auditStatus === 2" type="danger">未通过</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="封面">
          <img v-if="detailCourse.coverImage" :src="detailCourse.coverImage" style="width: 120px;" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="课程审核" width="400px">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  courseName: '',
  author: ''
})
const courseList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const deleteDialogVisible = ref(false)
const currentCourseId = ref(null)
const courseForm = reactive({
  id: null,
  courseName: '',
  author: '',
  description: '',
  orderNum: 1,
  videoUrl: '',
  coverImage: ''
})
const detailDialogVisible = ref(false)
const detailCourse = reactive({})
const auditDialogVisible = ref(false)
const auditForm = reactive({ auditStatus: 1, auditRemark: '' })

const fetchCourses = async () => {
  const res = await axios.get('/api/courses', {
    params: {
      courseName: searchForm.courseName,
      author: searchForm.author,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  })
  courseList.value = res.data.records
  total.value = res.data.total
}
const searchCourses = () => {
  pageNum.value = 1
  fetchCourses()
}
const resetSearch = () => {
  searchForm.courseName = ''
  searchForm.author = ''
  pageNum.value = 1
  fetchCourses()
}
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourses()
}
const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchCourses()
}
onMounted(fetchCourses)
const showAddCourseModal = () => {
  dialogTitle.value = '新增课程'
  Object.assign(courseForm, { id: null, courseName: '', author: '', description: '', orderNum: 1, videoUrl: '', coverImage: '' })
  dialogVisible.value = true
}
const showEditCourseModal = (course) => {
  dialogTitle.value = '编辑课程'
  Object.assign(courseForm, course)
  dialogVisible.value = true
}
const saveCourse = async () => {
  if (!courseForm.courseName || !courseForm.author || !courseForm.description) {
    ElMessage.error('请填写完整信息')
    return
  }
  if (courseForm.id) {
    await axios.put(`/api/courses/${courseForm.id}`, courseForm)
    ElMessage.success('课程更新成功')
  } else {
    await axios.post('/api/courses', courseForm)
    ElMessage.success('课程创建成功')
  }
  dialogVisible.value = false
  fetchCourses()
}
const confirmDelete = (id) => {
  currentCourseId.value = id
  deleteDialogVisible.value = true
}
const deleteCourse = async () => {
  await axios.delete(`/api/courses/${currentCourseId.value}`)
  ElMessage.success('课程删除成功')
  deleteDialogVisible.value = false
  fetchCourses()
}
const showDetailModal = async (course) => {
  const res = await axios.get(`/api/courses/${course.id}`)
  Object.assign(detailCourse, res.data.data)
  detailDialogVisible.value = true
}
const showAuditDialog = (course) => {
  auditForm.auditStatus = course.auditStatus ?? 1;
  auditForm.auditRemark = course.auditRemark || '';
  detailCourse.id = course.id;
  auditDialogVisible.value = true;
}
const submitAudit = async () => {
  try {
    await axios.put(`/api/courses/${detailCourse.id}/audit`, {
      auditStatus: auditForm.auditStatus,
      auditRemark: auditForm.auditRemark
    });
    ElMessage.success('审核成功');
    auditDialogVisible.value = false;
    await fetchCourses();
  } catch (e) {
    ElMessage.error('审核失败');
  }
}
const handleCoverUpload = (response) => {
  if (response.success) {
    let url = response.data;
    url = url.replace(/\\/g, '/');
    if (!url.startsWith('/')) url = '/' + url;
    courseForm.coverImage = url;
    ElMessage.success('封面上传成功');
  } else {
    ElMessage.error('封面上传失败');
  }
}
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 图片!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}
const handleVideoUpload = (response) => {
  if (response.success) {
    let url = response.data;
    url = url.replace(/\\/g, '/');
    if (!url.startsWith('/')) url = '/' + url;
    courseForm.videoUrl = url;
    ElMessage.success('视频上传成功');
  } else {
    ElMessage.error('视频上传失败');
  }
}
const beforeVideoUpload = (file) => {
  const isVideo = file.type === 'video/mp4' || file.type === 'video/avi' || file.type === 'video/mov';
  const isLt100M = file.size / 1024 / 1024 < 100;
  if (!isVideo) {
    ElMessage.error('只能上传 mp4/avi/mov 格式的视频!');
  }
  if (!isLt100M) {
    ElMessage.error('视频大小不能超过 100MB!');
  }
  return isVideo && isLt100M;
}
</script>

<style scoped>
.course-list-container {
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
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
}
.avatar {
  width: 80px;
  height: 80px;
  display: block;
}
.video-uploader .el-upload {
  display: inline-block;
}
</style> 