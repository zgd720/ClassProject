import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const name = ref(localStorage.getItem('name') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(t, r, n) {
    token.value = t
    role.value = r
    name.value = n
    localStorage.setItem('token', t)
    localStorage.setItem('role', r)
    localStorage.setItem('name', n)
  }

  function logout() {
    token.value = ''
    role.value = ''
    name.value = ''
    localStorage.clear()
  }

  return { token, role, name, isLoggedIn, setAuth, logout }
})
