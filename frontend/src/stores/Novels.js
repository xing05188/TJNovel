// src/stores/Novels.js
import { reactive } from 'vue'
import { getAuthorNovel } from '@/API/Novel_API'
import { authorStore } from './CurrentAuthor'
//import { current_state } from './index'

export const novelsStore = reactive({
  novels: [],
  // 不再从 localStorage 读取 currentNovel，完全依赖 API 和内存数据
  // 这样可以避免缓存数据不一致的问题
  currentNovel: null, 
  isLoading: false,
  error: null,
  shouldRefresh: false,
  
  async fetchNovels() {
    // 先检查当前标签页用户是否是作者（优先读sessionStorage的role，避免多标签页冲突）
    const currentRole = Number(sessionStorage.getItem('currentRole'))
    if (currentRole !== 1) {
      console.error('当前用户不是作者，无法获取小说列表')
      this.error = { message: '当前用户不是作者，无法获取小说列表' }
      this.isLoading = false
      return
    }
    
    // 先检查作者ID是否存在
    const authorId = authorStore.currentAuthor?.author_id
    if (!authorId || authorId === 0) {
      console.error('无法获取作者ID')
      this.error = { message: '作者信息未加载完成' }
      // 尝试获取作者信息
      await authorStore.fetchAuthorData()
      // 再次检查作者ID
      if (!authorStore.currentAuthor?.author_id || authorStore.currentAuthor.author_id === 0) {
        console.error('获取作者信息失败，当前用户可能不是作者')
        this.error = { message: '当前用户不是作者，无法获取小说列表' }
        this.isLoading = false
        return
      }
    }
    this.isLoading = true
    this.error = null
    
    try {
      const response = await getAuthorNovel(authorStore.currentAuthor.author_id)
      
      const rawData = Array.isArray(response) ? response : response?.data || []
      
      this.novels = rawData.map(novel => ({
        novel_id: novel.novelId,
        author_id: novel.authorId,
        novel_name: novel.novelName || '未命名小说',
        cover_url: this.getFullCoverUrl(novel.coverUrl), 
        status: this.mapStatus(novel.status),
        introduction: novel.introduction || '暂无简介',
        total_word_count: novel.totalWordCount || 0,
        score: novel.score || 0,
        create_time: novel.createTime,
        recommend_count: novel.recommendCount || 0,
        collected_count: novel.collectedCount || 0,
        total_price: novel.totalPrice
      }))
      
    } catch (error) {
      console.error('获取小说列表失败:', error)
      this.error = error
      this.novels = [{
        novel_id: 0,
        novel_name: '数据加载失败',
        cover_url: 'https://picsum.photos/300/400?error',
        status: '错误'
      }]
    } finally {
      this.isLoading = false
    }
  },

  // 设置当前小说并持久化存储
  setCurrentNovel(novel) {
    this.currentNovel = novel
    localStorage.setItem('currentNovel', JSON.stringify(novel))
  },

  // 清除当前小说
  clearCurrentNovel() {
    this.currentNovel = null
    localStorage.removeItem('currentNovel')
  },

  // 统一的封面URL处理方法
  getFullCoverUrl(partialPath) {
    // 处理 null、undefined、空字符串等情况
    if (!partialPath || partialPath === 'null' || partialPath === 'undefined' || partialPath.trim() === '') {
      // 使用默认封面
      return 'https://novelprogram123.oss-cn-hangzhou.aliyuncs.com/e165315c-da2b-42c9-b3cf-c0457d168634.jpg'
    }
    // 如果已经是完整URL，直接返回
    if (partialPath.startsWith('http://') || partialPath.startsWith('https://')) {
      return partialPath
    }
    // 拼接OSS基础URL
    const ossBase = 'https://novelprogram123.oss-cn-hangzhou.aliyuncs.com/'
    // 移除开头的斜杠（如果有）
    const cleanPath = partialPath.replace(/^\//, '')
    return `${ossBase}${cleanPath}`
  },

  // 状态映射方法
  mapStatus(status) {
    const statusMap = {
      '连载': '连载',
      '完结': '完结',
      '待审核': '待审核',
      '封禁': '封禁'
    }
    return statusMap[status] || status || '未知状态'
  },

  getNovelById(id) {
    return this.novels.find(novel => novel.novel_id === id) || {
      novel_id: -1,
      novel_name: '未知小说',
      cover_url: 'https://picsum.photos/300/400?random=999',
      status: '未知',
      introduction: '暂无简介',
      total_word_count: 0,
      chapter_count: 0,
      score: 0
    }
  },

  async removeNovel(id) {
    try {
      this.novels = this.novels.filter(novel => novel.novel_id !== id)
      if (this.currentNovel && this.currentNovel.novel_id === id) {
        this.clearCurrentNovel()
      }
    } catch (error) {
      console.error('删除小说失败:', error)
      throw error
    }
  }
})