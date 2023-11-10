<template>
  <el-select
    v-model="value"
    class="paytypeselector"
    placeholder="Select"
    size="large"
    @change="$emit('selector-change')"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script lang="ts" setup>
import { ref } from "vue";

const value = ref(0);

defineEmits(["selector-change"]);

const options = [
  {
    value: 0,
    label: "支付宝",
  },
  {
    value: 1,
    label: "微信支付",
  },
];
</script>
