<template>
  <div class="app-container">
    <el-row>
      <el-col :xs="2" :sm="5" :md="5" :lg="6" :xl="6"
        ><div class="grid-content"></div
      ></el-col>
      <el-col :xs="20" :sm="15" :md="14" :lg="12" :xl="12"
        ><div class="grid-content text" style="margin-top: 20px;">
        PostManagement</div
      ></el-col>
      <el-col :xs="2" :sm="4" :md="5" :lg="6" :xl="6"
        ><div class="grid-content"></div
      ></el-col>
    </el-row>

    <el-row>
      <el-col :xs="0" :sm="2"><div class="grid-content"></div></el-col>
      <el-col :xs="24" :sm="20" style="margin: 0 auto">
        <!-- 表单查询 -->

        <!-- 复选框 -->
        <el-table
          :data="contentList"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        />
        <!-- banner列表 -->
        <el-table
          :data="contentList"
          border
          style="width: 100%; margin-top: 10px"
          @selection-change="handleSelectionChange"
        >
          <!-- 复选框 -->
          <!-- <el-table-column type="selection" width="55" /> -->

          <el-table-column type="index" width="50" label="No." />
          <!-- <el-table-column prop="cid" label="cid" /> -->

          <el-table-column
            prop="params.categoryName"
            width="100"
            label="CatName"
          />

          <el-table-column prop="content" width="500" label="Content" />

          <el-table-column label="Status" width="80">
            <template slot-scope="scope">
              {{ handleStatus(scope.row.status) }}</template
            >
          </el-table-column>

          <el-table-column label="Type" width="100%">
            <template slot-scope="scope">{{
              scope.row.type === 0
                ? "text"
                : scope.row.type === 1
                ? "image"
                : "video"
            }}</template>
          </el-table-column>

          <el-table-column label="type" width="100%">
            <template slot-scope="scope">{{
              scope.row.isAnonymous === 0 ? "NoAnonymous" : "Anonymous"
            }}</template>
          </el-table-column>

          <el-table-column prop="createTime" width="170px" label="Time" />

          <el-table-column
            fixed="right"
            label="Operate"
            width="115"
            align="center"
          >
            <template slot-scope="scope">
              <!-- <el-button
                @click="lookByCid(scope.row.content)"
                style="margin-left: 10px"
                type="text"
                size="small"
                icon="el-icon-view"
                >查看</el-button
              > -->
              <el-button
                style="color: red"
                type="text"
                size="small"
                icon="el-icon-delete"
                @click="dialogOpen(scope.row.contentId)"
                >DLT</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <el-pagination
          :current-page="current"
          :page-size="limit"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          style="padding: 30px 0; text-align: center"
          layout="sizes, prev, pager, next, jumper, ->, total, slot"
          @current-change="getList"
        />
      </el-col>
      <el-col :xs="0" :sm="2"><div class="grid-content"></div></el-col>
    </el-row>

    <!-- 弹出层 删除-->

    <el-dialog
      style="font-weight: bolder"
      title="hint"
      :visible.sync="dialogVisible"
    >
      <p style="text-align: center; font-weight: bolder">
        <i
          class="el-icon-warning"
          style="color: red; font-size: 25px"
        />&nbsp;&nbsp;Do you want to confirm to delete this content?？
      </p>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">Cancel</el-button>
        <el-button size="small" type="primary" @click="deleteByCid()"
          >Yes</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>


<script>
//引入接口定义的js文件
import operateApi from "@/api/operate";
import touristApi from "@/api/tourist";
import { getToken } from "@/utils/auth";
export default {
  //定义变量和初始值
  data() {
    return {
      dialogVisible: false,
      current: 1, //当前页
      limit: 10, //每页显示记录数
      searchObj: {}, //条件封装对象
      contentList: [], //每页数据集合
      total: 0, //总记录数
      multipleSelection: [], // 批量选择中选择的记录列表
      cid: {},
      selectAid: "",
      messagessd: "",
    };
  },
  created() {
    if (getToken() === undefined) {
      this.$router.push({ path: "/userlogin", query: { id: "1" } });
    } else {
      //在页面渲染之前执行
      //一般调用methods定义的方法，得到数据
      this.getList();
    }
  },
  mounted() {
    this.messagessd = this.$route.query.types;
    if (this.messagessd == "1") {
      this.open1();
    }
    //定义方法，进行请求接口调用
  },

  methods: {
    handleStatus(status) {
      switch (status) {
        case 0:
          return "UnderReview";
        case 1:
          return "Normal";
        case 2:
          return "Removed";
        case 3:
          return "FailedAudit";
        default:
          return "UnderReview";
      }
    },
    lookByCid(value) {
      this.$router.push({ path: "/c/" + value });
    },
    //消息提示
    open1() {
      const h = this.$createElement;
      this.$notify({
        title: "success!",
        message: h(
          "i",
          { style: "color: teal" },
          ""
        ),
      });
    },
    //删除方法
    deleteByCid() {
      //确定执行then方法
      //调用接口
      operateApi.deleteContent(this.selectAid).then((response) => {
        //hint
        // this.$message({
        //   type: "success",
        //   message: "success",
        // });
        this.$notify({
          title: "hint：",
          message: this.$createElement(
            "i",
            { style: "color: red" },
            "success"
          ),
        });
        //刷新页面
        this.getList();
        //关闭弹出层
        this.dialogVisible = false;
      });
    },
    //弹出层
    dialogOpen(id) {
      this.dialogVisible = true;
      this.selectAid = id;
    },
    //获取选择复选框的id值
    handleSelectionChange(selection) {
      this.multipleSelection = selection;
      // console.log(selection);
    },

    //用户设置列表
    getList(page = 1) {
      //添加当前页参数
      this.current = page;
      operateApi
        .ownContents(this.searchObj)
        .then((response) => {
          //请求成功response是接口返回数据
          //返回集合赋值list
          this.contentList = response.rows;
          //总记录数
          this.total = parseInt(response.total);
          // console.log(this.list);
        })
        .catch((error) => {
          //请求失败
          //console.log("失败" + error);
        });
    },
  },
};
</script>
<style>
.text2 {
  text-align: center;
}
.dfg {
  margin: 0 20px 0 20px;
  font-weight: bolder;
}
.texts {
  font-weight: bolder;
}
.text {
  font-size: 20px;
  text-align: center;
  font-weight: bolder;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>
