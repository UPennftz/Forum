<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">

      <el-form-item label="UserID" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="Please enter your user name"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">Search</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">Reset</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column label="Time" align="center" prop="addtime"/>
      <el-table-column label="UserID" align="center" prop="username"/>
      <el-table-column label="Reply" align="center" prop="isreply">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isreply === 1" type="success">Yes</el-tag>
          <el-tag v-else type="danger">No</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Operate" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="openDialog(scope.row)"
          >Reply
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="50%"
      @close="stopTimer"
      append-to-body>
      <div style="height: 100%;">
        <el-form class="detail-form-content chat-form" ref="ruleForm" label-width="0">
          <div class="chat-content" ref="chatContent">
            <div v-bind:key="item.id" v-for="item in dataList">
              <div v-if="item.ask" class="left-content">
                <el-alert class="text-content" :title="item.ask" :closable="false" type="warning"></el-alert>
              </div>
              <div v-else class="right-content">
                <el-alert class="text-content" :title="item.reply" :closable="false" type="success"></el-alert>
              </div>
              <div class="clear-float"></div>
            </div>
          </div>
          <div class="btn-input">
            <el-input style="flex: 1;margin-right: 10px;" v-model="ruleForm.ask" placeholder="submit"
                      clearable></el-input>
            <el-button type="primary" @click="onSubmit">submit</el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listByUser, listByAdmin, reply} from "@/api/campus/chat";

export default {
  name: "Comment",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 评论表格数据
      commentList: [],
      // 弹出层标题
      title: "Service Center",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        username: ''
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},

      dialogVisible: false,
      CampusChat: null,
      id: "",
      ruleForm: {
        ask: "",
        userid: ""
      },
      dataList: [],
      timer: null
    };
  },
  created() {
    this.getList();
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
        this.listByUser();
      }, 1000);
    },
    stopTimer() {
      clearInterval(this.timer);
      this.getList()
    },
    listByUser() {
      listByUser(this.ruleForm).then((response) => {
        this.dataList = response.data
        this.scrollToBottom()
      });
    },
    // Set
    onSubmit() {
      if (!this.ruleForm.ask) {
        this.$message({
          type: "success",
          message: "Please enter content!",
        });
        return
      }
      //请求
      reply(this.ruleForm).then((response) => {
        this.$message.success("success");
      });
      this.ruleForm.ask = "";
      this.listByUser()
    },
    openDialog(row) {
      this.dialogVisible = true;
      this.ruleForm.userid = row.userid
      this.startTimer();
    },


    /** 查询评论列表 */
    getList() {
      this.loading = true;
      listByAdmin(this.queryParams).then(response => {
        this.commentList = response.data;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        commentId: null,
        parentId: null,
        userid: null,
        contentId: null,
        coContent: null,
        delFlag: null,
        createTime: null,
        createUser: null,
        updateTime: null,
        updateUser: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.commentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "AddComment";
    },
  }
};
</script>

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
