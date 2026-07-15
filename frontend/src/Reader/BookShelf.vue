<template>
  <div class="page-back-container">
    <section class="book-section">
      <!-- 收藏卡片 -->
      <div class="category-card">
        <div class="card-header">
          <h2>我的收藏</h2>
        </div>
        <div class="card-content">
          <template v-if="loading">
            <div class="loading">加载中...</div>
          </template>
          <template v-else>
            <div class="novel-grid">
              <template v-if="collects.length > 0">
                <template v-for="item in collects" :key="item.novel?.novelId || item.novelId">
                  <div v-if="item && item.novel && item.novel.novelId" class="novel-item">
                  <div class="img-container" @click="handle_NovelInfro(item)">
                      <img :src="item.novel.fullCoverUrl || defaultCover" :alt="item.novel.novelName || '未知小说'" @error="handleImageError"/>
                    </div>
                    <div class="novel-title">{{ item.novel.novelName || '未知小说' }}</div>
                  </div>
                </template>
              </template>
              <div v-else class="empty">暂无收藏内容</div>
            </div>
          </template>
        </div>
      </div>

      <!-- 推荐卡片 -->
      <div class="category-card">
        <div class="card-header">
          <h2>我的推荐</h2>
        </div>
        <div class="card-content">
          <template v-if="loading">
            <div class="loading">加载中...</div>
          </template>
          <template v-else>
            <div class="novel-grid">
              <template v-if="recommends.length > 0">
                <template v-for="item in recommends" :key="item.novel?.novelId || item.novelId">
                  <div v-if="item && item.novel && item.novel.novelId" class="novel-item">
                  <div class="img-container" @click="handle_NovelInfro(item)">
                      <img :src="item.novel.fullCoverUrl || defaultCover" :alt="item.novel.novelName || '未知小说'" @error="handleImageError"/>
                    </div>
                    <div class="novel-title">{{ item.novel.novelName || '未知小说' }}</div>
                  </div>
                </template>
              </template>
              <div v-else class="empty">暂无推荐内容</div>
            </div>
          </template>
        </div>
      </div>

      <!-- 阅读历史卡片 -->
      <div class="category-card">
        <div class="card-header">
          <h2>我的阅读历史</h2>
        </div>
        <div class="card-content">
          <template v-if="loading">
            <div class="loading">加载中...</div>
          </template>
          <template v-else>
            <div class="novel-grid">
              <template v-if="history.length > 0">
                <template v-for="item in history" :key="item.novel?.novelId || item.novelId">
                  <div v-if="item && item.novel && item.novel.novelId" class="novel-item">
                  <div class="img-container" @click="handle_NovelInfro(item)">
                      <img :src="item.novel.fullCoverUrl || defaultCover" :alt="item.novel.novelName || '未知小说'" @error="handleImageError"/>
                    </div>
                    <div class="novel-title">{{ item.novel.novelName || '未知小说' }}</div>
                  </div>
                </template>
              </template>
              <div v-else class="empty">暂无阅读历史</div>
            </div>
          </template>
        </div>
      </div>
    </section>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { readerState } from '@/stores/index'
import { novelsStore } from '@/stores/Novels'
import { getCollectsByReader } from '@/API/Collect_API'
import { getRecommendsByReader } from '@/API/Recommend_API'
import { getRecentReadingsByReaderId } from '@/API/Reader_API'
import { getNovel } from '@/API/Novel_API'
import { getAuthor } from '@/API/Author_API'
import { useRouter } from 'vue-router'
import { SelectNovel_State } from '@/stores/index'

const selectNovelState = SelectNovel_State()
const router = useRouter()

const store = readerState()
const collects = ref([])
const recommends = ref([])
const history = ref([])

const loading = ref(true) // 初始为true，表示正在加载
const error = ref(null)



// 使用统一的封面URL处理方法
const defaultCover = novelsStore.getFullCoverUrl(null)

async function fetchCollects() {
  try {
    if (!store.readerId) {
      console.warn('未检测到登录读者ID')
      collects.value = []
      return
    }
    
    console.log('开始获取收藏，读者ID:', store.readerId)
    const response = await getCollectsByReader(store.readerId)
    console.log('收藏API响应:', response)
    
    if (!response || !Array.isArray(response)) {
      console.log('收藏响应为空或不是数组')
      collects.value = []
      return
    }
    
    console.log(`获取到 ${response.length} 条收藏记录`)
    
    // 根据novelId获取每个小说的详细信息
    const processed = await Promise.all(
      response.map(async (collect) => {
        try {
          if (!collect.novelId) {
            console.warn('收藏记录缺少novelId:', collect)
            return null
          }
          
          const novel = await getNovel(collect.novelId)
          if (!novel) {
            console.warn(`小说不存在，ID: ${collect.novelId}`)
            return null
          }
          
          // 获取作者信息
          let authorName = '未知作者'
          if (novel.authorId) {
            try {
              const authorData = await getAuthor(novel.authorId)
              authorName = authorData.authorName || '未知作者'
            } catch (e) {
              console.error(`获取作者失败，小说ID ${novel.novelId}，作者ID ${novel.authorId}`, e)
            }
          }
          
          // 使用统一的封面URL处理方法
          const fullCoverUrl = novelsStore.getFullCoverUrl(novel.coverUrl)
          console.log(`处理收藏小说 ${novel.novelId}: ${novel.novelName}`)
          console.log(`  原始封面URL: ${novel.coverUrl}`)
          console.log(`  处理后封面URL: ${fullCoverUrl}`)
          
          return {
            ...collect,
            novel: {
              ...novel,
              authorName: authorName,
              fullCoverUrl: fullCoverUrl
            }
          }
        } catch (error) {
          console.error(`获取小说 ${collect.novelId} 详情失败:`, error)
          return null
        }
      })
    )
    
    // 过滤掉获取失败的小说
    collects.value = processed.filter(item => item !== null && item.novel)
    console.log(`成功处理 ${collects.value.length} 条收藏记录`)
    console.log('收藏数据示例:', collects.value.length > 0 ? {
      novelId: collects.value[0].novel?.novelId,
      novelName: collects.value[0].novel?.novelName,
      fullCoverUrl: collects.value[0].novel?.fullCoverUrl
    } : '无数据')
  } catch (err) {
    console.error('获取收藏失败:', err)
    error.value = err.message || '获取收藏失败'
    collects.value = []
  }
}

async function fetchRecommends() {
  try {
    if (!store.readerId) {
      console.warn('未检测到登录读者ID')
      recommends.value = []
      return
    }
    
    console.log('开始获取推荐，读者ID:', store.readerId)
    const response = await getRecommendsByReader(store.readerId)
    console.log('推荐API响应:', response)
    
    if (!response || !Array.isArray(response)) {
      console.log('推荐响应为空或不是数组')
      recommends.value = []
      return
    }
    
    console.log(`获取到 ${response.length} 条推荐记录`)
    
    // 根据novelId获取每个小说的详细信息
    const processed = await Promise.all(
      response.map(async (recommend) => {
        try {
          if (!recommend.novelId) {
            console.warn('推荐记录缺少novelId:', recommend)
            return null
          }
          
          const novel = await getNovel(recommend.novelId)
          if (!novel) {
            console.warn(`小说不存在，ID: ${recommend.novelId}`)
            return null
          }
          
          // 获取作者信息
          let authorName = '未知作者'
          if (novel.authorId) {
            try {
              const authorData = await getAuthor(novel.authorId)
              authorName = authorData.authorName || '未知作者'
            } catch (e) {
              console.error(`获取作者失败，小说ID ${novel.novelId}，作者ID ${novel.authorId}`, e)
            }
          }
          
          // 使用统一的封面URL处理方法
          const fullCoverUrl = novelsStore.getFullCoverUrl(novel.coverUrl)
          console.log(`处理推荐小说 ${novel.novelId}: ${novel.novelName}`)
          console.log(`  原始封面URL: ${novel.coverUrl}`)
          console.log(`  处理后封面URL: ${fullCoverUrl}`)
          
          return {
            ...recommend,
            novel: {
              ...novel,
              authorName: authorName,
              fullCoverUrl: fullCoverUrl
            }
          }
        } catch (error) {
          console.error(`获取小说 ${recommend.novelId} 详情失败:`, error)
          return null
        }
      })
    )
    
    // 过滤掉获取失败的小说
    recommends.value = processed.filter(item => item !== null && item.novel)
    console.log(`成功处理 ${recommends.value.length} 条推荐记录`)
    console.log('推荐数据示例:', recommends.value.length > 0 ? {
      novelId: recommends.value[0].novel?.novelId,
      novelName: recommends.value[0].novel?.novelName,
      fullCoverUrl: recommends.value[0].novel?.fullCoverUrl
    } : '无数据')
  } catch (err) {
    console.error('获取推荐失败:', err)
    error.value = err.message || '获取推荐失败'
    recommends.value = []
  }
}

async function fetchHistory() {
  try {
    if (!store.readerId) {
      console.warn('未检测到登录读者ID')
      history.value = []
      return
    }
    
    console.log('开始获取阅读历史，读者ID:', store.readerId)
    const response = await getRecentReadingsByReaderId(store.readerId)
    console.log('阅读历史API响应:', response)
    
    if (!response || !Array.isArray(response)) {
      console.log('阅读历史响应为空或不是数组')
      history.value = []
      return
    }
    
    console.log(`获取到 ${response.length} 条阅读历史记录`)
    
    // 根据novelId获取每个小说的详细信息
    const processed = await Promise.all(
      response.map(async (reading) => {
        try {
          if (!reading.novelId) {
            console.warn('阅读历史记录缺少novelId:', reading)
            return null
          }
          
          const novel = await getNovel(reading.novelId)
          if (!novel) {
            console.warn(`小说不存在，ID: ${reading.novelId}`)
            return null
          }
          
          // 获取作者信息
          let authorName = '未知作者'
          if (novel.authorId) {
            try {
              const author = await getAuthor(novel.authorId)
              authorName = author.authorName || '未知作者'
            } catch (e) {
              console.error(`获取作者失败，小说ID ${novel.novelId}，作者ID ${novel.authorId}`, e)
            }
          }
          
          // 使用统一的封面URL处理方法
          const fullCoverUrl = novelsStore.getFullCoverUrl(novel.coverUrl)
          console.log(`处理阅读历史小说 ${novel.novelId}: ${novel.novelName}`)
          console.log(`  原始封面URL: ${novel.coverUrl}`)
          console.log(`  处理后封面URL: ${fullCoverUrl}`)
          
          return {
            ...reading,
            novel: {
              ...novel,
              authorName: authorName,
              fullCoverUrl: fullCoverUrl
            }
          }
        } catch (error) {
          console.error(`获取小说 ${reading.novelId} 详情失败:`, error)
          return null
        }
      })
    )
    
    // 过滤掉获取失败的小说
    history.value = processed.filter(item => item !== null && item.novel)
    console.log(`成功处理 ${history.value.length} 条阅读历史记录`)
    console.log('阅读历史数据示例:', history.value.length > 0 ? {
      novelId: history.value[0].novel?.novelId,
      novelName: history.value[0].novel?.novelName,
      fullCoverUrl: history.value[0].novel?.fullCoverUrl
    } : '无数据')
  } catch (err) {
    console.error('获取阅读历史失败:', err)
    error.value = err.message || '获取历史失败'
    history.value = []
  }
}


// 图片加载错误处理
function handleImageError(event) {
  const img = event.target
  const currentSrc = img.src
  
  // 如果已经是默认封面，或者已经尝试过默认封面，使用占位符
  if (currentSrc === defaultCover || img.dataset.fallbackUsed === 'true') {
    // 使用一个简单的占位符（1x1透明像素的data URI）
    img.src = 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'150\' height=\'210\'%3E%3Crect width=\'150\' height=\'210\' fill=\'%23f0f0f0\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\' fill=\'%23999\' font-family=\'Arial\' font-size=\'14\'%3E暂无封面%3C/text%3E%3C/svg%3E'
    img.dataset.fallbackUsed = 'true'
    return
  }
  
  // 标记已使用过默认封面
  img.dataset.fallbackUsed = 'true'
  console.warn('图片加载失败，使用默认封面:', currentSrc)
  img.src = defaultCover
}

// 作品主页
async function handle_NovelInfro(item) {
  try {
    if (!item.novel || !item.novel.novelId) {
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
        response.authorName,
        response.phone,
        response.avatarUrl,
        response.registerTime,
        response.introduction
    );
  } catch (error) {
    console.error('处理失败:', error);
  }
  router.push('/Novels/Novel_Info/home');
}



onMounted(async () => {
  loading.value = true
  error.value = null
  
  try {
    // 并行获取所有数据
    await Promise.all([
      fetchCollects(),
      fetchRecommends(),
      fetchHistory()
    ])
    console.log('所有数据加载完成')
  } catch (err) {
    console.error('加载数据时发生错误:', err)
    error.value = err.message || '加载数据失败'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-back-container {
  padding: 20px;
}

.book-section {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.card-content {
  padding: 15px 20px;
}

.novel-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15px;
}

.novel-item {
  text-align: center;
}


.novel-title {
  margin-top: 8px;
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.loading, .empty {
  grid-column: 1 / -1;
  padding: 20px;
  text-align: center;
  color: #666;
}

/* 响应式设计 - 在小屏幕上减少每行显示数量 */
@media (max-width: 1200px) {
  .novel-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (max-width: 768px) {
  .novel-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 480px) {
  .novel-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
/* 新增图片容器样式 */
.img-container {
  position: relative;
  overflow: hidden;
  border-radius: 4px;
  width: 100%;
  height: 140px;
}

.novel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  cursor: pointer;
  display: block;
  background-color: #f0f0f0;
}

/* 点击效果 */
.novel-item img:active {
  transform: scale(0.95);
}

/* 悬停效果 */
.novel-item:hover img {
  transform: scale(1.03);
}

/* 点击时的涟漪效果 */
.img-container::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.5);
  opacity: 0;
  border-radius: 100%;
  transform: scale(1, 1) translate(-50%, -50%);
  transform-origin: 50% 50%;
}

.img-container:active::after {
  animation: ripple 0.6s ease-out;
}

@keyframes ripple {
  0% {
    transform: scale(0, 0);
    opacity: 0.5;
  }
  100% {
    transform: scale(20, 20);
    opacity: 0;
  }
}
</style>