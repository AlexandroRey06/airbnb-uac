import { defineStore } from 'pinia'
import { userService } from '@/services/userService'
import { handleApiError } from '@/services/errorHandler'


export const useUsersStore = defineStore('users', {
  state: () => ({
    users: [],
    loading: false,
    error: null,
    currentUser: null
  }),

  getters: {
    getUserById: (state) => (id) => {
      return state.users.find(user => user.id === id)
    }
  },

  actions: {
    async fetchUsers() {
      this.loading = true
      this.error = null
      try {
        const data = await userService.getAll()
        this.users = data
      } catch (error) {
        this.error = handleApiError(error)
      } finally {
        this.loading = false
      }
    },

    async fetchUser(id) {
      this.loading = true
      this.error = null
      try {
        const user = await userService.getById(id)
        this.currentUser = user
        return user
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    },

    async createUser(userData) {
      this.loading = true
      this.error = null
      try {
        const newUser = await userService.create(userData)

        await this.fetchUsers()
        return newUser
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    },

    async updateUser(id, userData) {
      this.loading = true
      this.error = null
      try {
        const updatedUser = await userService.update(id, userData)

        await this.fetchUsers()
        return updatedUser
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    },

    async deleteUser(id) {
      this.loading = true
      this.error = null
      try {
        await userService.delete(id)
        await this.fetchUsers()
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    }
  }
})