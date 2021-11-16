<template>
  <el-select v-model="currentValue" placeholder="请选择参数类型" size="small"
             :disabled="disabled">
    <el-option v-for="item in options" :key="item.value" :label="item.label"
               :value="item.value"
               :disabled="item.disabled"/>
  </el-select>
</template>

<script>
  export default {
    name: 'ApiParamRequiredSelector',
    props: {
      value: {type: Number, default: -1},
      disabled: {type: Boolean, default: false}
    },
    data: function () {
      return {
        currentValue: -1,
        options: [
          {value: -1, label: '选择类型', disabled: true},
          {value: 0, label: '非必填'},
          {value: 1, label: '必填'},
        ]
      }
    },
    created() {
      this.currentValue = this.value
    },
    watch: {
      value(val) {
        this.currentValue = val
      },
      currentValue(val) {
        this.$emit('update:value', val)
        this.$emit('change', val)
      }
    }
  }
</script>