<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="Old" prop="oldPassword">
      <el-input v-model="user.oldPassword" placeholder="Please enter your old password" type="password" show-password/>
    </el-form-item>
    <el-form-item label="New" prop="newPassword">
      <el-input v-model="user.newPassword" placeholder="Please enter new password" type="password" show-password/>
    </el-form-item>
    <el-form-item label="Confirm" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" placeholder="Please confirm your new password" type="password" show-password/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">Save</el-button>
      <el-button type="danger" size="mini" @click="close">Close</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserPwd } from "@/api/system/user";

export default {
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.user.newPassword !== value) {
        callback(new Error("The passwords you entered twice do not match"));
      } else {
        callback();
      }
    };
    return {
      user: {
        oldPassword: undefined,
        newPassword: undefined,
        confirmPassword: undefined
      },
      // 表单校验
      rules: {
        oldPassword: [
          { required: true, message: "The old password cannot be empty", trigger: "blur" }
        ],
        newPassword: [
          { required: true, message: "New password cannot be empty", trigger: "blur" },
          { min: 6, max: 20, message: "Length between 6 and 20 characters", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "Confirm password cannot be empty", trigger: "blur" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
            this.$modal.msgSuccess("Modification successful");
          });
        }
      });
    },
    close() {
      this.$tab.closePage();
    }
  }
};
</script>
