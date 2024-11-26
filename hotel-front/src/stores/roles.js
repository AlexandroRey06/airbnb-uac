import { defineStore } from 'pinia'
import { roleService } from '@/services/roleService'
import { handleApiError } from '@/services/errorHandler'


export const useRolesStore = defineStore('roles', {
  state: () => ({
    roles: [],
    loading: false,
    error: null,
    currentRole: null
  }),

  getters: {
    getRoleById: (state) => (id) => {
      return state.roles.find(role => role.id === id)
    }
  },

  actions: {
    async fetchRoles() {
      this.loading = true
      this.error = null
      try {
        const data = await roleService.getAll()
        this.roles = data
      } catch (error) {
        this.error = handleApiError(error)
      } finally {
        this.loading = false
      }
    },

    async fetchRole(id) {
      this.loading = true
      this.error = null
      try {
        const role = await roleService.getById(id)
        this.currentRole = role
        return role
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    },

    async createRole(roleData) {
      this.loading = true
      this.error = null
      try {
        const newRole = await roleService.create(roleData)
        await this.fetchRoles()
        return newRole
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    },

    async updateRole(id, roleData) {
      this.loading = true
      this.error = null
      try {
        const updatedRole = await roleService.update(id, roleData)
        await this.fetchRoles()
        return updatedRole
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    },

    async deleteRole(id) {
      this.loading = true
      this.error = null
      try {
        await roleService.delete(id)
        await this.fetchRoles()
      } catch (error) {
        this.error = handleApiError(error)
        throw this.error
      } finally {
        this.loading = false
      }
    }
  }
})