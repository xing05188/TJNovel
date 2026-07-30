import { ref } from 'vue'

/**
 * 通知 WebSocket 单例管理。
 * 后端通过网关 /ws/notifications?userId=xxx 推送通知，前端用原生 WebSocket 接收。
 */

const notifications = ref([])
const unreadCount = ref(0)
const connected = ref(false)

let socket = null
let reconnectTimer = null
let currentUserId = null

function getWsBase() {
  const base = process.env.VUE_APP_BASE_API || 'http://4.233.147.12:7080'
  return base.replace(/^http/, 'ws')
}

function connect(userId) {
  if (!userId) return
  currentUserId = userId
  if (socket && (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING)) {
    return
  }
  const url = `${getWsBase()}/ws/notifications?userId=${userId}`
  try {
    socket = new WebSocket(url)
  } catch (e) {
    scheduleReconnect()
    return
  }
  socket.onopen = () => { connected.value = true }
  socket.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      notifications.value.unshift(msg)
      unreadCount.value += 1
    } catch (e) {
      console.error('通知消息解析失败', e)
    }
  }
  socket.onclose = () => {
    connected.value = false
    scheduleReconnect()
  }
  socket.onerror = () => {
    if (socket) socket.close()
  }
}

function scheduleReconnect() {
  if (reconnectTimer) return
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null
    if (currentUserId) connect(currentUserId)
  }, 3000)
}

function disconnect() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  if (socket) {
    socket.close()
    socket = null
  }
}

function markAllRead() {
  unreadCount.value = 0
}

export function useNotifications() {
  return { notifications, unreadCount, connected, connect, disconnect, markAllRead }
}
