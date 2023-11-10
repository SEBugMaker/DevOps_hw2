<script setup lang="ts">
import { request } from "~/utils/request";
import { ElNotification } from "element-plus";
import { h, onMounted, reactive, watch } from "vue";
import { useStationsStore } from "~/stores/stations";
import { parseDate } from "~/utils/date";
import { onBeforeRouteLeave, useRouter } from "vue-router";
import { OrderDetailData } from "~/utils/interfaces";
import { ref } from "vue";
const router = useRouter();
const stations = useStationsStore();

const props = defineProps({
  id: Number,
});

let orderDetail = reactive<{ data: OrderDetailData }>({
  data: {
    id: 0,
    train_id: 0,
    seat: "",
    status: "",
    created_at: "",
    start_station_id: 0,
    end_station_id: 0,
    departure_time: "",
    arrival_time: "",
    price: 0,
    pay_by_credit: false,
    pay_type: 0,
  },
});

var real_price = 0;
const real_price_ref = ref(real_price);

let train = reactive<{ data: { name?: string } }>({
  data: {},
});

const getOrderDetail = () => {
  request({
    url: `/order/${props.id}`,
    method: "GET",
  })
    .then((res) => {
      orderDetail.data = res.data.data;
      real_price_ref.value = orderDetail.data.price;
      console.log(orderDetail.data);
    })
    .catch((err) => {
      console.log(err);
      if (err.response?.data.code == 100003) {
        router.push("/login");
      }
      ElNotification({
        offset: 70,
        title: "getOrder错误",
        message: h("i", { style: "color: teal" }, err.response?.data.msg),
      });
    });
};

function getPriceByCredit() {
  console.log("??");
  request({
    url: `/order/credit/${orderDetail.data.id}`,
    method: "GET",
  })
    .then((res) => {
      real_price_ref.value = res.data.data.price;
    })
    .catch((err) => {
      console.log(err);
      ElNotification({
        offset: 80,
        title: "getPriceByCredit错误",
        message: h("i", { style: "color: teal" }, err.response?.data.msg),
      });
      return orderDetail.data.price;
    });
}

const getTrain = () => {
  console.log("getTrain");
  if (orderDetail.data) {
    request({
      url: `/train/${orderDetail.data.train_id}`,
      method: "GET",
    })
      .then((res) => {
        train.data = res.data.data;
        console.log(train);
      })
      .catch((error) => {
        ElNotification({
          offset: 70,
          title: "getTrain错误(orderDetail)",
          message: h(
            "error",
            { style: "color: teal" },
            error.response?.data.msg,
          ),
        });
        console.log(error);
      });
  }
};

async function get_real_price() {
  if (orderDetail.data.pay_by_credit) {
    getPriceByCredit();
  } else {
    real_price_ref.value = orderDetail.data.price;
  }
}

const pay = (id: number) => {
  console.log(orderDetail.data.pay_by_credit);
  let pass = orderDetail.data.pay_by_credit;

  if (pass == undefined) {
    pass = false;
  }
  request({
    url: `/order/${id}`,
    method: "PATCH",
    data: {
      status: "等待支付",
      pay_by_credit: pass,
      pay_type: orderDetail.data.pay_type,
    },
  })
    .then((res) => {
      console.log(res);
      console.log(res.data.data.status);
      if (res.data.data.status == "已支付") {
        ElNotification({
          offset: 70,
          title: "支付成功",
          message: h("success", { style: "color: teal" }, res.data.msg),
        });
        getOrderDetail();
      } else if (res.data.data.status == "等待支付") {
        const divForm = document.getElementsByTagName("div");
        if (divForm.length) {
          document.body.removeChild(divForm[0]);
          const divHere = document.createElement("div");
          divHere.innerHTML = res.data.data.pay_info;
          document.body.appendChild(divHere);
          // document.forms[0].setAttribute('target', '_blank')
          document.forms[0].submit();
        }
      }
    })
    .catch((error) => {
      if (error.response?.data.code == 100003) {
        router.push("/login");
      }
      ElNotification({
        offset: 70,
        title: "支付失败",
        message: h("error", { style: "color: teal" }, error.response?.data.msg),
      });
      console.log(error);
    });
};

const cancel = (id: number) => {
  let pass = orderDetail.data.pay_by_credit;

  if (pass == undefined) {
    pass = false;
  }
  request({
    url: `/order/${id}`,
    method: "PATCH",
    data: {
      status: "已取消",
      pay_by_credit: pass,
      pay_type: 0,
    },
  })
    .then((res) => {
      ElNotification({
        offset: 70,
        title: "取消成功",
        message: h("success", { style: "color: teal" }, res.data.msg),
      });
      getOrderDetail();
      console.log(res);
    })
    .catch((error) => {
      if (error.response?.data.code == 100003) {
        router.push("/login");
      }
      ElNotification({
        offset: 70,
        title: "取消失败",
        message: h("error", { style: "color: teal" }, error.response?.data.msg),
      });
      console.log(error);
    });
};

watch(orderDetail, () => {
  getTrain();
});

onMounted(() => {
  stations.fetch();
  getOrderDetail();
});

getOrderDetail();

onBeforeRouteLeave(() => {
  let alertWhenLeave = "订单信息已被保存至个人中心->订单信息";
  if (orderDetail.data.status == "等待支付") {
    alertWhenLeave += "，请及时前往完成支付";
  }
  ElNotification({
    offset: 70,
    title: "提示",
    message: h("hint", { style: "color: teal" }, alertWhenLeave),
  });
});
</script>

<template>
  <div style="display: flex; flex-direction: column">
    <div style="margin-bottom: 2vh">
      <el-button style="float: right" @click="getOrderDetail"> 刷新 </el-button>
    </div>

    <div style="display: flex; justify-content: space-between">
      <div>
        <el-text size="large" tag="b" type="primary">
          订单号:&nbsp;&nbsp;
        </el-text>
        <el-text size="large" tag="b">
          {{ props.id }}
        </el-text>
      </div>
      <div>
        <el-text size="large" tag="b" type="primary">
          创建日期:&nbsp;&nbsp;
        </el-text>
        <el-text size="large" tag="b" v-if="orderDetail.data">
          {{ parseDate(orderDetail.data.created_at) }}
        </el-text>
      </div>
    </div>

    <div style="display: flex; justify-content: space-between">
      <div>
        <el-text size="large" tag="b" type="primary">
          订单状态:&nbsp;&nbsp;
        </el-text>
        <el-text size="large" tag="b" v-if="orderDetail.data">
          {{ orderDetail.data.status }}
        </el-text>
      </div>
      <div>
        <el-text size="large" tag="b" type="primary">
          价格:&nbsp;&nbsp;
        </el-text>
        <el-text id="price_text" size="large" tag="b" v-if="orderDetail.data">
          {{ real_price_ref }}
        </el-text>
      </div>
    </div>

    <div style="margin-bottom: 2vh">
      <el-text size="large" tag="b" type="primary"> 车次信息: </el-text>
    </div>

    <el-descriptions :column="4" border>
      <el-descriptions-item :span="2" width="25%" align="center">
        <template #label>
          <el-text type="primary" tag="b" size="large"> 车次 </el-text>
        </template>
        <el-text type="primary" tag="b" size="large">
          {{ train?.data?.name }}
        </el-text>
      </el-descriptions-item>
      <el-descriptions-item
        label="席位信息"
        :span="2"
        width="25%"
        align="center"
        v-if="orderDetail.data"
      >
        {{ orderDetail.data.seat }}
      </el-descriptions-item>
      <el-descriptions-item
        label="出发站"
        :span="2"
        width="25%"
        align="center"
        v-if="orderDetail.data"
      >
        {{ stations.idToName[orderDetail.data.start_station_id] ?? "未知站点" }}
      </el-descriptions-item>
      <el-descriptions-item
        label="到达站"
        :span="2"
        width="25%"
        align="center"
        v-if="orderDetail.data"
      >
        {{ stations.idToName[orderDetail.data.end_station_id] ?? "未知站点" }}
      </el-descriptions-item>
      <el-descriptions-item
        label="出发时间"
        :span="2"
        width="25%"
        align="center"
        v-if="orderDetail.data"
      >
        {{ parseDate(orderDetail.data.departure_time) }}
      </el-descriptions-item>
      <el-descriptions-item
        label="到达时间"
        :span="2"
        width="25%"
        align="center"
        v-if="orderDetail.data"
      >
        {{ parseDate(orderDetail.data.arrival_time) }}
      </el-descriptions-item>
    </el-descriptions>

    <el-checkbox
      v-model="orderDetail.data.pay_by_credit"
      v-if="orderDetail.data.status === '等待支付'"
      v-on:change="get_real_price"
      >使用积分支付</el-checkbox
    >

    <pay-type-selector
      style="width: 150px"
      v-model="orderDetail.data.pay_type"
      v-if="orderDetail.data.status === '等待支付'"
    ></pay-type-selector>

    <div
      style="margin-top: 2vh"
      v-if="orderDetail.data && orderDetail.data.status === '等待支付'"
    >
      <div style="float: right">
        <el-button type="danger" @click="cancel(id ?? -1)">
          取消订单
        </el-button>
        <el-button type="primary" @click="pay(id ?? -1)"> 支付订单 </el-button>
      </div>
    </div>
    <div
      v-else-if="orderDetail.data && orderDetail.data.status === '已支付'"
      style="margin-top: 2vh"
    >
      <div style="float: right">
        <el-button type="danger" @click="cancel(id ?? -1)">
          取消订单并退钱
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
