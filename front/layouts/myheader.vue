<template>
  <div class="header-container">
    <div class="wrapper">
      <!-- logo -->
      <el-row style="background-color: #faf5b9;">
        <el-col :xs="4" :sm="6">
          <div style="text-align: right; margin-top: 10px">
            <router-link :to="{ name: 'index' }">
              <svg
                t="1638268177277"
                class="icon"
                viewBox="0 0 1024 1024"
                version="1.1"
                xmlns="http://www.w3.org/2000/svg"
                p-id="4526"
                width="35"
                height="35"
              >
              </svg>
            </router-link>
          </div>
        </el-col>

        <el-col :xs="15" :sm="13">
          <div style="display: inline-block; text-align: center">
            <!--            <img height="46" src="~assets/images/bt.png" />-->
            <h1  style="color: #5e4509"><a href="/" style="color: #5e4509">SchoolForum</a></h1>
          </div>
        </el-col>

        <el-col :xs="5" :sm="5">
          <div style="font-weight: bold; text-align: left; padding-top: 10px">
            <router-link :to="{ name: 'userlogin' }">
              <div v-if="userName == '' && this.$route.path != '/userlogin'">
                <span class="sad">Login/Register</span>
              </div>
            </router-link>
            <el-dropdown
              v-if="userName != ''"
              style="margin-top: 0px; margin-left: 4px"
            >
              <span class="el-dropdown-link">
                <el-avatar
                  v-if="uImages != ''"
                  shape="circle"
                  :size="35"
                  :src="handleCampusUrl(uImages)"
                ></el-avatar>
                <i class="el-icon-caret-bottom"/>
              </span>
              <el-dropdown-menu class="user-name-wrapper" slot="dropdown">
                <span @click="profile()">
                  <el-dropdown-item
                    command="/User/edit"
                    style="font-weight: bolder"
                  >Profile👨‍</el-dropdown-item
                  ></span>
                <span @click="routingmanagement()">
                  <el-dropdown-item
                    command="/user/management"
                    style="font-weight: bolder"
                  >Manage📃</el-dropdown-item
                  ></span>
                <span @click="publish()">
                  <el-dropdown-item
                    command="/User/release"
                    style="font-weight: bolder"
                  >Post➕</el-dropdown-item
                  ></span>
                <span @click="openDialog()">
                  <el-dropdown-item
                    command="/User/edit"
                    style="font-weight: bolder"
                  >Service💁
                   </el-dropdown-item></span>
                <span @click="exit()">
                  <el-dropdown-item
                    command="/logout"
                    divided
                    style="font-weight: bolder"
                  >Log out🔙</el-dropdown-item
                  ></span>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-col>
      </el-row>
      <el-dialog
        title="Service Center"
        :visible.sync="dialogVisible"
        width="50%"
        @close="stopTimer"
        append-to-body>
        <div style="height: 100%;">
          <el-form class="detail-form-content chat-form" ref="ruleForm" label-width="0">
            <div class="chat-content" ref="chatContent">
              <div v-bind:key="item.id" v-for="item in dataList">
                <div v-if="item.reply" class="left-content">
                  <el-alert class="text-content" :title="item.reply" :closable="false" type="warning"></el-alert>
                </div>
                <div v-else class="right-content">
                  <el-alert class="text-content" :title="item.ask" :closable="false" type="success"></el-alert>
                </div>

                <div class="clear-float"></div>
              </div>
            </div>
            <div class="btn-input">
              <el-input style="flex: 1;margin-right: 10px;" v-model="ruleForm.ask" placeholder="Post"
                        clearable></el-input>
              <el-button type="primary" @click="onSubmit">Post</el-button>
            </div>
          </el-form>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import apiUserInfo from "@/api/userInfo";
import operateApi from "@/api/operate";
import {logout} from "@/api/login";
import {getToken, removeToken} from "@/utils/auth";
// import Vue from "vue";
export default {
  data() {
    return {
      nickName: "", // 用户登录显示的名称
      userName: "",
      uImages: "",
      userid: "",
      dialogVisible: false,
      CampusChat: null,

      id: "",
      ruleForm: {
        ask: "",
        userId: ""
      },
      dataList: [],
      timer: null
    };
  },
  //创建的时候自动调用
  created() {
    this.info();
  },
  mounted() {
  },
  methods: {
    scrollToBottom() {
      const chatContent = this.$refs.chatContent;
      chatContent.scrollTop = chatContent.scrollHeight;
    },
    startTimer() {
      this.timer = setInterval(() => {
        console.log("The timer executes once");
        // 在这里放置你想每秒执行的代码
        this.getDataList();
      }, 1000);
    },
    stopTimer() {
      clearInterval(this.timer);
    },
    getDataList() {
      operateApi.getChatList().then((response) => {
        this.dataList = response.data
        this.scrollToBottom()
      });
    },
    // 提交
    onSubmit() {
      if (!this.ruleForm.ask) {
        this.$message({
          type: "success",
          message: "Please enter content!",
        });
        return
      }
      //请求
      this.ruleForm.userId = this.userId
      operateApi.addChat(this.ruleForm).then((response) => {
        this.$message.success("Send successfully");
      });
      this.ruleForm.ask = "";
      this.getDataList()
    },
    openDialog() {
      this.dialogVisible = true;
      this.startTimer();
    },
    //获取用户信息
    info() {
      if (getToken() !== undefined) {
        apiUserInfo
          .getUserInfo()
          .then((response) => {
            this.userName = response.user.userName;
            this.nickName = response.user.nickName;
            this.userId = response.user.userId;
            this.uImages = response.user.avatar;
            // 设置全局用户信息
            this.globalVariable.userInfoGlobal = response.user;
          })
          .catch((response) => {
            removeToken();
          });
      }
    },
    // 退出登录
    exit() {
      logout(this.ruleForm).then((response) => {
        removeToken();
      });
      window.location.reload();
    },
    // 跳转个人中心页面
    profile() {
      this.$router.push("/user/profile");
    },
    // 跳转个人中心页面
    chatTap() {
      this.$router.push("/user/profile");
    },
    // 跳转management页面
    routingmanagement() {
      this.$router.push("/user/management");
    },
    // 跳转addContent页面
    publish() {
      this.$router.push("/user/publish");
    },
  },
};
</script>
<style>
.header-container {
  text-align: center;
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  z-index: 1;
  background-color: #fff;
}

a {
  color: #919191;
  text-decoration: none;
}

.router-link-active {
  text-decoration: none;
}
</style>
<style>
#app {
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
