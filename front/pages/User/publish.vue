<template>
  <div class="publish-content">
    <el-row>
      <el-col :span="16" :xs="40">
        <el-dialog title="QuickRelease" :visible.sync="dialog" width="1000px;">
          <div>
            <div>
              <el-input
                type="textarea"
                :autosize="{ minRows: 20, maxRows: 40 }"
                placeholder="Please enter content"
                v-model="contentParam.content"
                maxlength="500"
                show-word-limit
              >
              </el-input>
              <div class="woo-box-flex woo-box-alignCenter">
                <div class="woo-box-item-flex" style="align-self: center">
                  <!-- 分类 -->
                  <div class="woo-box-item-inlineBlock">
                    <el-cascader
                      size="small"
                      placeholder="Please select a category"
                      v-model="cascader"
                      :options="categoryOptions"
                      :props="props"
                      class="c-cascader"
                    ></el-cascader>
                  </div>

                  <!-- 添加匿名操作 1-匿名 0-不匿名 -->
                  <el-switch
                      v-model="contentParam.isAnonymous"
                      active-value="1"
                      inactive-value="0"
                      active-text="Anonymous"
                      inactive-text="NoAnonymous"
                    />

                  <div class="woo-box-item-inlineBlock">
                    <el-button
                      type="text"
                      @click="selectPicture()"
                      :disabled="disabled.picture"
                    >
                      <svg-icon
                        class="woo-font woo-font--emotico"
                        style="width: 18px; height: 18px"
                        icon-class="picture"
                      />
                      <span class="iconbed-text">picture</span>
                    </el-button>
                  </div>
                  <div class="woo-box-item-inlineBlock">
                    <el-button
                      type="text"
                      @click="selectVideo()"
                      :disabled="disabled.video"
                    >
                      <svg-icon
                        class="woo-font woo-font--emotico"
                        style="width: 18px; height: 18px"
                        icon-class="video"
                      />
                      <span class="iconbed-text">video</span>
                    </el-button>
                  </div>
                </div>
                <el-button
                  type="primary"
                  round
                  style="margin-top: 15px"
                  :disabled="contentParam.content.length <= 0"
                  @click="publishContent()"
                >
                  Post
                </el-button>
              </div>
              <el-upload
                style="height: 15x"
                :headers="upload.headers"
                v-show="upload.show"
                ref="upload"
                :limit="upload.limit"
                :action="upload.action"
                :show-file-list="true"
                :list-type="upload.listType"
                :accept="upload.accept"
                :auto-upload="false"
                :on-change="handleChange"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
                :on-success="handleSuccess"
                :on-error="handleError"
              >
                <button id="uploadBtn" v-show="false"></button>
              </el-upload>
            </div>
          </div>
        </el-dialog>
      </el-col>
    </el-row>
  </div>
</template>

<script>
//引入接口定义的js文件
import operateApi from "@/api/operate";
import userInfoApi from "@/api/userInfo";
import touristApi from "@/api/tourist";
import { getToken } from "@/utils/auth";

export default {
  data() {
    return {
      //分类
      props: {
        expandTrigger: "hover",
        value: "categoryId",
        label: "categoryName",
        children: "children",
      },
      //请求参数
      contentParam: {
        categoryId: "",
        type: 0,
        isAnonymous: 0,
        content: "",
        fileList: [],
      },
      //禁用
      disabled: {
        picture: false,
        video: false,
      },
      //上传参数
      upload: {
        show: false,
        action: "",
        limit: 3,
        accept: "",
        listType: "",
        headers: {},
      },
      //选择的分类
      cascader: [],
      fileCount: 0,
      fileList: [],
      //分类列表
      categoryOptions: [],
      dialog: true,
      //1图片,2视频
      selectType: 0,
    };
  },
  created() {
    if (getToken() === undefined) {
      this.$router.push({ path: "/userlogin" });
    }
    this.upload.headers["Authorization"] = "Bearer " + getToken();
    this.haveMail();
  },
  mounted() {
    this.$nextTick(function () {
      //元素加载完成后执行的代码
      this.getTreeselect();
      //this.$refs.searchAddress.$el.click()
    });
  },

  methods: {
    //初始化数据
    haveMail() {
      userInfoApi
        .haveMail()
        .then((response) => {})
        .catch((response) => {
          var count = 3; //赋值多少秒
          var times = setInterval(() => {
            count--; //递减
            if (count <= 0) {
              clearInterval(times);
              this.$router.push({ path: "/user/profile" });
            } else {
              this.$message.warning(
                "To the bind email page in " + count + " second"
              );
            }
          }, 1000); //1000毫秒后执行
        });
    },
    //选择图片
    selectPicture() {
      //   this.$refs.uploadBtn.$el.click();
      //   this.$refs.uploadBtn.dispatchEvent(new MouseEvent('click'));
      this.upload.action = this.handleCampusUrl("/campus/imageUpload");
      this.upload.limit = 3;
      this.upload.accept = "image/*";
      this.upload.listType = "picture-card";
      this.selectType = 1;
      document.getElementById("uploadBtn").click();
    },
    selectVideo() {
      console.log("Select Video");
      //   this.$refs.uploadBtn.$el.click();
      //   this.$refs.uploadBtn.dispatchEvent(new MouseEvent('click'));
      this.upload.action = this.handleCampusUrl("/campus/videoUpload");
      this.upload.limit = 1;
      this.upload.accept = "video/*";
      this.upload.listType = "text";
      this.selectType = 2;
      document.getElementById("uploadBtn").click();
    },
    /** 查询下拉树结构 */
    getTreeselect() {
      touristApi.getCategoryList().then((response) => {
        let lists = response.data;

        for (let i = 0; i < lists.length; i++) {
          if (lists[i].type == "Y") {
            lists.splice(i, 1);
          }
        }
        this.categoryOptions = this.handleTree(lists, "categoryId");
      });
    },
    //文件移除
    handleRemove(file, fileList) {
      console.log(file, fileList);
      this.checkBtn();
    },
    //点击文件列表中已上传的文件时的钩子
    handlePreview(file) {
      console.log(file);
    },
    //上传成功
    handleSuccess(response, file, fileList) {
      if (response.code != 200) {
        fileList.splice(fileList.length - 1, 1);
        this.$message.warning(response.msg);
        setTimeout(() => {
          this.$message.error("Upload failed, please upload again");
        }, 1000);
      }
      this.checkBtn();
    },
    handleError(err, file, fileList) {
      this.$message.error("Upload failed");
      this.checkBtn();
    },
    //文件改变
    handleChange(file, fileList) {
      console.log(file);
      this.fileList = fileList;
      let fileNum = fileList.length;
      //添加文件
      if (this.fileCount < fileNum) {
        if (file.raw.type.startsWith("image")) {
          this.disabled.video = true;
        } else {
          this.disabled.picture = true;
        }
        this.validated(file);
        //上传文件
        this.$refs.upload.submit();
      }
      this.fileCount = fileNum;

      if (fileNum > 0) {
        this.upload.show = true;
      }
      this.checkBtn();
    },
    handleExceed(files, fileList) {
      this.$message.warning(`The number of uploads exceeds the limit`);
    },
    //验证文件类型
    validated(file) {
      // 若不符合类型，则让当前上传的文件去除掉即可，即从上传对列删除本次上传
      const size = file.size / 1024 / 1024;
      if (this.selectType == 1) {
        if (!file.raw.type.startsWith("image")) {
          this.$message.warning(`Please select a picture`);
          this.fileList.splice(this.fileList.length - 1, 1);
        }

        if (size > 5) {
          this.fileList.splice(this.fileList.length - 1, 1);
          this.$message.warning("The uploaded image size cannot exceed 5MB!");
        }
      } else {
        if (!file.raw.type.startsWith("video")) {
          this.$message.warning(`Please select a video`);
          this.fileList.splice(this.fileList.length - 1, 1);
        }
        if (size > 20) {
          this.fileList.splice(this.fileList.length - 1, 1);
          this.$message.warning("The uploaded video size cannot exceed 20MB!");
        }
      }
    },
    checkBtn() {
      if (this.fileList.length == 0) {
        this.upload.show = false;
        this.disabled.picture = false;
        this.disabled.video = false;
      }
    },
    //Post
    publishContent() {
      this.contentParam.fileList = [];
      console.log(this.fileList);
      for (let file of this.fileList) {
        if (file.raw.type.startsWith("image")) {
          this.contentParam.type = 1;
        }
        if (file.raw.type.startsWith("video")) {
          this.contentParam.type = 2;
        }
        if (file.response !== undefined && file.response.code == 200) {
          this.contentParam.fileList.push(file.response.data.fileId);
        } else {
          console.log("ban");
        }
      }
      if (this.cascader.length != 2) {
        this.$message.warning("Please select a category");
        return;
      } else {
        this.contentParam.categoryId = this.cascader[1];
      }
      //请求
      operateApi.publishContent(this.contentParam).then((response) => {
        this.$message.success("success");
        this.$router.push({ path: "/User/management?types=1" });
      });
    },
  },
};
</script>
<style scoped>
.iconbed-text {
  margin: 0 8px 0 2px;
  font-size: 13px;
  /* color: rgb(131, 131, 131); */
}
.c-cascader {
  border-radius: 0;
}
</style>
