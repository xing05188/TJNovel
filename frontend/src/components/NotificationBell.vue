<template>
  <div class="notification-bell">
    <div class="bell-trigger" @click="toggle">
      <span class="bell-icon">🔔</span>
      <span v-if="unreadCount > 0" class="badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
    </div>
    <div v-if="open" class="dropdown">
      <div class="dropdown-header">
        <span>通知中心</span>
        <span v-if="unreadCount > 0" class="mark-read" @click="markAllRead">全部已读</span>
      </div>
      <div class="dropdown-body">
        <div v-if="notifications.length === 0" class="empty">暂无通知</div>
        <div v-for="n in notifications" :key="n.id" class="item">
          <div class="item-title">{{ n.title }}</div>
          <div class="item-content">{{ n.content }}</div>
          <div class="item-time">{{ formatTime(n.timestamp) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useNotifications } from '@/utils/notificationSocket'

const { notifications, unreadCount, connect, disconnect, markAllRead } = useNotifications()
const open = ref(false)

function toggle() {
  open.value = !open.value
  if (open.value && unreadCount.value > 0) markAllRead()
}

function formatTime(ts) {
  if (!ts) return ''
  return new Date(ts).toLocaleString()
}

onMounted(() => {
  const userId = Number(sessionStorage.getItem('id') || localStorage.getItem('id'))
  if (userId) connect(userId)
})
onUnmounted(() => disconnect())
</script>

<style scoped>
.notification-bell {
  position: relative;
  display: inline-block;
  cursor: pointer;
}
.bell-trigger {
  position: relative;
  font-size: 22px;
  line-height: 1;
}
.badge {
  position: absolute;
  top: -6px;
  right: -10px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: #e74c3c;
  color: #fff;
  font-size: 11px;
  line-height: 16px;
  text-align: center;
}
.dropdown {
  position: absolute;
  top: 34px;
  right: 0;
  width: 300px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  z-index: 9999;
  color: #333;
}
.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 600;
}
.mark-read {
  font-size: 12px;
  color: #3498db;
  cursor: pointer;
}
.dropdown-body {
  max-height: 320px;
  overflow-y: auto;
}
.empty {
  padding: 24px;
  text-align: center;
  color: #999;
  font-size: 13px;
}
.item {
  padding: 10px 14px;
  border-bottom: 1px solid #f5f5f5;
}
.item-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}
.item-content {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
.item-time {
  font-size: 11px;
  color: #aaa;
}
</style>
