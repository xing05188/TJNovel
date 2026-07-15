<template>
  <div class="home-view">
    <h2>我的举报</h2>
    <div v-if="loading">加载中...</div>
    <div v-else-if="error">{{ error }}</div>
    <div v-else-if="reports.length === 0">暂无举报记录</div>
    <div v-else>
      <div v-for="report in reports" :key="report.reportId" class="report-item">
        <p><strong>举报ID:</strong> {{ report.reportId || '未知' }}</p>
        <p v-if="report.reason"><strong>举报原因:</strong> {{ report.reason }}</p>
        <p v-if="report.reportTime"><strong>举报时间:</strong> {{ formatTime(report.reportTime) }}</p>
        <p v-if="report.progress"><strong>处理状态:</strong> 
          <span :class="getProgressClass(report.progress)">{{ report.progress }}</span>
        </p>
        <p v-if="report.commentId"><strong>被举报评论ID:</strong> {{ report.commentId }}</p>
        <p v-if="report.commentContent"><strong>被举报评论内容:</strong> {{ report.commentContent }}</p>

        <div v-if="report.managementLogs && report.managementLogs.length > 0" class="managementLogs">
          <h4>处理日志 ({{ report.managementLogs.length }}条):</h4>
          <ul>
            <li v-for="(log, index) in report.managementLogs" :key="log.managementId || index">
              <strong>处理员ID:</strong> {{ log.managerId || '未知' }}, 
              <strong>处理结果:</strong> {{ log.result || '未知' }}, 
              <strong>处理时间:</strong> {{ formatTime(log.time) }}
            </li>
          </ul>
        </div>
        <div v-else class="no-logs">
          <p>暂无处理日志</p>
        </div>
        <hr>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getReportManagementLogs } from '@/API/Reader_API'
import { getAllReports } from '@/API/Report_API'
import { readerState } from '@/stores/index'
import { ref, onMounted } from 'vue'

const store = readerState()
const reports = ref([])
const loading = ref(false)
const error = ref(null)

async function fetchReports() {
  loading.value = true
  error.value = null
  try {
    if (!store.readerId) {
      throw new Error('未检测到登录读者ID')
    }
    
    // 获取所有举报，然后过滤出该读者的举报
    const allReportsResponse = await getAllReports()
    let allReports = []
    if (Array.isArray(allReportsResponse)) {
      allReports = allReportsResponse
    } else if (allReportsResponse?.data && Array.isArray(allReportsResponse.data)) {
      allReports = allReportsResponse.data
    }
    
    // 过滤出该读者的举报
    const readerReports = allReports.filter(report => 
      report && report.readerId && Number(report.readerId) === Number(store.readerId)
    )
    
    // 为每个举报获取管理日志
    const reportsWithLogs = await Promise.all(
      readerReports.map(async (report) => {
        let managementLogs = []
        try {
          // 获取该举报的管理日志
          const logsResponse = await getReportManagementLogs(report.reportId)
          let logs = []
          if (Array.isArray(logsResponse)) {
            logs = logsResponse
          } else if (logsResponse?.data && Array.isArray(logsResponse.data)) {
            logs = logsResponse.data
          }
          
          // 转换管理日志格式
          managementLogs = logs.map(log => ({
            managementId: log.managementId || log.management_id,
            managerId: log.managerId || log.manager_id,
            result: log.result,
            time: log.time
          }))
        } catch (e) {
          console.warn(`获取举报 ${report.reportId} 的管理日志失败:`, e)
        }
        
        return {
          reportId: report.reportId,
          reason: report.reason,
          reportTime: report.reportTime,
          progress: report.progress || '未处理',
          commentId: report.commentId,
          commentContent: null, // 可以后续添加获取评论内容的逻辑
          readerId: report.readerId,
          managementLogs: managementLogs
        }
      })
    )
    
    // 按举报时间倒序排列
    reportsWithLogs.sort((a, b) => {
      const timeA = a.reportTime ? new Date(a.reportTime).getTime() : 0
      const timeB = b.reportTime ? new Date(b.reportTime).getTime() : 0
      return timeB - timeA
    })
    
    reports.value = reportsWithLogs
    console.log('举报记录加载完成：', reports.value)
  } catch (err) {
    console.error('获取举报记录失败:', err)
    error.value = err.message || '加载举报记录失败'
    reports.value = []
  } finally {
    loading.value = false
  }
}

function formatTime(timeStr) {
  if (!timeStr) return '未知'
  try {
    const d = new Date(timeStr)
    return d.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (e) {
    return timeStr
  }
}

function getProgressClass(progress) {
  if (progress === '成功') return 'progress-success'
  if (progress === '失败') return 'progress-failed'
  return 'progress-pending'
}

onMounted(fetchReports)
</script>

<style scoped>
.home-view {
  padding: 20px;
}

.report-item {
  background-color: white ;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 15px 20px;
  margin-bottom: 12px;
  box-shadow: 0 2px 5px rgb(0 0 0 / 0.1);
  transition: box-shadow 0.3s ease;
}

.report-item:hover {
  box-shadow: 0 4px 10px rgb(0 0 0 / 0.15);
}

.report-item p {
  margin: 6px 0;
  color: #333;
  font-size: 14px;
}

.report-item p:first-child {
  font-weight: 600;
  font-size: 16px;
  color: #222;
}

.report-item p:nth-child(3),
.report-item p:nth-child(4) {
  color: #666;
  font-size: 13px;
}

.report-item p:nth-child(4) {
  font-weight: 600;
}

.managementLogs {
  margin-top: 12px;
  padding-left: 15px;
  border-left: 3px solid #409eff;
}

.managementLogs h4 {
  margin-bottom: 6px;
  color: #409eff;
  font-size: 14px;
  font-weight: 600;
}

.managementLogs ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.managementLogs li {
  margin-bottom: 6px;
  font-size: 13px;
  color: #555;
}

hr {
  border: none;
  border-bottom: 1px solid #eee;
  margin: 15px 0 0;
}

.no-logs {
  margin-top: 12px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
  color: #999;
  font-size: 13px;
}

.progress-success {
  color: #67c23a;
  font-weight: 600;
}

.progress-failed {
  color: #f56c6c;
  font-weight: 600;
}

.progress-pending {
  color: #e6a23c;
  font-weight: 600;
}
</style>
