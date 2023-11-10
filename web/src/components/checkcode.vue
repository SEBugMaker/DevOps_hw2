<template>
  <div class="hello">
    <h1>{{ props.msg }}</h1>
    <div ref="demo"></div>
  </div>
</template>

<script setup>
import { onMounted, defineEmits, defineProps, ref } from "vue";

const emit = defineEmits(["success"]);
const props = defineProps(["msg"]);
const demo = ref(null);

onMounted(() => {
  _dx.Captcha(demo.value, {
    // appId, 在控制台应用管理或应用配置模块获取
    appId: "73fbe1c9c72ca6ec6d5d2edf5f0f465b",
    apiServer: "https://cap.dingxiang-inc.com",
    // apiServer域名地址在控制台页面->无感验证->应用管页面左上角获取，必须填写完整包括https://。
    success: (token) => {
      // 获取验证码token，用于后端校验，注意获取token若是sl开头的字符串，则是前端网络不通生成的降级token,请检查前端网络及apiServer地址。
      console.log(token);
      emit("success");
      console.log("emitted");
    },
  });
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
