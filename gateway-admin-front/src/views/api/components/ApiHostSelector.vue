<template>
  <div>
    <template v-if="mode === 'view'">{{description}}</template>
    <template v-if="mode === 'editor'">
      <el-input placeholder="http://127.0.0.1:8086" v-model="currentValue"/>
    </template>
  </div>
</template>

<script>
  import {getEnv} from '@/common/env'

  const env = getEnv()
  export default {
    name: 'ApiHostSelector',
    props: {
      value: {type: String, default: ''},
      disabled: {type: Boolean, default: false},
      mode: {type: String, default: 'editor'}
    },
    data: function () {
      return {
        currentValue: '',
      }
    },
    created() {
      this.currentValue = this.enabledOptions.length > 0
        ? this.enabledOptions[0].value
        : ''
    },
    watch: {
      value(val) {
        this.currentValue = val
      },
      currentValue(val) {
        this.$emit('update:value', val)
        this.$emit('change', val)
      }
    },
    computed: {
      enabledOptions() {
        if (env === 'local') {
          return this.options
        }
        return this.options.filter(it => it.show)
      },
      description() {
        for (let i = 0; i < this.options.length; ++i) {
          if (this.options[i].value === this.currentValue) {
            return this.options[i].label
          }
        }
        return '--'
      }
    }
  }
</script>