<template>
  <div class="app-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="sidebar-brand" @click="router.push('/dashboard')">
        <div class="brand-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="6" width="32" height="28" rx="3" stroke="currentColor" stroke-width="2.3" fill="none"/>
            <line x1="14" y1="6" x2="14" y2="34" stroke="currentColor" stroke-width="1.8"/>
            <rect x="18" y="11" width="8" height="2.5" rx="1.2" fill="currentColor" opacity="0.7"/>
            <rect x="18" y="16" width="14" height="2.5" rx="1.2" fill="currentColor" opacity="0.7"/>
            <rect x="18" y="21" width="11" height="2.5" rx="1.2" fill="currentColor" opacity="0.7"/>
          </svg>
        </div>
        <div class="brand-text" v-show="!isCollapse">
          <span class="brand-title">图书管理</span>
          <span class="brand-sub">新疆财经大学</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <template v-for="menu in menus" :key="menu.id">
          <div v-if="menu.children && menu.children.length" class="nav-group">
            <div class="nav-group-title" v-show="!isCollapse">{{ menu.name }}</div>
            <router-link
              v-for="child in menu.children"
              :key="child.id"
              :to="child.path"
              class="nav-item"
              :class="{ active: route.path === child.path }"
            >
              <el-icon class="nav-icon"><component :is="child.icon || 'Document'" /></el-icon>
              <span class="nav-label" v-show="!isCollapse">{{ child.name }}</span>
            </router-link>
          </div>
          <router-link
            v-else
            :to="menu.path || '/'"
            class="nav-item"
            :class="{ active: route.path === menu.path }"
          >
            <el-icon class="nav-icon"><component :is="menu.icon || 'Document'" /></el-icon>
            <span class="nav-label" v-show="!isCollapse">{{ menu.name }}</span>
          </router-link>
        </template>
      </nav>

      <div class="sidebar-footer">
        <button class="collapse-trigger" @click="isCollapse = !isCollapse">
          <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
        </button>
      </div>
    </aside>

    <!-- Main -->
    <div class="main-area">
      <header class="topbar">
        <div class="topbar-left">
          <div class="breadcrumb">
            <span class="breadcrumb-current">{{ route.meta.title || '' }}</span>
          </div>
        </div>
        <div class="topbar-right">
          <span class="user-name">{{ authStore.userInfo?.realName }}</span>
          <div class="user-avatar">
            {{ (authStore.userInfo?.realName || '?')[0] }}
          </div>
          <button class="logout-btn" @click="handleLogout" title="退出登录">
            <el-icon><SwitchButton /></el-icon>
          </button>
        </div>
      </header>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Fold, Expand, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

const menus = ref<any[]>([])

onMounted(async () => {
  if (!authStore.userInfo) {
    const info = await authStore.getUserInfo()
    menus.value = info?.menus || []
  } else {
    menus.value = (authStore.userInfo as any)?.menus || []
  }
})

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background: #F7F4EE;
}

/* ===== Sidebar ===== */
.sidebar {
  width: 240px;
  background: #1A2F2A;
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 72px;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 20px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  user-select: none;
}

.brand-icon {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  color: #D4A853;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 0;
  overflow: hidden;
  white-space: nowrap;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
  color: #F0EBE0;
  letter-spacing: 0.04em;
}

.brand-sub {
  font-size: 11px;
  color: rgba(240, 235, 224, 0.4);
  letter-spacing: 0.06em;
}

/* ===== Nav ===== */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.nav-group {
  margin-bottom: 4px;
}

.nav-group-title {
  padding: 16px 20px 8px;
  font-size: 11px;
  font-weight: 700;
  color: rgba(200, 205, 195, 0.4);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 20px;
  margin: 2px 10px;
  border-radius: 8px;
  color: #B8C9C3;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
}

.nav-item:hover {
  background: #243D35;
  color: #E8DCC8;
}

.nav-item.active {
  background: #2E4F44;
  color: #D4A853;
  font-weight: 600;
}

.nav-icon {
  font-size: 18px;
  flex-shrink: 0;
  width: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-label {
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ===== Sidebar Footer ===== */
.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255,255,255,0.06);
}

.collapse-trigger {
  width: 100%;
  padding: 8px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: rgba(200,205,195,0.5);
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.collapse-trigger:hover {
  background: #243D35;
  color: #D4A853;
}

/* ===== Main Area ===== */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* ===== Topbar ===== */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 28px;
  background: #FFFFFF;
  border-bottom: 1px solid #EBE5D8;
  flex-shrink: 0;
}

.breadcrumb-current {
  font-size: 16px;
  font-weight: 600;
  color: #2C2416;
  letter-spacing: 0.02em;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  color: #6B5E48;
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8B6914, #A67C1E);
  color: #FFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.logout-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #9B8E78;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: #F5EEE0;
  color: #B85450;
}

/* ===== Content ===== */
.content {
  flex: 1;
  padding: 28px;
  overflow-y: auto;
}

/* ===== Page transition ===== */
.page-enter-active,
.page-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
