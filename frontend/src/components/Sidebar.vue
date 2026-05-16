<template>
  <aside class="w-56 bg-white shadow-sm flex flex-col">
    <div class="p-4 border-b">
      <h2 class="text-sm font-medium text-gray-400 uppercase tracking-wide">菜单</h2>
    </div>
    <nav class="flex-1 p-2 space-y-1">
      <router-link to="/students"
        class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition"
        :class="$route.path.startsWith('/students') ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-600 hover:bg-gray-100'">
        <span>👤</span> 学生画像
      </router-link>
      <router-link to="/dashboard"
        class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition"
        :class="$route.path === '/dashboard' ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-600 hover:bg-gray-100'">
        <span>📊</span> 可视化看板
      </router-link>
      <router-link to="/data-import"
        class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition"
        :class="$route.path === '/data-import' ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-600 hover:bg-gray-100'">
        <span>📥</span> 数据管理
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
</script>
