<template>
  <div id="app">
    <el-form class="detail-form-content chat-form" ref="ruleForm" label-width="0">
      <div class="chat-content">
        <div v-bind:key="item.id" v-for="item in dataList">
          <div v-if="item.ask" class="left-content">
            <el-alert class="text-content" :title="item.ask" :closable="false" type="success"></el-alert>
          </div>
          <div v-else class="right-content">
            <el-alert class="text-content" :title="item.reply" :closable="false" type="warning"></el-alert>
          </div>
          <div class="clear-float"></div>
        </div>
      </div>
      <div class="btn-input">
        <el-input style="flex: 1;margin-right: 10px;" v-model="ruleForm.ask" placeholder="Post"   clearable></el-input>
        <el-button type="primary" @click="onSubmit">Post</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import touristApi from "@/api/tourist";

export default {
  name:"CampusChat",

  components: {},
  data() {
    //这里存放数据
    return {
      id: "",
      ruleForm: {},
      dataList: [],
      inter: null
    };
  },

  //生命周期 - 创建完成（可以访问当前this实例）
  created() {
    this.getSimpleHotContent();
  },
  //方法集合
  methods: {
    getSimpleHotContent() {
      touristApi
        .getSimpleHotContent()
        .then((response) => {
          this.simpleHotList = response.data;
        })
        .catch();
    },
    // 初始化
    // init(id) {
    //   this.getList();
    //   this.id = id;
    //   var that = this;
    //   var inter = setInterval(function() {
    //     that.getList();
    //   }, 10000)
    //   this.inter = inter;
    // },
    // getList() {
    //   layui.http.request('chat/list', 'get', {
    //     userid: localStorage.getItem('userid'),
    //     sort: 'addtime',
    //     order: 'asc'
    //   }, (res) => {
    //     this.dataList = res.data.list;
    //   })
    // },
    // 提交
    onSubmit() {
      if (!this.ruleForm.ask) {
        this.$message({
          type: "success",
          message: "Please enter content!",
        });
        return
      }
      // layui.http.requestJson('chat/add', 'post', {
      //   userid: localStorage.getItem('userid'),
      //   ask: this.ruleForm.ask
      // }, (res) => {
      //   this.getList();
      // });
      this.ruleForm.ask="";
    }
  },
};
</script>
<style>
#app {
  z-index: 2000 !important; /* 确保弹窗在最顶层 */
  height: 100%;
}
.chat-form {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chat-content {
  overflow-y: scroll;
  border: 1px solid #eeeeee;
  margin: 0;
  padding: 0;
  width: 100%;
  height: 400px;
  /*flex: 1;*/
}
.left-content {
  float: left;
  margin-bottom: 10px;
  padding: 10px;
}
.right-content {
  float: right;
  margin-bottom: 10px;
  padding: 10px;
}
.clear-float {
  clear: both;
}
.btn-input {
  margin-left: 0px;
  display: flex;
  width: 100%;
  padding: 10px 12px;
  box-sizing: border-box;
}
</style>
