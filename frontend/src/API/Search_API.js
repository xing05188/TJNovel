import request from '@/API/index'

/**
 * 通过小说名模糊搜索小说
 * @param {string} keyword - 搜索关键词
 * @returns {Promise<Array<Novel>>} 返回小说数组
 */
export function searchNovels(keyword) {
    return request({
        url: '/api/Search/novel',
        method: 'get',
        params: {
            keyword: keyword
        },
        headers: {
            'Accept': 'application/json'
        }
    })
}

/**
 * 通过作者名模糊搜索作者
 * @param {string} keyword - 搜索关键词
 * @returns {Promise<Array<Author>>} 返回作者数组
 * @typedef {Object} Author  - 含6个元素
 * @property {number} authorId - 作者ID
 * @property {string} authorName - 作者名称
 * @property {string} password - 密码
 * @property {number} earning - 收入
 * @property {string|null} phone - 电话
 * @property {string|null} avatarUrl - 头像URL
 */
export function searchAuthors(keyword) {
    return request({
        url: '/api/Search/author',
        method: 'get',
        params: {
            keyword: keyword
        }
    })
}

/**
 * 通过读者名模糊搜索读者
 * @param {string} keyword - 搜索关键词
 * @returns {Promise<Array<Reader>>} 返回读者数组
 */
export function searchReaders(keyword) {
    return request({
        url: '/api/Search/reader',
        method: 'get',
        params: {
            keyword: keyword
        }
    })
}

/**
 * Elasticsearch 全文搜索小说（标题/简介/作者名模糊搜索）
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码（从0开始）
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 返回分页对象 { content: NovelDocument[], totalElements, totalPages, number, size }
 */
export function esSearchNovels(keyword, page = 0, size = 20) {
    return request({
        url: '/api/search/novels',
        method: 'get',
        params: {
            keyword: keyword,
            page: page,
            size: size
        }
    })
}

/**
 * Elasticsearch 按分类搜索小说
 * @param {string} category - 分类名称
 * @param {number} page - 页码（从0开始）
 * @param {number} size - 每页数量
 */
export function esSearchNovelsByCategory(category, page = 0, size = 20) {
    return request({
        url: '/api/search/novels/category',
        method: 'get',
        params: {
            category: category,
            page: page,
            size: size
        }
    })
}

/**
 * Elasticsearch 高级搜索（多条件组合）
 * @param {Object} params - { keyword, category, minWordCount, maxWordCount, status, page, size }
 */
export function esAdvancedSearch(params) {
    return request({
        url: '/api/search/novels/advanced',
        method: 'get',
        params: params
    })
}

/**
 * Elasticsearch 搜索章节内容（返回带 <em> 高亮片段）
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码（从0开始）
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 返回分页对象 { content: ChapterDocument[], totalElements, ... }
 */
export function esSearchChapters(keyword, page = 0, size = 20) {
    return request({
        url: '/api/search/chapters',
        method: 'get',
        params: {
            keyword: keyword,
            page: page,
            size: size
        }
    })
}

/**
 * Elasticsearch 搜索章节标题
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码（从0开始）
 * @param {number} size - 每页数量
 */
export function esSearchChapterTitles(keyword, page = 0, size = 20) {
    return request({
        url: '/api/search/chapters/titles',
        method: 'get',
        params: {
            keyword: keyword,
            page: page,
            size: size
        }
    })
}