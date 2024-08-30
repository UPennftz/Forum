<template>
  <div>
    <el-form ref="form" :model="user" label-width="80px">
      <el-form-item label="Name" prop="nickName">
        <el-input v-model="user.nickName" maxlength="30" />
      </el-form-item>
      <el-form-item label="ID" prop="phonenumber">
      <el-input v-model="user.phonenumber" maxlength="11" />
    </el-form-item>
      <el-form-item label="Email" prop="email">
        <div style="display: flex">
          <el-input v-model="user.email" :disabled="false" maxlength="50" />
          <!-- <el-button type="info" @click="openDialog()">更换邮箱</el-button> -->
        </div>
      </el-form-item>
      <el-form-item label="Gender">
        <el-radio-group v-model="user.sex">
          <el-radio label="0">Male</el-radio>
          <el-radio label="1">Female</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="submit">Save</el-button>
        <el-button type="danger" size="mini" @click="close">Close</el-button>
      </el-form-item>
    </el-form>

    <el-dialog title="ChangeEmail" :visible.sync="dialogVisible" width="600px">
      <userEmail v-if="user !== undefined" :user="user" />
    </el-dialog>
  </div>
</template>

<script>
import userInfoApi from "@/api/userInfo";
import { getCodeImg, checkEmailUnique } from "@/api/login";

import userEmail from "./userEmail";

export default {
  components: { userEmail },
  props: {
    user: {
      type: Object,
    },
  },
  data() {
    const checkEmail = (rule, value, callback) => {
      //判断用户名是否存在
      checkEmailUnique(value).then((response) => {
        if (response.data !== true) {
          callback(new Error("The mailbox already exists"));
          return;
        } else {
          callback();
        }
      });
    };
    return {

      emailRules: {
        email: [
          { required: true, message: "Email address cannot be empty", trigger: "blur" },
          {
            type: "email",
            message: "Please enter a valid email address",
            trigger: ["blur", "change"],
          },
          { required: true, validator: checkEmail, trigger: "blur" },
        ],
      },
      dialogVisible: false,
      emailForm: {
        email: "",
        code: "",
        uuid: "",
      },
      codeUrl: "",
      loading: false,
    };
  },
  created() {},
  methods: {
    submit() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          userInfoApi.updateUserProfile(this.user).then((response) => {
            this.$message.success("success");   //调用Element库中的内置对象方法弹出成功的提示框
          });
        }
      });
    },
    close() {
      this.$router.push("/");   //vue-router内置方法跳转
    },
    openDialog() {
      this.dialogVisible = true;
    },
    getCode() {
      getCodeImg().then((res) => {
        this.captchaEnabled =
          res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.emailForm.uuid = res.uuid;
        }
      });
    },
    handleEmail() {},
  },
};
</script>
<style>
.email-code {
  width: 33%;
  height: 38px;
  float: right;
}
.email-code img {
  cursor: pointer;
  vertical-align: middle;
  height: 38px;
}
</style>
