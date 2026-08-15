<template>
    <div class="chapter-card" @click="goRead">
        <div class="card-header">
            <span class="novel-name">{{ novelName }}</span>
            <span class="chapter-title">《{{ chapter.title }}》</span>
        </div>
        <p class="snippet" v-html="highlightContent"></p>
        <div class="card-footer">
            <span class="word-count">{{ chapter.wordCount || 0 }} 字</span>
            <span class="go-read">去阅读 →</span>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, defineProps } from 'vue'
import { useRouter } from 'vue-router'
import { getNovel } from '@/API/Novel_API'
import { SelectNovel_State } from '@/stores/index'

const props = defineProps({
    chapter: {
        type: Object,
        required: true
    }
})

const router = useRouter()
const novelName = ref('加载中...')

// 后端返回的高亮片段已用 <em> 包裹关键词，直接渲染
const highlightContent = computed(() => {
    const highlight = props.chapter?.highlight
    if (highlight) return highlight
    // 无高亮片段时，截取正文前 120 字作为摘要
    const content = props.chapter?.content || ''
    return content.length > 120 ? content.slice(0, 120) + '...' : content
})

onMounted(async () => {
    try {
        const novel = await getNovel(props.chapter.novelId)
        novelName.value = novel?.novelName || `小说 #${props.chapter.novelId}`
    } catch (error) {
        novelName.value = `小说 #${props.chapter.novelId}`
    }
})

function goRead() {
    const novelId = props.chapter.novelId
    const chapterId = props.chapter.chapterId
    if (!novelId || !chapterId) return
    const store = SelectNovel_State()
    store.resetNovel(
        novelId, 0, novelName.value, '', '', '', 0, 0, 0, 0, '连载', 0, '', '', '', '', ''
    )
    store.resetChapter(chapterId, props.chapter.title || '', '', props.chapter.wordCount || 0, 0, 0, false, '', '已发布')
    router.push({
        path: '/Novels/reader',
        query: { novelId, chapterId }
    })
}
</script>

<style scoped>
.chapter-card {
    background: #fff;
    border: 1px solid #eee;
    border-radius: 10px;
    padding: 16px 20px;
    cursor: pointer;
    transition: box-shadow 0.2s, border-color 0.2s;
}

.chapter-card:hover {
    border-color: #ffd100;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
    display: flex;
    align-items: baseline;
    gap: 10px;
    margin-bottom: 8px;
    flex-wrap: wrap;
}

.novel-name {
    font-size: 14px;
    color: #ff5722;
    font-weight: bold;
}

.chapter-title {
    font-size: 16px;
    font-weight: bold;
    color: #222;
}

.snippet {
    font-size: 14px;
    color: #555;
    line-height: 1.7;
    margin: 8px 0 12px;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

/* 关键词高亮样式（后端返回的 <em> 标签） */
.snippet :deep(em) {
    color: #ff0000;
    font-style: normal;
    font-weight: bold;
    background: #fff3cd;
    padding: 0 2px;
    border-radius: 2px;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.word-count {
    font-size: 12px;
    color: #999;
}

.go-read {
    font-size: 13px;
    color: #ff5722;
    font-weight: bold;
}
</style>
