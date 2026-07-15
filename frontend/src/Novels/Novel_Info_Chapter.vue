<template>
  <div>
    <!-- 主内容块：章节列表 + 整本购买按钮 -->
    <div class="chapter-list">
      <!-- 标题和按钮并排对齐 -->
      <div class="chapter-header-bar">
        <h2 class="chapter-title">章节目录</h2>
        <div v-if="selectNovelState.status === '完结'">
          <button :disabled="hasPurchased" class="whole-puy-btn" @click="showPurchaseModal = true">
            {{ hasPurchased ? '已买断' : '整本购买' }}
          </button>
        </div>
      </div>
      <!-- 章节列表 -->
      <ul v-if="displayedChapters.length > 0">
        <li v-for="chapter in displayedChapters" :key="chapter.chapterId" @click="handleChapterClick(chapter)" :class="['chapter-item', {
          'banned': isDisabled(chapter),
          'locked': chapter.isCharged === '是' && !chapter.hasPurchased && !hasPurchased,
          'vip': chapter.isCharged === '是',
          'current': chapter.chapterId === selectNovelState.chapterId
        }]">
          <div class="chapter-info">
            <span class="chapter-number">第{{ chapter.chapterId }}章</span>
            <span class="chapter-title">
              {{ chapter.title }}
              <span v-if="chapter.status === '封禁'" class="banned-tag">【封禁中】</span>
              <span v-else-if="chapter.status === '审核中'" class="banned-tag">【审核中】</span>
            </span>
            <span v-if="chapter.isCharged === '是'" class="charged">
              <span v-if="chapter.status === '已发布' && !chapter.hasPurchased && !hasPurchased" class="lock-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="16" height="16">
                  <path fill="currentColor"
                    d="M224 448a32 32 0 0 0-32 32v384a32 32 0 0 0 32 32h576a32 32 0 0 0 32-32V480a32 32 0 0 0-32-32zm0-64h576a96 96 0 0 1 96 96v384a96 96 0 0 1-96 96H224a96 96 0 0 1-96-96V480a96 96 0 0 1 96-96">
                  </path>
                  <path fill="currentColor"
                    d="M512 544a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V576a32 32 0 0 1 32-32m192-160v-64a192 192 0 1 0-384 0v64zM512 64a256 256 0 0 1 256 256v128H256V320A256 256 0 0 1 512 64">
                  </path>
                </svg>
              </span>（收费）</span>
            <span v-else class="free">（免费）</span>
          </div>
        </li>
      </ul>
      <!-- 如果章节为空 -->
      <p v-else>作者还在努力敲字中，感谢您的关注~</p>
      <!-- 分页组件 -->
      <div v-if="totalPages > 1" class="pagination-container">
        <button class="prev" @click="changePage(currentPage - 1)" :disabled="currentPage === 1">
          🡄
        </button>
        <span class="page-info">当前：{{ currentPage }}页 / 共{{ totalPages }}页</span>
        <button class="next" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">
          🡆
        </button>
      </div>
    </div>
    <!-- ✅ Teleport 到 body，必须在根元素外层并写在 template 内 -->
    <teleport to="body">
      <div v-if="showPurchaseModal" class="modal-overlay">
        <div class="modal">
          <div class="modal-header">
            <span>限时优惠！</span>
            <button class="close-btn" @click="showPurchaseModal = false">×</button>
          </div>
          <div class="modal-body">
            <p>本书整本价格为 <strong>{{ selectNovelState.totalPrice }}币</strong></p>
          </div>
          <div class="modal-footer">
            <button class="confirm-btn" @click="confirmPurchase">确认购买</button>
          </div>
        </div>
      </div>
      <div v-if="showBalanceInsufficientDialog" class="insufficient-dialog-overlay">
        <div class="insufficient-dialog">
          <div class="dialog-header">
            <h3>购买章节</h3>
            <button class="re_close-btn" @click="showBalanceInsufficientDialog = false">&times;</button>
          </div>
          <div class="insufficient-content">
            <p class="insufficient-message">账户余额不足</p>
            <div class="amount-info">
              <span>本次购买 {{ selectNovelState.totalPrice }} 币</span>
              <span>账户余额 {{ readerStore.balance }} 币·还差 {{ (selectNovelState.totalPrice - readerStore.balance) }}
                币</span>
            </div>
            <div class="quick-payment">
              <button class="recharge-btn" @click="goToRecharge">去充值</button>
            </div>
          </div>
        </div>
      </div>
      <!-- 单章购买弹窗 -->
      <div v-if="showChapterPurchaseDialog" class="purchase-dialog-overlay">
        <div class="purchase-dialog">
          <div class="dialog-header"
            style="display: flex; justify-content: center; align-items: center; position: relative;">
            <h3 style="margin: 0;">购买第{{ selectedChapter.chapterId }}章</h3>
            <button class="da_close-btn" @click="showChapterPurchaseDialog = false"
              style="position: absolute; right: 20px;">&times;</button>
          </div>
          <div class="purchase-content">
            <p>本章节价格为 {{ selectedChapter.calculatedPrice }}虚拟币</p>
          </div>
          <button class="confirm-reward-btn" @click="purchase_Chapter">
            确认购买
          </button>
        </div>
      </div>
      <!-- 单章购买余额不足弹窗 -->
      <div v-if="showChapterInsufficientDialog" class="purchase-insufficient-overlay">
        <div class="purchase-insufficient-dialog">
          <div class="purchase-dialog-header">
            <h3>章节购买</h3>
            <button class="purchase-close-btn" @click="showChapterInsufficientDialog = false">&times;</button>
          </div>
          <div class="purchase-insufficient-content">
            <p class="purchase-insufficient-message">账户余额不足，无法购买本章节</p>
            <div class="purchase-amount-info">
              <span>章节价格：{{ selectedChapter.calculatedPrice }}币</span>
              <span>当前余额：{{ readerStore.balance }}币</span>
            </div>
            <div class="purchase-action-buttons">
              <button class="purchase-recharge-btn" @click="goToRecharge">立即充值</button>
              <button class="purchase-cancel-btn" @click="showChapterInsufficientDialog = false">取消</button>
            </div>
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { getNovelChaptersWithoutContent, getChapter } from '@/API/Chapter_API'
import { getWholePurchaseStatus, purchaseWholeNovel } from '@/API/Transaction_API'
import { readerState, SelectNovel_State } from '@/stores/index'
import { addOrUpdateRecentReading, getReaderBalance } from '@/API/Reader_API';
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { useRouter } from 'vue-router';
import { checkPurchase, purchaseChapter } from '@/API/Purchase_API';

const router = useRouter();
const readerStore = readerState()
const selectNovelState = SelectNovel_State()

const readerId = readerStore.readerId
const novelId = selectNovelState.novelId

// 章节相关
const chapterList = ref([])
const displayedChapters = ref([])
const currentPage = ref(1)
const itemsPerPage = 10

// 整本买断状态
const showPurchaseModal = ref(false)
const hasPurchased = ref(false)

// 单章购买相关
const showChapterPurchaseDialog = ref(false)
const showChapterInsufficientDialog = ref(false)
const selectedChapter = ref(null)

// 过滤掉草稿章节
const visibleChapters = computed(() =>
  chapterList.value.filter(ch => ch.status !== '草稿' && ch.status !== '首次审核')
);

// 总页数
const totalPages = computed(() =>
  Math.ceil(visibleChapters.value.length / itemsPerPage) || 1
);

// 初始化加载章节
onMounted(async () => {
  try {
    const response = await getNovelChaptersWithoutContent(novelId);
    // 获取所有章节的购买状态
    const chaptersWithPurchaseStatus = await Promise.all(
      response
        .filter(chapter => chapter.status !== '草稿' && chapter.status !== '首次审核')
        .map(async chapter => {
          // 如果是收费章节，检查是否已购买
          if (chapter.isCharged === '是') {
            try {
              const purchaseStatus = await checkPurchase(
                readerId,
                chapter.novelId,
                chapter.chapterId
              );
              // 后端返回 ApiResponse<Boolean>，axios 拦截器直接返回布尔值
              // 如果已整本买断，也视为已购买
              return {
                ...chapter,
                hasPurchased: !!purchaseStatus || hasPurchased.value
              };
            } catch (error) {
              console.error(`检查章节 ${chapter.chapterId} 购买状态失败:`, error);
              return {
                ...chapter,
                hasPurchased: hasPurchased.value || false
              };
            }
          }
          // 免费章节直接标记为已购买
          return {
            ...chapter,
            hasPurchased: true
          };
        })
    );
    chapterList.value = chaptersWithPurchaseStatus;
    await updateDisplayedChapters();
  } catch (err) {
    console.error('获取章节失败:', err);
    chapterList.value = [];
  }
});

onMounted(async () => {
  try {
    const status = await getWholePurchaseStatus(readerId, novelId)
    hasPurchased.value = status?.hasPurchased || false
  } catch (err) {
    console.error('查询买断状态失败', err)
  }
})

// 分页切换
async function changePage(page) {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
  await updateDisplayedChapters();
}

// 更新显示的章节（先清空再加载）
async function updateDisplayedChapters() {
  displayedChapters.value = []; // 清空章节列表
  await nextTick(); // 等 DOM 更新后重新赋值
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = currentPage.value * itemsPerPage;
  displayedChapters.value = visibleChapters.value.slice(start, end);
}

async function confirmPurchase() {
  try {
    const res = await purchaseWholeNovel({ readerId, novelId })

    if (res.success === 1) {
      hasPurchased.value = true
      showPurchaseModal.value = false
      // 更新所有章节的购买状态
      chapterList.value = chapterList.value.map(chapter => ({
        ...chapter,
        hasPurchased: true
      }));
      await updateDisplayedChapters();
      toast.success('购买成功！', { autoClose: 2000 })
    } else {
      handleBalanceCheckAndError(res.message)
    }

  } catch (err) {
    console.error('购买失败:', err)
    const backendMsg = err?.response?.data?.message || ''
    if (backendMsg.includes('余额不足')) {
      showBalanceInsufficientDialog.value = true
    } else {
      toast.error('❌ 发生错误，请稍后再试', { autoClose: 2000 })
    }
  }
}

const showBalanceInsufficientDialog = ref(false);
function handleBalanceCheckAndError(msg) {
  const balance = readerStore.balance ?? 0
  const price = selectNovelState.totalPrice ?? 0

  if (balance < price || msg.includes('余额不足')) {
    showBalanceInsufficientDialog.value = true
  } else {
    toast.warning(msg || '购买失败，请重试', { autoClose: 2000 })
  }
}

const goToRecharge = () => {
  showBalanceInsufficientDialog.value = false;
  showPurchaseModal.value = false;
  showChapterPurchaseDialog.value = false;
  router.push('/Novels/Novel_Recharge');
};

// 禁止点击的章节
function isDisabled(chapter) {
  return chapter.status === '封禁' || chapter.status === '审核中';
}

// 处理章节点击
async function handleChapterClick(chapter) {
  try {
    // 如果是收费章节且未购买，显示购买弹窗
    if (chapter.status === '已发布' && chapter.isCharged === '是' && !chapter.hasPurchased && !hasPurchased.value) {
      selectedChapter.value = chapter;
      showChapterPurchaseDialog.value = true;
      return;
    }
    selectChapter(chapter.chapterId);
  } catch (error) {
    toast("章节加载失败!", {
      "type": "info",
      "dangerouslyHTMLString": true
    });
  }

}

// 购买单章
async function purchase_Chapter() {
  try {
    if (readerStore.balance < selectedChapter.value.calculatedPrice) {
      showChapterPurchaseDialog.value = false;
      showChapterInsufficientDialog.value = true;
      return;
    }

    const response = await purchaseChapter({
      readerId: readerStore.readerId,
      novelId: selectedChapter.value.novelId,
      chapterId: selectedChapter.value.chapterId
    });

    // 响应拦截器会返回data字段，成功时data是字符串"章节购买成功"
    // 如果response存在且不是错误，说明购买成功
    if (response !== undefined && response !== null) {
      // 更新章节购买状态
      const chapterIndex = chapterList.value.findIndex(
        c => c.chapterId === selectedChapter.value.chapterId
      );
      if (chapterIndex !== -1) {
        chapterList.value[chapterIndex].hasPurchased = true;
      }
      await updateDisplayedChapters();
      // 从后端重新获取最新余额并同步到全局 store，避免前端手动减产生误差
      try {
        const latestBalance = await getReaderBalance(readerStore.readerId);
        // axios 响应拦截器会直接返回数值
        readerStore.updateBalance(Number(latestBalance) || 0);
      } catch (balanceError) {
        console.error('刷新余额失败:', balanceError);
        // 如果获取余额失败，使用前端计算的值作为降级方案
        readerStore.balance -= selectedChapter.value.calculatedPrice;
      }
      showChapterPurchaseDialog.value = false;
      toast("购买成功！", {
        type: "success",
        dangerouslyHTMLString: true
      });
      //停顿2s
      await new Promise(resolve => setTimeout(resolve, 2000));
      // 跳转到该章节
      selectChapter(selectedChapter.value.chapterId);
    } else {
      toast("购买失败: 未知错误", {
        type: "error",
        dangerouslyHTMLString: true
      });
    }
  } catch (error) {
    console.error('购买章节失败:', error);
    if (error.response && error.response.status === 400) {
      showChapterPurchaseDialog.value = false;
      showChapterInsufficientDialog.value = true;
    } else {
      toast("购买失败: " + (error.message || "未知错误"), {
        type: "error",
        dangerouslyHTMLString: true
      });
    }
  } finally {
    showChapterPurchaseDialog.value = false;
  }
}

// 选中章节后存入全局状态
async function selectChapter(chapterId) {
  try {
    const response = await getChapter(novelId, chapterId);
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
        readerStore.readerId,      // 读者ID
        selectNovelState.novelId,   // 小说ID
        chapterId            // 实际阅读的章节ID
      );
    } catch (historyError) {
      console.error("记录阅读历史失败:", historyError);
    }

    router.push('/Novels/reader');
  } catch (error) {
    toast("第" + chapterId + "章加载失败!", {
      type: "error",
      dangerouslyHTMLString: true
    });
    console.error('获取章节内容失败:', error);
  }
}
</script>

<style scoped>
.chapter-list {
  padding: 20px;
  background-color: #edf1f1ff;
  border-radius: 8px;
}

.chapter-item {
  cursor: pointer;
  padding: 15px;
  border-bottom: 1px solid #edf1f1ff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  transition: background-color 0.3s ease;
  position: relative;
}

.chapter-item:hover {
  background-color: #EFE9D3;
}

.chapter-item.banned {
  background-color: #e0e0e0;
}

.chapter-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.chapter-number {
  font-size: 14px;
  color: #555;
}

.chapter-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-left: 10px;
  display: inline;
  white-space: nowrap;
}

.charged {
  color: red;
  font-size: 14px;
}

.free {
  color: green;
  font-size: 14px;
}

.banned-tag {
  color: #ff6600;
  margin-left: 10px;
  font-weight: bold;
}

.lock-icon {
  font-weight: 700px;
  margin-right: 10px;
  color: #ff6600;
}

p {
  font-size: 16px;
  color: #888;
  text-align: center;
  margin-top: 30px;
}

.page-info {
  font-size: 14px;
  color: #555;
  margin: 0 5px;
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin-top: 20px;
  gap: 10px;
}

.pagination-container button {
  background-color: #bfc5d5;
  color: white;
  border: none;
  padding: 5px 10px;
  margin: 0 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
  font-size: 18px;
}

.pagination-container button:hover {
  background-color: #bfc5d5;
}

.pagination-container button:disabled {
  background-color: #dcdcdc;
  cursor: not-allowed;
}

.pagination-container span {
  font-size: 16px;
  color: #555;
}

.chapter-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 0 10px;
  flex-wrap: wrap;
}

.whole-puy-btn {
  background: linear-gradient(135deg, #4a90e2, #357ABD);
  color: white;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.whole-puy-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.whole-puy-btn:disabled {
  background: #cccccc;
  color: #666;
  cursor: not-allowed;
  box-shadow: none;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  background-color: #e7dbcb;
  border-radius: 10px;
  padding: 20px;
  width: 25vw;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  font-weight: bold;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 24px;
  font-weight: bold;
  color: #666;
  cursor: pointer;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #d0021b;
  transform: scale(1.2);
}

.modal-body {
  margin: 15px 0;
  font-size: 16px;
}

.confirm-btn {
  width: 100%;
  padding: 10px;
  background-color: #3f83f8;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.insufficient-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  text-align: center;
}

.insufficient-dialog {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  margin-left: 0px;
}

.re_close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  margin-left: auto;
  margin-right: 0;
  display: flex;
  align-items: center;
}

.insufficient-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.insufficient-message {
  color: #f56c6c;
  font-size: 16px;
  text-align: center;
  margin: 10px 0;
}

.amount-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
  color: #666;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.quick-payment {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  padding-top: 10px;
}

.recharge-btn {
  width: 100%;
  padding: 12px;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.recharge-btn:hover {
  background-color: #e65c5c;
}

/* 单章购买弹窗样式 */
.purchase-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.purchase-dialog {
  background-color: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  padding: 25px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.3s ease;
  z-index: 10000;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.purchase-content {
  text-align: center;
  margin: 25px 0;
  font-size: 18px;
  color: #333;
  padding: 0 20px;
}

.purchase-content p {
  margin: 15px 0;
  font-weight: bold;
  font-size: 20px;
  color: #f56c6c;
}

.confirm-reward-btn {
  width: 100%;
  padding: 12px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 15px;
}

.confirm-reward-btn:hover {
  background-color: #ff5252;
}

.da_close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  transition: color 0.2s;
}

.da_close-btn:hover {
  color: #666;
}

/* 单章购买余额不足弹窗 */
.purchase-insufficient-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10001;
}

.purchase-insufficient-dialog {
  background-color: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  padding: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  animation: purchaseFadeIn 0.3s ease;
}

@keyframes purchaseFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.purchase-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.purchase-dialog-header h3 {
  margin: 0;
  font-size: 18px;
}

.purchase-close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.purchase-insufficient-content {
  padding: 10px 0;
}

.purchase-insufficient-message {
  color: #f56c6c;
  font-size: 16px;
  text-align: center;
  margin: 10px 0 20px;
}

.purchase-amount-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 20px 0;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.purchase-amount-info span {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
}

.purchase-action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.purchase-recharge-btn {
  flex: 1;
  padding: 12px;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.purchase-recharge-btn:hover {
  background-color: #e65c5c;
}

.purchase-cancel-btn {
  flex: 1;
  padding: 12px;
  background-color: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.purchase-cancel-btn:hover {
  background-color: #e0e0e0;
}
</style>