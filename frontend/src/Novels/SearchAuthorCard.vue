<template>
    <div class="author-card">
        <div class="author-info">
            <img :src="getFullAvatarUrl(author.avatarUrl)"
                alt="作者头像" class="avatar" @click="goAuthorHome" @error="handleAvatarError" />
            <div class="details">
                <h3 @click="goAuthorHome">{{ author.authorName }}</h3>
                <p class="meta">已创作 {{ authorRegisterDays }} 天</p>
                <p class="meta">联系方式 : {{ maskedPhone }}</p>
                <p class="meta">
                    简介 : {{
                        (author.introduction && author.introduction.length > 37)
                            ? author.introduction.slice(0, 37) + '...'
                            : (author.introduction || 'Ta还有点神秘哦~')
                    }}
                </p>
            </div>
        </div>
        <div class="stats">
            <div class="stat-item">
                <span class="stat-value">{{ authorNovelCount }}</span>
                <span class="stat-label">作品数</span>
            </div>
            <div class="stat-item">
                <span class="stat-value">{{ authorWordCount }}</span>
                <span class="stat-label">总字数</span>
            </div>
        </div>
        <button class="follow-btn" @click="goAuthorHome">进入主页</button>
    </div>
</template>

<script setup>
import { ref, onMounted, defineProps, computed } from 'vue';
import { getAuthorNovelCount, getAuthorTotalWordCount } from '@/API/Author_API';
import { useRouter } from 'vue-router';
import defaultAvatar from '@/assets/default-avatar.jpeg'
const router = useRouter();

const props = defineProps({
    author: {
        type: Object,
        required: true
    }
});
const authorNovelCount = ref(0);
const authorWordCount = ref(0);
const authorRegisterDays = ref(0);

// 计算注册天数
const calculateRegisterDays = (registerTime) => {
    if (!registerTime) return 0
    const registerDate = new Date(registerTime)
    const now = new Date()
    const diffTime = Math.abs(now - registerDate)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
}

const fetchAuthorStats = async () => {
    try {
        const [novelCount, wordCount] = await Promise.all([
            getAuthorNovelCount(props.author.authorId),
            getAuthorTotalWordCount(props.author.authorId)
        ]);
        
        // 正确提取数值
        const nc = (novelCount && typeof novelCount === 'number')
            ? novelCount
            : (novelCount?.data?.novelCount ?? novelCount?.novelCount ?? 0);
        authorNovelCount.value = Number(nc) || 0;
        
        const wc = (wordCount && typeof wordCount === 'number')
            ? wordCount
            : (wordCount?.data?.totalWordCount ?? wordCount?.totalWordCount ?? 0);
        authorWordCount.value = Number(wc) || 0;
        
        // 从 registerTime 计算创作天数
        const days = calculateRegisterDays(props.author.registerTime);
        authorRegisterDays.value = days;
    } catch (error) {
        console.error('获取作者统计信息失败:', error);
    }
}

const maskedPhone = computed(() => {
    if (!props.author.phone) return '未公开';
    return props.author.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
});

// 格式化头像URL的工具函数
function getFullAvatarUrl(avatarUrl) {
    const ossBase = 'https://novelprogram123.oss-cn-hangzhou.aliyuncs.com/'
    
    if (!avatarUrl || avatarUrl.trim() === '' || avatarUrl === 'null' || avatarUrl === 'undefined') {
        return defaultAvatar
    }
    
    // MinIO 内网地址(localhost:9000) → 走前端代理(8086)
    if (avatarUrl.indexOf('localhost:9000') !== -1) {
        return '/minio/' + avatarUrl.substring(avatarUrl.indexOf('localhost:9000/') + 'localhost:9000/'.length)
    }
    
    // 如果已经是完整URL，直接返回
    if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
        return avatarUrl
    }
    
    // 移除开头的斜杠（如果有）
    const cleanPath = avatarUrl.replace(/^\//, '')
    return ossBase + cleanPath
}

// 头像加载错误处理
function handleAvatarError(event) {
    if (event.target.src !== defaultAvatar) {
        event.target.src = defaultAvatar
    }
}

function goAuthorHome() {
    router.push(`/author/${props.author.authorId}`);
}

onMounted(() => {
    fetchAuthorStats();
});
</script>

<style scoped>
.author-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s;
    width: 340px;
}

.author-card:hover {
    transform: translateY(-5px);
}

.author-info {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
}

.avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 15px;
    border: 2px solid #ffd100;
    cursor: pointer;
    transition: transform 0.3s ease;
}

.avatar:hover {
    transform: scale(1.1);
}

.details h3 {
    margin: 0 0 5px 0;
    font-size: 18px;
    color: #333;
    cursor: pointer;
    transition: color 0.3s ease;
}

.details h3:hover {
    color: #f0940a;
}

.meta {
    margin: 0;
    font-size: 12px;
    color: #888;
}

.stats {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    padding: 10px 0;
    border-top: 1px solid #eee;
    border-bottom: 1px solid #eee;
}

.stat-item {
    text-align: center;
    flex: 1;
}

.stat-value {
    display: block;
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 4px;
}

.stat-label {
    font-size: 12px;
    color: #888;
}

.follow-btn {
    width: 100%;
    padding: 8px;
    background: #ffd100;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background 0.2s;
}

.follow-btn:hover {
    background: #f3a806;
    color: #1f1e1e;
}
</style>