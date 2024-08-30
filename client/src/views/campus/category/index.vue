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
      <el-form-item label="CatName" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="Please enter a category name"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Status" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="Data Status"
          clearable
        >
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
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
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['campus:category:add']"
          >New</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
          >Expand/Collapse</el-button
        >
      </el-col>

      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      row-key="categoryId"
      :data="categoryList"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column label="CatName" align="center" prop="categoryName" />
      <el-table-column prop="icon" label="icon" align="center" width="100">
        <template slot-scope="scope">
          <svg-icon :icon-class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column label="Abbr" align="center" prop="slug" />
      <el-table-column label="Sort" align="center" width="60" prop="orderNum" />

      <el-table-column label="SystemBuilt-in" align="center" prop="type">
        <template slot-scope="scope">
          <el-tooltip
            v-if="scope.row.type == 'Y'"
            content="System content cannot be deleted, its ID cannot be modified, and it cannot be published on the wall. It is only used for front-end display. Code logic is used to process and obtain wall content."
            placement="top"
          >
            <el-badge is-dot class="item">
               <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.type" />
            </el-badge>
          </el-tooltip>

            <dict-tag v-else :options="dict.type.sys_yes_no" :value="scope.row.type" />

        </template>
      </el-table-column>

      <el-table-column label="Description" align="center" prop="description" />
      <el-table-column label="Status" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_normal_disable"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="PostTime"
        align="center"
        prop="createTime"
        width="180"
      >
      </el-table-column>
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
            v-hasPermi="['campus:category:edit']"
            >Modify</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleAdd(scope.row)"
            v-hasPermi="['campus:category:add']"
            >New</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['campus:category:remove']"
            >Delete</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改分类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="ParentCategory" prop="parentId">
          <treeselect
            v-model="form.parentId"
            :options="categoryOptions"
            :normalizer="normalizer"
            :show-count="true"
            placeholder="Select the parent category"
          />
        </el-form-item>
        <el-form-item label="CatName" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="Please enter a category name" />
        </el-form-item>
        <el-form-item label="Menu Icon" prop="icon">
          <el-popover
            placement="bottom-start"
            width="460"
            trigger="click"
            @show="$refs['iconSelect'].reset()"
          >
            <IconSelect ref="iconSelect" @selected="selected" />
            <el-input
              slot="reference"
              v-model="form.icon"
              placeholder="Click the Select icon"
              readonly
            >
              <svg-icon
                v-if="form.icon"
                slot="prefix"
                :icon-class="form.icon"
                class="el-input__icon"
                style="height: 32px; width: 16px"
              />
              <i v-else slot="prefix" class="el-icon-search el-input__icon" />
            </el-input>
          </el-popover>
        </el-form-item>
        <el-form-item label="Abbr" prop="slug">
          <el-input v-model="form.slug" placeholder="Please enter abbr" />
        </el-form-item>
        <el-form-item label="Sort" prop="orderNum">
          <el-input-number
            v-model="form.orderNum"
            controls-position="right"
            :min="0"
          />
        </el-form-item>

        <el-form-item label="SystemBuilt-in" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio
              v-for="dict in dict.type.sys_yes_no"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>

        <el-form-item label="Status" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="Please enter a description"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">OK</el-button>
        <el-button @click="cancel">Cancel</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCategory,
  getCategory,
  delCategory,
  addCategory,
  updateCategory,
} from "@/api/campus/category";

import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import IconSelect from "@/components/IconSelect";

export default {
  name: "Cat",
  dicts: ["sys_yes_no", "sys_normal_disable"],
  components: { Treeselect, IconSelect },
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
      // 是否展开，默认全部折叠
      isExpandAll: false,
      // 重新渲染表格状态
      refreshTable: true,
      showSearch: true,
      // 分类表格数据
      categoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        categoryName: null,
        slug: null,
        description: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        categoryName: [
          { required: true, message: "Category name cannot be empty", trigger: "blur" },
        ],
        slug: [{ required: true, message: "Abbreviation name cannot be empty", trigger: "blur" }],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 选择图标
    selected(name) {
      this.form.icon = name;
    },
    /** 查询分类列表 */
    getList() {
      this.loading = true;
      listCategory(this.queryParams).then((response) => {
        this.categoryList = this.handleTree(response.data, "categoryId");
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
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 查询下拉树结构 */
    getTreeselect() {
      listCategory().then((response) => {
        this.categoryOptions = [];
        const category = {
          categoryId: 0,
          categoryName: "Main",
          children: [],
        };
        category.children = this.handleTree(response.data, "categoryId");
        this.categoryOptions.push(category);
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
        categoryId: null,
        categoryName: null,
        parentId: null,
        icon: undefined,
        slug: null,
        type: "N",
        status: "0",
        description: null,
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
      this.ids = selection.map((item) => item.categoryId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.categoryId) {
        this.form.parentId = row.categoryId;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "AddCategory";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      getCategory(row.categoryId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "ModifyCategory";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.categoryId != null) {
            updateCategory(this.form).then((response) => {
              this.$modal.msgSuccess("success");
              this.open = false;
              this.getList();
            });
          } else {
            addCategory(this.form).then((response) => {
              this.$modal.msgSuccess("success");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const categoryIds = row.categoryId || this.ids;
      this.$modal
        .confirm('Do you confirm to delete the data item with classification number"' + categoryIds + '"？')
        .then(function () {
          return delCategory(categoryIds);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("Deleted successfully");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "system/category/export",
        {
          ...this.queryParams,
        },
        `category_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
