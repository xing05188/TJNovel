<template>
    <div class="reader-card">
        <div class="reader-info">
            <img :src="getFullAvatarUrl(reader.avatarUrl)"
                alt="读者头像" class="avatar" @click="goReaderHome" @error="handleAvatarError" />
            <div class="details">
                <h3 @click="goReaderHome">{{ reader.readerName }}</h3>
                <p class="meta">性别: {{ reader.gender || '未设置' }}</p>
                <p class="meta">联系方式: {{ maskedPhone }}</p>
            </div>
        </div>
        <div class="stats">
            <div class="stat-item">
                <span class="stat-value">{{ reader.isCollectVisible === '否' ? '私密' : '公开' }}</span>
                <span class="stat-label">收藏状态</span>
            </div>
            <div class="stat-item">
                <span class="stat-value">{{ reader.isRecommendVisible === '否' ? '私密' : '公开' }}</span>
                <span class="stat-label">推荐状态</span>
            </div>
        </div>
        <button class="follow-btn" @click="goReaderHome">进入主页</button>
    </div>
</template>

<script setup>
import { defineProps, computed } from 'vue';
import { useRouter } from 'vue-router';
import defaultAvatar from '@/assets/default-avatar.jpeg'
const router = useRouter();

const props = defineProps({
    reader: {
        type: Object,
        required: true
    }
});

const maskedPhone = computed(() => {
    if (!props.reader.phone) return '未公开';
    return props.reader.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
});

// 格式化头像URL的工具函数
function getFullAvatarUrl(avatarUrl) {
    const ossBase = 'https://novelprogram123.oss-cn-hangzhou.aliyuncs.com/'
    
    if (!avatarUrl || avatarUrl.trim() === '' || avatarUrl === 'null' || avatarUrl === 'undefined') {
        return defaultAvatar
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

function goReaderHome() {
    router.push(`/reader/${props.reader.readerId}`);
}
</script>

<style scoped>
.reader-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s;
    width: 340px;
}

.reader-card:hover {
    transform: translateY(-5px);
}

.reader-info {
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
}

.avatar:hover {
    transform: scale(1.1);
}

.details h3 {
    margin: 0 0 5px 0;
    font-size: 18px;
    color: #333;
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
    background: #ffea80;
}
</style>