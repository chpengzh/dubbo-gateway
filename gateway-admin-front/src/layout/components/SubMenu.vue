<template>
  <div>
    <div v-for="(val, key) in menuList" :key="key">
      <el-submenu
        v-if="val[props.children] && val[props.children].length > 0"
        :index="val[props.index]">
        <template slot="title">
          <i :class="val.icon"></i>
          <span>{{val[props.label]}}</span>
        </template>
        <SubMenu
          :menu-list="val[props.children]"
          :props="props"
          :depth="depth + 1"
          @getmenu="onMenuItemClick"/>
      </el-submenu>
      <el-menu-item
        v-else
        :index="val[props.index]"
        :class="'menu-depth-' + depth"
        @click="onMenuItemClick(val)">
        <i :class="val.icon"></i>
        <span slot="title">{{val[props.label]}}</span>
      </el-menu-item>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'SubMenu',
    props: {
      depth: {
        type: Number,
        default: 0
      },
      menuList: {
        type: Array,
        default() {
          return []
        }
      },
      props: {
        type: Object,
        default() {
          return {
            children: 'menuChildren',
            label: 'label',
            index: 'id',
            hidden: 'hidden'
          }
        }
      }
    },
    methods: {
      onMenuItemClick(item) {
        if (item.link) {
          window.open(item.link, '_blank')
        } else {
          this.$emit('getmenu', item)
        }
      }
    }
  }
</script>