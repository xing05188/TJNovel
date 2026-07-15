<template>
    <div class="novel-card">
        <div class="cover-container">
            <img :src="getCoverUrl(novel.coverUrl)"
                :alt="novel.novelName" class="cover-image" @click="handle_NovelInfro" @error="handleImageError" />
            <div class="score-badge" v-if="novel.score > 0">
                ★ {{ novel.score.toFixed(1) }}
            </div>
        </div>
        <div class="content">
            <div class="novel-header">
                <h3 class="title" @click="handle_NovelInfro">{{ novel.novelName }}</h3>
                <span class="status" :class="statusClass">{{ novel.status }}</span>
            </div>
            <p class="author">作者：{{ author_name || '未知作者' }}</p>
            <p class="introduction">{{ truncatedIntroduction }}</p>
            <div class="stats">
                <div class="stat-item">
                    <span class="stat-value">{{ novel.totalWordCount }}字</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">{{ collectedcount }}收藏</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">{{ novel.recommendCount }}推荐</span>
                </div>
            </div>
            <div class="footer">
                <span class="create-time">发布于: {{ formattedCreateTime }}</span>
                <div class="action-buttons">
                    <button class="action-btn add-shelf" @click="handleAddShelf" :class="{ 'in-shelf': isFavorite }"
                        :disabled="isFavorite">
                        {{ isFavorite ? '已在书架' : '放入书架' }}
                    </button>
                    <button class="action-btn read" @click="handleRead">立即阅读</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, defineProps } from 'vue';
import { useRouter } from 'vue-router';
import { SelectNovel_State, readerState } from '@/stores/index';
import { novelsStore } from '@/stores/Novels';
import { getAuthor } from '@/API/Author_API';
import { addOrUpdateCollect } from '@/API/Collect_API';
import { getChapter } from '@/API/Chapter_API';
import { checkPurchase } from '@/API/Purchase_API';
import { addOrUpdateRecentReading, getLastReadChapterId } from '@/API/Reader_API';
import { toast } from "vue3-toastify";
import "vue3-toastify/dist/index.css";

// 获取封面URL的统一方法
function getCoverUrl(coverUrl) {
    return novelsStore.getFullCoverUrl(coverUrl);
}

// 图片加载错误处理
function handleImageError(event) {
    event.target.src = 'https://novelprogram123.oss-cn-hangzhou.aliyuncs.com/e165315c-da2b-42c9-b3cf-c0457d168634.jpg';
}
const props = defineProps({
    novel: {
        type: Object,
        required: true
    }
});
const selectNovelState = SelectNovel_State();
const reader_state = readerState();
const router = useRouter();
const isFavorite = ref(reader_state.isFavorite(props.novel.novelId));
const collectedcount = ref(props.novel.collectedCount || 0);

// 加入书架/收藏
async function handleAddShelf() {
    if (isFavorite.value) return;
    const readerId = reader_state.readerId;
    const response = await addOrUpdateCollect(props.novel.novelId, readerId, 'yes');
    if (response) {
        isFavorite.value = true;
        reader_state.favoriteBooks.push({
            novelId: props.novel.novelId,
            novel: selectNovelState,
            readerId,
            isPublic: "yes",
            collectTime: new Date().toISOString()
        });
        collectedcount.value += 1; // 更新收藏数
        toast("收藏成功！", {
            "type": "success",
            "dangerouslyHTMLString": true
        });
    } else {
        toast("收藏失败：" + response.message, {
            "type": "error",
            "dangerouslyHTMLString": true
        });
    }
}

const author_name = ref('未知作者');
const authorData = ref({
    authorName: '未知作者',
    phone: null,
    avatarUrl: null,
    registerTime: null,
    introduction: null
});

const responsePromise = getAuthor(props.novel.authorId);
responsePromise.then(response => {
    author_name.value = response.authorName;
    authorData.value = response;
    console.log('作者信息获取成功:', response);
}).catch(error => {
    console.error('获取作者信息失败:', error);
    // 作者不存在，使用默认值
    author_name.value = '未知作者';
    authorData.value = {
        authorName: '未知作者',
        phone: null,
        avatarUrl: null,
        registerTime: null,
        introduction: null
    };
});

async function handle() {
    try {
        console.log('开始更新小说信息到状态管理');
        console.log('小说ID:', props.novel.novelId);
        console.log('小说名称:', props.novel.novelName);
        console.log('作者数据:', authorData.value);
        
        selectNovelState.resetNovel(
            props.novel.novelId,
            props.novel.authorId,
            props.novel.novelName,
            props.novel.introduction,
            props.novel.createTime,
            props.novel.coverUrl,
            props.novel.score,
            props.novel.totalWordCount,
            props.novel.recommendCount,
            props.novel.collectedCount,
            props.novel.status,
            props.novel.totalPrice,
            authorData.value.authorName,
            authorData.value.phone,
            authorData.value.avatarUrl,
            authorData.value.registerTime,
            authorData.value.introduction
        );
        console.log('小说信息已更新到状态管理！');
    } catch (error) {
        console.error('处理失败:', error);
    }
}

// 立即阅读
async function handleRead() {
    try {
        await handle();

        // 获取用户上次阅读的章节ID，如果没有记录则返回1
        let chapterIdToRead = 1;
        try {
            const lastReadResponse = await getLastReadChapterId(
                reader_state.readerId,
                selectNovelState.novelId
            );
            chapterIdToRead = lastReadResponse || 1;
        } catch (error) {
            console.warn("获取阅读历史失败，使用默认第1章:", error);
            chapterIdToRead = 1;
        }

        // 尝试获取目标章节
        let response;
        let validChapterFound = false;
        
        try {
            response = await getChapter(props.novel.novelId, chapterIdToRead);
            // 检查章节是否可用
            if (response && response.status !== '首次审核' && response.status !== '草稿') {
                validChapterFound = true;
            }
        } catch (error) {
            console.warn(`获取第${chapterIdToRead}章失败:`, error);
        }

        // 如果目标章节不可用且不是第1章，尝试获取第1章
        if (!validChapterFound && chapterIdToRead !== 1) {
            try {
                response = await getChapter(props.novel.novelId, 1);
                if (response && response.status !== '首次审核' && response.status !== '草稿') {
                    chapterIdToRead = 1;
                    validChapterFound = true;
                }
            } catch (fallbackError) {
                console.error("获取第1章也失败:", fallbackError);
            }
        }

        // 如果没有找到有效章节，显示错误并返回
        if (!validChapterFound) {
            toast("该作品暂无可阅读章节", {
                "type": "info",
                "dangerouslyHTMLString": true
            });
            return;
        }

        // 检查章节购买状态（仅对已发布的付费章节）
        if (response.status === '已发布' && response.isCharged === '是') {
            console.log('章节需要付费，检查购买状态...');
            console.log('readerId:', reader_state.readerId);
            console.log('novelId:', selectNovelState.novelId);
            console.log('chapterId:', chapterIdToRead);
            
            try {
                const purchaseStatus = await checkPurchase(
                    reader_state.readerId,
                    selectNovelState.novelId,
                    chapterIdToRead
                );
                console.log('购买状态响应:', purchaseStatus);
                console.log('purchaseStatus 类型:', typeof purchaseStatus);
                console.log('purchaseStatus.hasPurchased:', purchaseStatus?.hasPurchased);
                console.log('purchaseStatus.success:', purchaseStatus?.success);
                
                // 检查多种可能的返回格式
                const hasPurchased = purchaseStatus === true || 
                                   purchaseStatus?.hasPurchased === true || 
                                   purchaseStatus?.success === true;
                
                console.log('最终判断 hasPurchased:', hasPurchased);
                
                if (!hasPurchased) {
                    toast(`第${chapterIdToRead}章需要购买后才能阅读`, {
                        "type": "info",
                        "dangerouslyHTMLString": true
                    });
                    return;
                }
                console.log('已购买，允许进入阅读页面');
            } catch (error) {
                console.error('检查购买状态失败:', error);
                toast('检查购买状态失败，请稍后重试', {
                    "type": "error",
                    "dangerouslyHTMLString": true
                });
                return;
            }
        }

        // 保存章节信息到状态管理
        selectNovelState.resetChapter(
            response.chapterId,
            response.title,
            response.content,
            response.wordCount,
            response.pricePerKilo,
            response.calculatedPrice,
            response.isCharged,
            response.publishTime,
            response.status
        );

        // 添加或更新阅读记录
        try {
            await addOrUpdateRecentReading(
                reader_state.readerId,
                selectNovelState.novelId,
                chapterIdToRead
            );
        } catch (historyError) {
            console.error("记录阅读历史失败:", historyError);
        }

        // 跳转到阅读页面
        router.push('/Novels/reader');
    } catch (error) {
        console.error("阅读失败:", error);
        toast("进入阅读页面失败，请稍后重试", {
            "type": "error",
            "dangerouslyHTMLString": true
        });
    }
}

// 作品主页
async function handle_NovelInfro() {
    await handle();
    router.push('/Novels/Novel_Info/home');
}

const truncatedIntroduction = computed(() => {
    return props.novel.introduction && props.novel.introduction.length > 35
        ? props.novel.introduction.substring(0, 35) + '...'
        : props.novel.introduction || '暂无简介';
});

const formattedCreateTime = computed(() => {
    return props.novel.createTime
        ? new Date(props.novel.createTime).toLocaleDateString()
        : '未知时间';
});

const statusClass = computed(() => {
    return {
        'ongoing': props.novel.status === '连载中',
        'completed': props.novel.status === '已完结',
        'suspended': props.novel.status === '暂停'
    };
});
</script>

<style scoped>
.novel-card {
    display: flex;
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    max-width: 800px;
    margin: 0 auto;
}

.novel-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.cover-container {
    position: relative;
    width: 150px;
    min-width: 150px;
    height: 200px;
}

.cover-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    cursor: pointer;
}

.score-badge {
    position: absolute;
    top: 10px;
    left: 10px;
    background: rgba(0, 0, 0, 0.7);
    color: #f93e0a;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 16px;
    font-weight: bold;
}

.content {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.novel-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: -5px;
    align-items: flex-start;
    gap: 12px;
}

.title {
    margin: 0;
    font-size: 18px;
    color: #333;
    font-weight: 600;
    cursor: pointer;
    transition: color 0.2s;
    line-height: 1.3;
}

.title:hover {
    color: #e67d06;
}

.author {
    margin: 4px 0 0;
    font-size: 13px;
    color: #666;
    font-weight: 500;
    margin-bottom: -5px;
}

.status {
    background: #ffe357;
    color: #f09707;
    border-radius: 16px;
    font-size: 14px;
    font-weight: 500;
    font-size: 15px;
    padding: 4px 8px;
    margin-left: 10px;
}

.status.ongoing {
    background-color: #ffeb3b;
    color: #333;
}

.status.completed {
    background-color: #4caf50;
    color: white;
}

.status.suspended {
    background-color: #f44336;
    color: white;
}

.introduction {
    font-size: 14px;
    color: #666;
    line-height: 1.5;
    margin-bottom: 6px;
    flex: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.stats {
    display: flex;
    gap: 16px;
    margin-bottom: 6px;
}

.stat-item {
    font-size: 13px;
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #666;
}


.footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.create-time {
    font-size: 12px;
    color: #999;
}

.action-buttons {
    display: flex;
    gap: 8px;
}

.action-btn {
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s;
}

.action-btn.read {
    background: #ffd100;
    color: #333;
}

.action-btn.read:hover {
    background: #ffea80;
}

.action-btn.add-shelf {
    background: #f0f0f0;
    color: #666;
}

.action-btn.add-shelf:hover:not(.in-shelf) {
    background: #e0e0e0;
    color: #e67d06;
}

.action-btn.add-shelf.in-shelf {
    background: #f0f0f0;
    color: #999;
    cursor: default;
}

@media (max-width: 600px) {
    .novel-card {
        flex-direction: column;
    }

    .cover-container {
        width: 100%;
        height: 180px;
    }

    .content {
        padding: 12px;
    }
}
</style>