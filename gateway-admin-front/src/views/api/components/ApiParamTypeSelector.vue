<template>
  <div>
    <template v-if="mode === 'view'">{{description}}</template>
    <template v-if="mode === 'editor'">
      <el-select v-model="currentValue" placeholder="请选择参数类型" size="small" style="width: 100%"
                 :disabled="disabled || currentValue === 60">
        <el-option v-for="item in options"
                   :key="item.value"
                   :label="item.label"
                   :value="item.value"
                   :disabled="item.disabled"/>
      </el-select>
    </template>
  </div>
</template>

<script>
  export default {
    name: 'ApiParamTypeSelector',
    props: {
      value: {type: Number, default: 0},
      disabled: {type: Boolean, default: false},
      mode: {type: String, default: 'editor'}
    },
    data: function () {
      return {
        currentValue: 0,
        options: [
          {value: 0, label: '选择类型', disabled: true},
          {value: 50, label: 'String'},
          {value: 51, label: 'File', disabled: true},
          {value: 60, label: 'Object', disabled: true},
          {value: 20, label: 'Integer'},
          {value: 21, label: 'Long'},
          {value: 26, label: 'Double'},
          {value: 22, label: 'Short'},
          {value: 23, label: 'Byte'},
          {value: 24, label: 'Character'},
          {value: 25, label: 'Float'},
          {value: 27, label: 'Boolean'},
          {value: 10, label: 'int'},
          {value: 11, label: 'long'},
          {value: 12, label: 'short'},
          {value: 13, label: 'byte'},
          {value: 14, label: 'char'},
          {value: 15, label: 'float'},
          {value: 16, label: 'double'},
          {value: 17, label: 'boolean'}
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
    },
    computed: {
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