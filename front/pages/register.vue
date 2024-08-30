<template>
  <div class="register">
    <el-form
      ref="registerForm"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
    >
      <h3 class="title">SchoolForum</h3>
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          type="text"
          auto-complete="off"
          placeholder="account"
        >
          <svg-icon
            slot="prefix"
            icon-class="user"
            class="el-input__icon input-icon"
          />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          auto-complete="off"
          placeholder="password"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon
            slot="prefix"
            icon-class="password"
            class="el-input__icon input-icon"
          />
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          auto-complete="off"
          placeholder="Confirm Password"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon
            slot="prefix"
            icon-class="password"
            class="el-input__icon input-icon"
          />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="Verification Code"
          style="width: 63%"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon
            slot="prefix"
            icon-class="validCode"
            class="el-input__icon input-icon"
          />
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img" />
        </div>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width: 100%"
          @click.native.prevent="handleRegister"
        >
          <span v-if="!loading">register</span>
          <span v-else>Registering...</span>
        </el-button>
        <div style="float: right">
          <router-link class="link-type" :to="'/userlogin'"
            >Log in with an existing account</router-link
          >
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>Copyright 2024  </span>
    </div>
  </div>
</template>
<script>
import { getCodeImg, register, checkUserNameUnique } from "@/api/login";

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("The passwords you entered twice do not match"));
      } else {
        callback();
      }
    };
    const checkUserName = (rule, value, callback) => {
      //判断用户名是否存在

      checkUserNameUnique(value).then((response) => {
        if (response.data !== true) {
          callback(new Error("Username already exists"));
          return;
        } else {
          callback();
        }
      });
    };

    return {
      codeUrl: "",
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: "",
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "Please enter your account number" },
          {
            min: 2,
            max: 20,
            message: "User account length must be between 2 and 20",
            trigger: "blur",
          },
          { required: true, validator: checkUserName, trigger: "blur" },
        ],
        password: [
          { required: true, trigger: "blur", message: "Please enter your account number" },
          {
            min: 5,
            max: 20,
            message: "User password length must be between 5 and 20",
            trigger: "blur",
          },
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "Please enter your password again" },

          { required: true, validator: equalToPassword, trigger: "blur" },
        ],
        code: [{ required: true, trigger: "change", message: "Please enter the verification code" }],
      },
      loading: false,
      captchaEnabled: true,
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      getCodeImg().then((res) => {
        this.captchaEnabled =
          res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.registerForm.uuid = res.uuid;
        }
      });
    },
    handleRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          register(this.registerForm)
            .then((res) => {
              const username = this.registerForm.username;
              this.$alert(
                "<font color='red'>Congratulations, your account " +
                  username +
                  " has been successfully registered!</font>",
                "System prompts",
                {
                  dangerouslyUseHTMLString: true,
                  type: "success",
                }
              )
                .then(() => {
                  this.$router.push("/userlogin");
                })
                .catch(() => {});
            })
            .catch(() => {
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            });
        }
      });
    },
  },
};
</script>

<style>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
}
.el-input {
  height: 38px;
}
.el-input input {
  height: 38px;
}

.register-code {
  width: 33%;
  height: 38px;
  float: right;
}
.register-code img {
  cursor: pointer;
  vertical-align: middle;
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 38px;
}
</style>
