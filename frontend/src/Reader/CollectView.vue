<template>
  <div class="page-back-container">
    <div class="collects-container">
      <div class="header-row">
        <h2>我的收藏</h2>
        <div class="visibility-control">
          <label>收藏是否可见：</label>
          <select v-model="isCollectVisible" @change="updateCollectVisibility">
            <option value="是">是</option>
            <option value="否">否</option>
          </select>
        </div>
      </div>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else>
        <div v-if="collects.length === 0" class="empty">暂无收藏内容</div>
        <ul class="collect-list">
          <template v-for="item in collects" :key="item?.novel?.novelId || item?.novelId">
            <li v-if="item && item.novel && item.novel.novelId" class="collect-item"
            :class="{ selected: selectedNovelId === item.novel.novelId }" @click="toggleSelect(item.novel.novelId)">
            <div class="privacy-status" :class="item.isPublic === 'yes' ? 'public' : 'private'">
              {{ item.isPublic === 'yes' ? '公开' : '私密' }}
            </div>
              <img :src="item.novel.fullCoverUrl || defaultCover" alt="封面" class="cover" @error="handleImageError" />
            <div class="collect-info">
                <h3 class="novel-name">{{ item.novel.novelName || '未知小说' }}</h3>
                <p class="novel-author">作者：{{ item.novel.authorName || '未知作者' }}</p>
                <p class="novel-intro" :title="item.novel.introduction || ''">
                  {{ item.novel.introduction || '暂无简介' }}
              </p>
              <div class="meta">
                  <span>状态：<strong>{{ item.novel.status || '未知' }}</strong></span>
                  <span>评分：<strong>{{ item.novel.score || 0 }}</strong></span>
                  <span>收藏数：<strong>{{ item.novel.collectedCount || 0 }}</strong></span>
              </div>
            </div>
            <div v-if="selectedNovelId === item.novel.novelId" class="overlay" @click.stop>
              <button class="overlay-button" @click="viewDetail(item)">查看详情</button>
              <button class="overlay-button" @click="updateCollectPublic(item.novel.novelId)">是否公开</button>
              <button class="overlay-button" @click="cancelCollect(item.novel.novelId)">取消收藏</button>
            </div>
          </li>
          </template>
        </ul>
        <div v-if="isPublicDialogVisible" class="dialog-mask">
          <div class="dialog-box">
            <h3>设置收藏状态</h3>
            <p>请选择该收藏是否公开：</p>
            <div class="dialog-buttons">
              <button @click="confirmPublic('yes')">公开</button>
              <button @click="confirmPublic('no')">私密</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { readerState, SelectNovel_State } from '@/stores/index'
import { novelsStore } from '@/stores/Novels'
import { getCollectsByReader, deleteCollect, addOrUpdateCollect } from '@/API/Collect_API'
import { getNovel } from '@/API/Novel_API'
import { getAuthor } from '@/API/Author_API'
import { useRouter } from 'vue-router'
import { updateReader } from '@/API/Reader_API'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const selectNovelState = SelectNovel_State()
const router = useRouter()
const store = readerState()
const isCollectVisible = ref(store.isCollectVisible || '是')

// 确保 collects 始终是一个数组，避免 null/undefined 导致的错误
const collects = computed(() => {
  const books = store.favoriteBooks
  if (!books || !Array.isArray(books)) {
    return []
  }
  // 过滤掉无效的项
  return books.filter(item => item && item.novel && item.novel.novelId)
})
const loading = ref(false)
const error = ref(null)
const selectedNovelId = ref(null)

// 使用统一的封面URL处理方法
const defaultCover = novelsStore.getFullCoverUrl(null)

async function fetchCollects() {
  loading.value = true
  error.value = null
  try {
    if (!store.readerId) throw new Error('未检测到登录读者ID')
    const response = await getCollectsByReader(store.readerId)
    if (!response || !Array.isArray(response)) {
      store.updateFavoriteBooks([])
      return
    }

    // 根据novelId获取每个小说的详细信息
    const updatedCollects = await Promise.all(
      response.map(async (collect) => {
        try {
          if (!collect || !collect.novelId) {
            console.warn('收藏记录缺少novelId:', collect)
            return null
          }
          
          const novel = await getNovel(collect.novelId)
          if (!novel || !novel.novelId) {
            console.warn(`小说不存在或无效，ID: ${collect.novelId}`)
            return null
          }
          
          // 获取作者信息
          let authorName = '未知作者'
          if (novel.authorId) {
            try {
              const authorData = await getAuthor(novel.authorId)
              authorName = authorData?.authorName || '未知作者'
            } catch (e) {
              console.error(`获取作者失败，小说ID ${novel.novelId}，作者ID ${novel.authorId}`, e)
            }
          }
          
          // 使用统一的封面URL处理方法
          const fullCoverUrl = novelsStore.getFullCoverUrl(novel.coverUrl)
          
          return {
            ...collect,
            novel: {
              ...novel,
              authorName: authorName,
              fullCoverUrl: fullCoverUrl
            }
          }
        } catch (error) {
          console.error(`获取小说 ${collect?.novelId} 详情失败:`, error)
          return null
        }
      })
    )

    // 过滤掉获取失败的小说，确保所有项都有有效的 novel 对象
    const validCollects = updatedCollects.filter(item => item !== null && item.novel && item.novel.novelId)
    store.updateFavoriteBooks(validCollects)
  } catch (err) {
    error.value = err.message || '获取收藏失败'
    store.updateFavoriteBooks([])
  } finally {
    loading.value = false
  }
}

async function cancelCollect(novelId) {
  try {
    if (!store.readerId) throw new Error('未检测到登录读者ID')
    await deleteCollect(novelId, store.readerId)
    selectedNovelId.value = null
    await fetchCollects()
  } catch (err) {
    toast.error('取消收藏失败：' + (err.message || '请稍后再试'))
  }
}

function toggleSelect(novelId) {
  selectedNovelId.value = selectedNovelId.value === novelId ? null : novelId
}

// 作品详情
async function viewDetail(item) {
  try {
    if (!item || !item.novel || !item.novel.novelId) {
      console.error('小说信息不完整:', item)
      return
    }
    
    const response = await getAuthor(item.novel.authorId);
    selectNovelState.resetNovel(
      item.novel.novelId,
      item.novel.authorId,
      item.novel.novelName,
      item.novel.introduction,
      item.novel.createTime,
      item.novel.coverUrl,
      item.novel.score,
      item.novel.totalWordCount,
      item.novel.recommendCount,
      item.novel.collectedCount,
      item.novel.status,
      item.novel.totalPrice,
      response?.authorName || '未知作者',
      response?.phone || '',
      response?.avatarUrl || '',
      response?.registerTime || '',
      response?.introduction || ''
    );
  } catch (error) {
    console.error('处理失败:', error);
  }
  router.push('/Novels/Novel_Info/home');
}
function handleClickOutside(event) {
  if (!event.target.closest('.collect-item')) {
    selectedNovelId.value = null
  }
}

// 图片加载错误处理
function handleImageError(event) {
  const img = event.target
  const currentSrc = img.src
  
  // 如果已经是默认封面，或者已经尝试过默认封面，使用占位符
  if (currentSrc === defaultCover || img.dataset.fallbackUsed === 'true') {
    // 使用一个简单的占位符（1x1透明像素的data URI）
    img.src = 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'280\' height=\'140\'%3E%3Crect width=\'280\' height=\'140\' fill=\'%23f0f0f0\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\' fill=\'%23999\' font-family=\'Arial\' font-size=\'14\'%3E暂无封面%3C/text%3E%3C/svg%3E'
    img.dataset.fallbackUsed = 'true'
    return
  }
  
  // 标记已使用过默认封面
  img.dataset.fallbackUsed = 'true'
  console.warn('图片加载失败，使用默认封面:', currentSrc)
  img.src = defaultCover
}

const isPublicDialogVisible = ref(false)
const pendingNovelId = ref(null)

function updateCollectPublic(novelId) {
  if (!store.readerId) {
    toast.info('未检测到登录读者ID')
    return
  }
  pendingNovelId.value = novelId
  isPublicDialogVisible.value = true
}

async function confirmPublic(choice) {
  try {
    await addOrUpdateCollect(pendingNovelId.value, store.readerId, choice)
    toast.success('设置成功：该收藏已设为 ' + (choice === 'yes' ? '公开' : '私密'))
    await fetchCollects()
  } catch (err) {
    toast.error('设置公开失败：' + (err.message || '请稍后再试'))
  } finally {
    isPublicDialogVisible.value = false
    pendingNovelId.value = null
  }
}

// 更新收藏可见性
async function updateCollectVisibility() {
  try {
    // 将 "是"/"否" 转换为 "yes"/"no"
    const isPublicValue = isCollectVisible.value === '是' ? 'yes' : 'no'
    
    // 获取该读者的所有收藏记录
    const collectsData = await getCollectsByReader(store.readerId)
    
    // 批量更新所有收藏记录的 isPublic 字段
    if (collectsData && collectsData.length > 0) {
      await Promise.all(
        collectsData.map(collect => 
          addOrUpdateCollect(collect.novelId, store.readerId, isPublicValue)
        )
      )
      console.log(`成功更新 ${collectsData.length} 条收藏记录的可见性为: ${isPublicValue}`)
    }
    
    // 更新读者的全局设置
    const updateData = {
      readerId: store.readerId,
      phone: store.phone || null,
      gender: store.gender || null,
      isCollectVisible: isCollectVisible.value,
      isRecommendVisible: store.isRecommendVisible || '是',
      balance: store.balance || 0,
      avatarUrl: store.avatarUrl || null,
      backgroundUrl: store.backgroundUrl || null,
    }
    await updateReader(store.readerId, updateData)
    store.isCollectVisible = isCollectVisible.value
    
    // 重新加载收藏列表以显示最新状态
    await fetchCollects()
    
    toast.success('收藏可见性已更新')
  } catch (error) {
    console.error('更新收藏可见性失败:', error)
    toast.error('更新失败，请稍后再试')
  }
}

onMounted(() => {
  isCollectVisible.value = store.isCollectVisible || '是'
  fetchCollects()
  window.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>


<style scoped>
.collects-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 16px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.loading,
.error,
.empty {
  text-align: center;
  font-size: 18px;
  margin: 20px 0;
}

.error {
  color: #d9534f;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.visibility-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.visibility-control label {
  font-weight: 600;
  font-size: 18px;
  margin-bottom: 0;
}

.visibility-control select {
  padding: 4px 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 18px;
}

.collect-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: flex-start;
}

.collect-item {
  position: relative;
  background: #fff;
  box-shadow: 0 2px 6px rgb(0 0 0 / 0.1);
  border-radius: 10px;
  overflow: hidden;
  width: 280px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, filter 0.3s ease;
}

.collect-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 20px rgb(0 0 0 / 0.15);
}

.collect-item.selected {
  pointer-events: none;
}

.collect-item.selected .overlay {
  pointer-events: auto;
}

.cover {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-bottom: 1px solid #eee;
}

.collect-info {
  padding: 16px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.novel-name {
  font-size: 20px;
  margin: 0 0 10px;
  color: #f8d302f5;
  font-weight: 700;
  line-height: 1.2;
}

.novel-intro {
  font-size: 14px;
  color: #555;
  flex-grow: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  margin-bottom: 12px;
}

.meta {
  font-size: 13px;
  color: #666;
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.meta span strong {
  font-size: 16px;
  color: #83718b;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  backdrop-filter: blur(4px);
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 12px;
  pointer-events: auto;
  border-radius: 10px;
  padding: 0 10px;
  z-index: 10;
}

.overlay-button {
  padding: 12px 24px;
  background: #fff;
  color: #333;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  width: 160px;
  transition: background 0.3s ease, transform 0.2s ease;
  user-select: none;
}

.overlay-button:hover {
  background: #f0f0f0;
  transform: scale(1.05);
}

.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.dialog-box {
  background: #fff;
  padding: 20px 30px;
  border-radius: 8px;
  text-align: center;
  width: 300px;
}

.dialog-buttons button {
  margin: 10px;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.dialog-buttons button:first-child {
  background-color: #409EFF;
  color: white;
}

.dialog-buttons button:last-child {
  background-color: #E6A23C;
  color: white;
}

.privacy-status {
  position: absolute;
  top: 15px;
  left: 15px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  z-index: 5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.privacy-status.public {
  background-color: #409EFF;
  color: white;
}

.privacy-status.private {
  background-color: #E6A23C;
  color: white;
}
</style>
