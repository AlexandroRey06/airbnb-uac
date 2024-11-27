import { defineStore } from 'pinia'
import { authService } from '@/services/authService'
import { getToken, removeToken } from '@/utils/auth'
import { handleApiError } from '@/utils/errorHandler'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: getToken(),
    isAuthenticated: false,
    loading: false,
    error: null
  }),

  getters: {
    isAdmin: (state) => (state.user?.rolId === 1 || state.user?.rolId === 3),
    currentUser: (state) => state.user
  },

  actions: {
    async checkAuth() {
      if (this.token && !this.user) {
        try {
            console.log(this.state.user);
            
          const user = await authService.getCurrentUser()
          this.user = user
          this.isAuthenticated = true
        } catch {
          this.logout()
        }
      }
    },

    async login(credentials) {
        this.loading = true
        this.error = null
        try {
            const data = await authService.login(credentials)
            this.user = data.user
            this.token = data.token
            this.isAuthenticated = true
            return data
        } catch (error) {
            this.error = handleApiError(error)
            throw this.error
        } finally {
            this.loading = false
        }
    },

    async register(userData) {
      this.loading = true;
      this.error = null;
      try {
        const data = await authService.register(userData);
        this.user = data.user;
        this.token = data.token;
        this.isAuthenticated = true;
        localStorage.setItem('token', data.token);
        return data;
      } catch (error) {
        this.error = handleApiError(error);
        throw this.error;
      } finally {
        this.loading = false;
      }
    },

    logout() {
      this.user = null
      this.token = null
      this.isAuthenticated = false
      removeToken()
    }
  }
})