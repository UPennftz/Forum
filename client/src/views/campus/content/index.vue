<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="User" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="Enter User Name Here"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Cat" prop="categoryId">
        <treeselect
          v-model="queryParams.categoryId"
          :options="categoryOptions"
          :normalizer="normalizer"
          :show-count="true"
          placeholder="Choose"
          style="width: 130px"
        />
      </el-form-item>
      <el-form-item label="Content" prop="content">
        <el-input
          v-model="queryParams.content"
          placeholder="Enter Content Here"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Status" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="Select status"
          clearable
        >
          <el-option
            v-for="dict in dict.type.campus_content_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Type" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="Choose type"
          clearable
        >
          <el-option
            v-for="dict in dict.type.campus_content_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Anon" prop="isAnonymous">
        <el-select
          v-model="queryParams.isAnonymous"
          placeholder="Select anon"
          clearable
        >
          <el-option
            v-for="dict in dict.type.campus_anonymous"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >Search</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >Reset</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <!-- v-hasPermi="['campus:content:add']" -->
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          >New</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <!-- v-hasPermi="['campus:content:edit']" -->
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          >Modify</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          >Delete</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="contentList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="Name" align="center" prop="params.nickName" />
      <el-table-column label="Cat" align="center" prop="params.categoryName" />
      <el-table-column
        label="Content"
        align="center"
        prop="content"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="Status" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.campus_content_status"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column label="Type" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.campus_content_type"
            :value="scope.row.type"
          />
        </template>
      </el-table-column>
      <el-table-column label="Anon" align="center" prop="isAnonymous">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.campus_anonymous"
            :value="scope.row.isAnonymous"
          />
        </template>
      </el-table-column>
      <el-table-column label="PostTime" align="center" prop="createTime" />

      <el-table-column
        label="Operate"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >Modify</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            >Delete</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            >Detail</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改校园墙内容对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="Cat" prop="categoryId">
          <!-- <el-input v-model="form.categoryId" placeholder="Cat" /> -->
          <treeselect
            v-model="form.categoryId"
            :options="categoryOptions"
            :disableBranchNodes="true"
            :normalizer="normalizer"
            :show-count="true"
            placeholder="Choose"
          />
        </el-form-item>
        <el-form-item label="Content" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="Enter Content Here"
          />
        </el-form-item>
        <el-form-item label="Status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.campus_content_status"
              :key="dict.value"
              :label="parseInt(dict.value)"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Type" prop="type">
          <el-select v-model="form.type" placeholder="Choose type">
            <el-option
              v-for="dict in dict.type.campus_content_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Anon" prop="isAnonymous">
          <el-select v-model="form.isAnonymous" placeholder="Please choose anonymous">
            <el-option
              v-for="dict in dict.type.campus_anonymous"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">OK</el-button>
        <el-button @click="cancel">Cancel</el-button>
      </div>
    </el-dialog>

    <!-- 调度校园墙内容详细 -->
    <el-dialog
      title="PostDetails"
      :visible.sync="viewOpen"
      width="700px"
      append-to-body
    >
      <el-form ref="form" :model="form" label-width="100px" size="mini">
        <el-row>
          <el-col :span="8">
            <el-form-item label="avatar："
              ><image-preview :src="contentParams.avatar"> </image-preview
            ></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="Name：">{{
              contentParams.nickName
            }}</el-form-item>
            <el-form-item label="userName：">{{
              contentParams.userName
            }}</el-form-item>
            <el-form-item label="Cat：">{{
              contentParams.categoryName
            }}</el-form-item>
          </el-col>

          <!-- <el-col :span="8">
            <el-form-item label="标签：">
              <li v-for="item in form.tags">{{ item.tagName }}</li>
            </el-form-item>
          </el-col> -->
          <el-col :span="24">
            <el-form-item label="Content：" style="white-space: pre-wrap">{{
              form.content
            }}</el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.type == 1">
            <el-form-item label="Picture：">
              <div
                class="picture_item"
                v-for="(item, key) in form.fileUrl"
                :key="key"
              >
                <image-preview
                  style="border-radius: 10px; width: 25%; aspect-ratio: 1/1"
                  :src="item"
                >
                </image-preview>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.type == 2">
            <el-form-item label="Video：">
              <div
                class="picture_item"
                v-for="(item, key) in form.fileUrl"
                :key="key"
              >
                <el-link
                  :underline="false"
                  :href="handleUrl(item)"
                  target="_blank"
                  >Click to jump</el-link
                >
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">Close</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listContent,
  getContent,
  delContent,
  addContent,
  updateContent,
} from "@/api/campus/content";

import { listSelectCategory, listCategory } from "@/api/campus/category";
import { isExternal } from "@/utils/validate";

import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import Treeselect from "@riophae/vue-treeselect";

export default {
  name: "Content",
  dicts: ["campus_content_status", "campus_content_type", "campus_anonymous"],
  components: { Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      //分类树选项
      categoryOptions: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 校园墙内容表格数据
      contentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //是否显示详细的弹出层
      viewOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        categoryId: null,
        content: null,
        status: null,
        type: null,
        isAnonymous: null,
      },
      //其他参数
      contentParams: {
        nickName: "",
        userName: "",
        avatar: "",
        categoryName: "",
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        categoryId: [
          { required: true, message: "Category cannot be empty", trigger: "blur" },
        ],
        content: [{ required: true, message: "Content cannot be empty", trigger: "blur" }],
      },
    };
  },
  created() {
    this.getList();
    this.reset();
    this.getTreeselect();
  },
  methods: {
    /** 查询校园墙内容列表 */
    getList() {
      this.loading = true;
      listContent(this.queryParams).then((response) => {
        this.contentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.categoryId,
        label: node.categoryName,
        children: node.children,
      };
    },
    /** 查询下拉树结构 */
    getTreeselect() {
      listSelectCategory().then((response) => {
        this.categoryOptions = this.handleTree(response.data, "categoryId");
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
        contentId: null,
        userId: null,
        categoryId: null,
        content: null,
        status: 0,
        type: null,
        fileNumber: null,
        isAnonymous: null,
        delFlag: null,
        createTime: null,
        createUser: null,
        updateTime: null,
        updateUser: null,
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
      this.ids = selection.map((item) => item.contentId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "Add campus wall content";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const contentId = row.contentId || this.ids;
      getContent(contentId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "Modify campus wall content";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.contentId != null) {
            updateContent(this.form).then((response) => {
              this.$modal.msgSuccess("success");
              this.open = false;
              this.getList();
            });
          } else {
            addContent(this.form).then((response) => {
              this.$modal.msgSuccess("success");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 详细按钮操作 */
    handleView(row) {
      this.viewOpen = true;
      this.form = row;
      this.contentParams = row.params;
    },
    handleUrl(url) {
      if (!url) {
        return;
      }
      let real_src = url.split(",")[0];
      if (isExternal(real_src)) {
        return real_src;
      }
      return process.env.VUE_APP_BASE_API + real_src;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const contentIds = row.contentId || this.ids;
      this.$modal
        .confirm('Are you sure to delete the data item with campus wall content number"' + contentIds + '"？')
        .then(function () {
          return delContent(contentIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("success");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "system/content/export",
        {
          ...this.queryParams,
        },
        `content_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>

<style scoped>
.picture_item {
  display: inline;
  padding-left: 0.25rem;
  padding-top: 0.25rem;
}
</style>
