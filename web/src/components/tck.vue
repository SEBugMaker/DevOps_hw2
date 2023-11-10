<!-- 
<template>
    <el-popover placement="top" :width="400" v-model:visible="visible">
    <checkcode @checkcodesuccess="alert('csndm')"></checkcode>
    <template #reference>
      <el-button @click="visible = true">删除</el-button>
    </template>
  </el-popover>
</template>
  
  <script>
    import {defineEmits} from 'vue'
    defineEmits(['tcksuccess'])
    export default {
      data() {
        return {
          visible: false,
        }
      },
    }
  </script> -->

<template>
  <el-popover id="popover1" placement="top" :width="320">
    <checkcode @success="getsuccess"></checkcode>
    <template #reference>
      <el-button @click="clickon">登录</el-button>
    </template>
  </el-popover>
</template>

<script setup>
import { defineEmits, ref } from "vue";
const emit = defineEmits(["tcksuccess"]);
let visible1 = ref(false);
var suc = false;
function getsuccess() {
  suc = true;
  emit("tcksuccess");
}
function clickon() {
  if (suc) {
    emit("tcksuccess");
  }
}
</script>
