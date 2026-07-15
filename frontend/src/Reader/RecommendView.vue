<template>
  <div class="home-view">
    <div class="page-back-container">
      <div class="collects-container">
        <div class="header-row">
          <h2>我的推荐</h2>
          <div class="visibility-control">
            <label>推荐是否可见：</label>
            <select v-model="isRecommendVisible" @change="updateRecommendVisibility">
              <option value="是">是</option>
              <option value="否">否</option>
            </select>
          </div>
        </div>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else>
          <div v-if="recommends.length === 0" class="empty">暂无推荐内容</div>
          <ul class="collect-list">
            <template v-for="item in recommends" :key="item?.novel?.novelId || item?.novelId">
              <li v-if="item && item.novel && item.novel.novelId" class="collect-item"
              :class="{ selected: selectedNovelId === item.novel.novelId }" @click="toggleSelect(item.novel.novelId)">
                <img :src="item.novel.fullCoverUrl || defaultCover" alt="封面" class="cover" @error="handleImageError" />
              <div class="collect-info">
                  <h3 class="novel-name">{{ item.novel.novelName || '未知小说' }}</h3>
                  <p class="novel-author">作者：{{ item.novel.authorName || '未知作者' }}</p>
                <div class="meta">
                    <span>状态：<strong>{{ item.novel.status || '未知' }}</strong></span>
                    <span>评分：<strong>{{ item.novel.score || 0 }}</strong></span>
                    <span>推荐数：<strong>{{ item.novel.recommendCount || 0 }}</strong></span>
                </div>
                  <p class="recommend-reason">推荐理由："{{ item.reason || '无' }}"</p>
              </div>
              <div v-if="selectedNovelId === item.novel.novelId" class="overlay" @click.stop>
                <button class="overlay-button" @click="viewDetail(item)">查看详情</button>
                <button class="overlay-button" @click="cancelRecommend(item.novel.novelId)">取消推荐</button>
              </div>
            </li>
            </template>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { readerState, SelectNovel_State } from '@/stores/index'
import { novelsStore } from '@/stores/Novels'
import { getRecommendsByReader, deleteRecommend } from '@/API/Recommend_API'
import { getNovel } from '@/API/Novel_API'
import { getAuthor } from '@/API/Author_API'
import { useRouter } from 'vue-router'
import { updateReader } from '@/API/Reader_API'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const selectNovelState = SelectNovel_State()
const router = useRouter()

const store = readerState()
const isRecommendVisible = ref(store.isRecommendVisible || '是')
const recommends = computed(() => store.recommendBooks)
const loading = ref(false)
const error = ref(null)
const selectedNovelId = ref(null)

// 使用统一的封面URL处理方法
const defaultCover = novelsStore.getFullCoverUrl(null)

async function fetchRecommends() {
  loading.value = true
  error.value = null

  try {
    if (!store.readerId) throw new Error('未检测到登录读者ID')

    const response = await getRecommendsByReader(store.readerId)
    if (!response || !Array.isArray(response)) {
      store.updateRecommendBooks([])
      return
    }

    // 根据novelId获取每个小说的详细信息
    const processed = await Promise.all(
      response.map(async (recommend) => {
        try {
          if (!recommend || !recommend.novelId) {
            console.warn('推荐记录缺少novelId:', recommend)
            return null
          }
          
          const novel = await getNovel(recommend.novelId)
          if (!novel || !novel.novelId) {
            console.warn(`小说不存在或无效，ID: ${recommend.novelId}`)
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
            ...recommend,
            novel: {
              ...novel,
              authorName: authorName,
              fullCoverUrl: fullCoverUrl
            }
          }
        } catch (error) {
          console.error(`获取小说 ${recommend?.novelId} 详情失败:`, error)
          return null
        }
      })
    )

    // 过滤掉获取失败的小说，确保所有项都有有效的 novel 对象
    const validProcessed = processed.filter(item => item !== null && item.novel && item.novel.novelId)
    store.updateRecommendBooks(validProcessed)

  } catch (err) {
    console.error('获取推荐失败：', err)
    error.value = err.message || '获取推荐失败'
    store.updateRecommendBooks([])
  } finally {
    loading.value = false
  }
}

async function cancelRecommend(novelId) {
  try {
    if (!store.readerId) {
      throw new Error('未检测到登录读者ID')
    }
    await deleteRecommend(novelId, store.readerId)
    selectedNovelId.value = null
    await fetchRecommends()
  } catch (err) {
    toast.error('取消推荐失败：' + (err.message || '请稍后再试'))
  }
}

function toggleSelect(novelId) {
  if (selectedNovelId.value === novelId) {
    selectedNovelId.value = null
  } else {
    selectedNovelId.value = novelId
  }
}

// 查看详情
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

// 更新推荐可见性
async function updateRecommendVisibility() {
  try {
    // 只发送需要更新的字段，避免触发数据库约束检查
    const updateData = {
      readerId: store.readerId,
      // 不发送readerName，避免唯一性约束冲突
      // 不发送password字段，除非需要更新密码
      phone: store.phone || null,
      gender: store.gender || null,
      isCollectVisible: store.isCollectVisible || '是',
      isRecommendVisible: isRecommendVisible.value || '是',
      balance: store.balance || 0,
      avatarUrl: store.avatarUrl || null,
      backgroundUrl: store.backgroundUrl || null,
    }

    await updateReader(store.readerId, updateData)
    store.isRecommendVisible = isRecommendVisible.value
    toast.success('推荐可见性已更新')
  } catch (error) {
    console.error('更新推荐可见性失败:', error)
    toast.error('更新失败，请稍后再试')
  }
}

onMounted(() => {
  isRecommendVisible.value = store.isRecommendVisible || '是'
  fetchRecommends()
  window.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.home-view {
  padding: 0;
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

.collects-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 16px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.cover {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-bottom: 1px solid #eee;
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

.meta {
  font-size: 13px;
  color: #666;
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.meta span strong {
  font-size: 16px;
  color: #83718b;
}

.recommend-reason {
  font-size: 14px;
  color: #444;
  font-style: italic;
  line-height: 1.4;
  word-break: break-word;
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
</style>
