<template>
    <div 
      :class="[
        'rounded-md p-4',
        typeClasses[type]
      ]"
      v-if="show"
    >
      <div class="flex">
        <div class="flex-shrink-0">
          <component 
            :is="icon" 
            class="h-5 w-5" 
            :class="iconClasses[type]" 
            aria-hidden="true"
          />
        </div>
        <div class="ml-3">
          <h3 
            v-if="title"
            :class="[
              'text-sm font-medium',
              textClasses[type]
            ]"
          >
            {{ title }}
          </h3>
          <div 
            :class="[
              'text-sm',
              textClasses[type],
              { 'mt-2': title }
            ]"
          >
            {{ message }}
          </div>
        </div>
        <div class="ml-auto pl-3">
          <div class="-mx-1.5 -my-1.5">
            <button
              type="button"
              :class="[
                'inline-flex rounded-md p-1.5',
                buttonClasses[type]
              ]"
              @click="$emit('close')"
            >
              <span class="sr-only">Dismiss</span>
              <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'VAlert',
    props: {
      show: {
        type: Boolean,
        default: true
      },
      type: {
        type: String,
        default: 'info',
        validator: function(value) {
          return ['success', 'error', 'warning', 'info'].indexOf(value) !== -1
        }
      },
      title: {
        type: String,
        default: ''
      },
      message: {
        type: String,
        required: true
      }
    },
    emits: ['close'],
    data() {
      return {
        typeClasses: {
          success: 'bg-green-50',
          error: 'bg-red-50',
          warning: 'bg-yellow-50',
          info: 'bg-blue-50'
        },
        textClasses: {
          success: 'text-green-800',
          error: 'text-red-800',
          warning: 'text-yellow-800',
          info: 'text-blue-800'
        },
        iconClasses: {
          success: 'text-green-400',
          error: 'text-red-400',
          warning: 'text-yellow-400',
          info: 'text-blue-400'
        },
        buttonClasses: {
          success: 'bg-green-50 text-green-500 hover:bg-green-100',
          error: 'bg-red-50 text-red-500 hover:bg-red-100',
          warning: 'bg-yellow-50 text-yellow-500 hover:bg-yellow-100',
          info: 'bg-blue-50 text-blue-500 hover:bg-blue-100'
        }
      }
    },
    computed: {
      icon() {
        const icons = {
          success: 'CheckCircleIcon',
          error: 'XCircleIcon',
          warning: 'ExclamationIcon',
          info: 'InformationCircleIcon'
        }
        return icons[this.type]
      }
    }
  }
  </script>