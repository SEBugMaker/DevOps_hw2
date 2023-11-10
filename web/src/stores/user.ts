import { defineStore } from "pinia";
import { request } from "~/utils/request";

export const useUserStore = defineStore("user", {
  state: () => {
    return {
      username: "",
      name: "",
      type: "",
      idn: "",
      phone: "",
      credit: 0,
      role: "",
    };
  },
  getters: {
    getUserName() {},
  },
  actions: {
    fetch() {
      request({
        url: "/user",
        method: "GET",
      })
        .then((res) => {
          this.username = res.data.data.username;
          this.name = res.data.data.name;
          this.type = res.data.data.type;
          this.idn = res.data.data.idn;
          this.credit = res.data.data.credit;
          this.phone = res.data.data.phone;
          this.role = res.data.data.role;
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
});
