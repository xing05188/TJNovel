<template>
    <div id="app-root">
        <NotificationBell class="global-bell" />
        <router-view />
    </div>
</template>

<script setup>
import NotificationBell from '@/components/NotificationBell.vue'
</script>

<style>
/* 全局基础样式 */
body {
    margin: 0;
    font-family: Arial, sans-serif;
    line-height: 1.6;
}

.global-bell {
    position: fixed;
    top: 16px;
    right: 20px;
    z-index: 10000;
}
</style>
