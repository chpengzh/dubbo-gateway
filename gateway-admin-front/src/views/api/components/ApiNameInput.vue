<template>
  <div>
    <template v-if="mode === 'edit'">
      <el-input v-model="currentValue" placeholder="输入服务名称" :disabled="disabled"/>
    </template>
    <div v-if="mode === 'create'">
      <el-input v-model="suffixValue" placeholder="输入服务名称..." style="width: 100%"/>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'ApiNameInput',
    props: {
      mode: {type: String, default: 'edit'},
      value: {type: String, default: -1},
      disabled: {type: Boolean, default: false}
    },
    data: function () {
      return {
        prefix: [],
        currentValue: -1,
        prefixValue: '',
        suffixValue: '',
      }
    },
    created() {
      this.currentValue = this.value
    },
    watch: {
      value(val) {
        this.currentValue = val
      },
      prefixValue(val) {
        this.$emit('update:value', val + this.suffixValue)
        this.$emit('change', val + this.suffixValue)
      },
      suffixValue(val) {
        this.$emit('update:value', this.prefixValue + val)
        this.$emit('change', this.prefixValue + val)
      },
      currentValue(val) {
        this.$emit('update:value', val)
        this.$emit('change', val)
      }
    }
  }
</script>