<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
    >
      <el-form-item label="Role Name" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="Please enter a role name"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Permission Character" prop="roleKey">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="Please enter the permission characters"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Status" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="Character Status"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="PostTime">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="Start Date"
          end-placeholder="End Date"
        ></el-date-picker>
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
          v-hasPermi="['system:role:add']"
          >New</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:role:edit']"
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
          v-hasPermi="['system:role:remove']"
          >Delete</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:role:export']"
          >Export</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="roleList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="Role Number" prop="roleId" width="120" />
      <el-table-column
        label="Role Name"
        prop="roleName"
        :show-overflow-tooltip="true"
        width="150"
      />
      <el-table-column
        label="Permission Character"
        prop="roleKey"
        :show-overflow-tooltip="true"
        width="150"
      />
      <el-table-column label="Order" prop="roleSort" width="100" />
      <el-table-column label="Status" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        label="PostTime"
        align="center"
        prop="createTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="Operate"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope" v-if="scope.row.roleId != 1">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:role:edit']"
            >Modify</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:role:remove']"
            >Delete</el-button
          >
          <el-dropdown
            size="mini"
            @command="(command) => handleCommand(command, scope.row)"
            v-hasPermi="['system:role:edit']"
          >
            <span class="el-dropdown-link">
              <i class="el-icon-d-arrow-right el-icon--right"></i>More
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                command="handleAuthApi"
                icon="el-icon-circle-check"
                v-hasPermi="['system:role:edit']"
                >Assigning Interface</el-dropdown-item
              >
              <el-dropdown-item
                command="handleAuthUser"
                icon="el-icon-user"
                v-hasPermi="['system:role:edit']"
                >Assigning Users</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="Role Name" prop="roleName">
          <el-input v-model="form.roleName" placeholder="Please enter a role name" />
        </el-form-item>
        <el-form-item prop="roleKey">
          <span slot="label">
            <el-tooltip
              content="The permission characters defined in the controller, such as:@PreAuthorize(`@ss.hasRole('admin')`)"
              placement="top"
            >
              <i class="el-icon-question"></i>
            </el-tooltip>
            Permission Character
          </span>
          <el-input v-model="form.roleKey" placeholder="Please enter the permission characters" />
        </el-form-item>
        <el-form-item label="RoleOrder" prop="roleSort">
          <el-input-number
            v-model="form.roleSort"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="Status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="MenuPermissions">
          <el-checkbox
            v-model="menuExpand"
            @change="handleCheckedTreeExpand($event, 'menu')"
            >Expand/Collapse</el-checkbox
          >
          <el-checkbox
            v-model="menuNodeAll"
            @change="handleCheckedTreeNodeAll($event, 'menu')"
            >SelectAll/DeselectAll</el-checkbox
          >
          <el-checkbox
            v-model="form.menuCheckStrictly"
            @change="handleCheckedTreeConnect($event, 'menu')"
            >Father&Son</el-checkbox
          >
          <el-tree
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            ref="menu"
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="Loading, please wait"
            :props="defaultProps"
          ></el-tree>
        </el-form-item>
        <el-form-item label="Remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="Please enter content"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">OK</el-button>
        <el-button @click="cancel">Cancel</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="Assigning Interface"
      :visible.sync="openAuthApi"
      width="500px"
      append-to-body
    >
      <el-form ref="form2" label-width="100px">
        <el-form-item label="MenuPermissions">
          <el-checkbox
            v-model="resourceExpand"
            @change="handleCheckedTreeExpand($event, 'resource')"
            >Expand/Collapse</el-checkbox
          >
          <el-checkbox
            v-model="resourceNodeAll"
            @change="handleCheckedTreeNodeAll($event, 'resource')"
            >SelectAll/DeselectAll</el-checkbox
          >
          <el-checkbox
            v-model="form.resourceCheckStrictly"
            @change="handleCheckedTreeConnect($event, 'resource')"
            >Father&Son</el-checkbox
          >
          <el-tree
            class="tree-border"
            :data="resourceOptions"
            show-checkbox
            ref="resource"
            node-key="id"
            :check-strictly="!form.resourceCheckStrictly"
            empty-text="Loading, please wait"
            :props="defaultProps"
          ></el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApiResource">OK</el-button>
        <el-button @click="cancelApiResource">Cancel</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listRole,
  getRole,
  delRole,
  addRole,
  updateRole,
  dataScope,
  changeRoleStatus,
} from "@/api/system/role";
import {
  treeselect as menuTreeselect,
  roleMenuTreeselect,
} from "@/api/system/menu";
import {
  roleResourceTreeselect,
  editRoleResource,
} from "@/api/system/resource";

export default {
  name: "Role",
  dicts: ["sys_normal_disable"],
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
      // 角色表格数据
      roleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //是否显示弹出层(Assigning Interface)
      openAuthApi: false,
      menuExpand: false,
      menuNodeAll: false,
      resourceExpand: true,
      resourceNodeAll: false,
      // 日期范围
      dateRange: [],
      // 菜单列表
      menuOptions: [],
      // 资源列表
      resourceOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roleName: undefined,
        roleKey: undefined,
        status: undefined,
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label",
      },
      // 表单校验
      rules: {
        roleName: [
          { required: true, message: "Role name cannot be empty", trigger: "blur" },
        ],
        roleKey: [
          { required: true, message: "The permission character cannot be empty", trigger: "blur" },
        ],
        roleSort: [
          { required: true, message: "Role order cannot be empty", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      listRole(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          this.roleList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 查询菜单树结构 */
    getMenuTreeselect() {
      menuTreeselect().then((response) => {
        this.menuOptions = response.data;
      });
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menu.getCheckedKeys();
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    // 所有api节点数据
    getResourceAllCheckedKeys() {
      // 目前被选中的api节点 仅返回被选中的叶子节点的 keys
      let checkedKeys = this.$refs.resource.getCheckedKeys(true);

      return checkedKeys;
    },
    /** 根据角色ID查询菜单树结构 */
    getRoleMenuTreeselect(roleId) {
      return roleMenuTreeselect(roleId).then((response) => {
        this.menuOptions = response.menus;
        return response;
      });
    },
    /** 根据角色ID查询api资源树结构 */
    getRoleResourceTreeselect(roleId) {
      return roleResourceTreeselect(roleId).then((response) => {
        this.resourceOptions = response.resources;
        return response;
      });
    },
    // 角色状态修改
    handleStatusChange(row) {
      let text = row.status === "0" ? "Enable" : "Deactivate";
      this.$modal
        .confirm('Confirm"' + text + '""' + row.roleName + '"role？')
        .then(function () {
          return changeRoleStatus(row.roleId, row.status);
        })
        .then(() => {
          this.$modal.msgSuccess(text + "success");
        })
        .catch(function () {
          row.status = row.status === "0" ? "1" : "0";
        });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 取消按钮（Assigning Interface）
    cancelApiResource() {
      this.openAuthApi = false;
      this.reset();
    },
    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
      (this.menuExpand = false),
        (this.menuNodeAll = false),
        (this.resourceExpand = true),
        (this.resourceNodeAll = false),
        (this.form = {
          roleId: undefined,
          roleName: undefined,
          roleKey: undefined,
          roleSort: 0,
          status: "0",
          menuIds: [],
          resourceIds: [],
          menuCheckStrictly: true,
          resourceCheckStrictly: true,
          remark: undefined,
        });
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.roleId);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case "handleAuthApi":
          this.handleAuthApi(row);
          break;
        case "handleAuthUser":
          this.handleAuthUser(row);
          break;
        default:
          break;
      }
    },
    // 树权限（Expand/Collapse）
    handleCheckedTreeExpand(value, type) {
      if (type == "menu") {
        let treeList = this.menuOptions;
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value;
        }
      } else if (type == "resource") {
        let treeList = this.resourceOptions;
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.resource.store.nodesMap[treeList[i].id].expanded = value;
        }
      }
    },
    // 树权限（SelectAll/DeselectAll）
    handleCheckedTreeNodeAll(value, type) {
      if (type == "menu") {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : []);
      } else if (type == "resource") {
        this.$refs.resource.setCheckedNodes(value ? this.resourceOptions : []);
      }
    },
    // 树权限（Father&Son）
    handleCheckedTreeConnect(value, type) {
      if (type == "menu") {
        this.form.menuCheckStrictly = value ? true : false;
      } else if (type == "resource") {
        this.form.resourceCheckStrictly = value ? true : false;
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.getMenuTreeselect();
      this.open = true;
      this.title = "添加角色";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const roleId = row.roleId || this.ids;
      const roleMenu = this.getRoleMenuTreeselect(roleId);
      getRole(roleId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.$nextTick(() => {
          roleMenu.then((res) => {
            let checkedKeys = res.checkedKeys;
            checkedKeys.forEach((v) => {
              this.$nextTick(() => {
                this.$refs.menu.setChecked(v, true, false);
              });
            });
          });
        });
        this.title = "Modify Role";
      });
    },
    /** Assigning Interface */
    handleAuthApi(row) {
      this.reset();
      const roleId = row.roleId || this.ids;
      this.form.roleId = roleId;
      const roleResource = this.getRoleResourceTreeselect(roleId);
      this.openAuthApi = true;
      this.$nextTick(() => {
        roleResource.then((res) => {
          let checkedKeys = res.checkedKeys;
          checkedKeys.forEach((v) => {
            this.$nextTick(() => {
              this.$refs.resource.setChecked(v, true, false);
            });
          });
        });
      });
      this.title = "Assigning Interface";
    },
    /** 分配用户操作 */
    handleAuthUser: function (row) {
      const roleId = row.roleId;
      this.$router.push("/system/role-auth/user/" + roleId);
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.roleId != undefined) {
            this.form.menuIds = this.getMenuAllCheckedKeys();
            updateRole(this.form).then((response) => {
              this.$modal.msgSuccess("success");
              this.open = false;
              this.getList();
            });
          } else {
            this.form.menuIds = this.getMenuAllCheckedKeys();
            addRole(this.form).then((response) => {
              this.$modal.msgSuccess("success");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 提交按钮（Assigning Interface） */
    submitApiResource: function () {
      if (this.form.roleId != undefined) {
        this.form.resourceIds = this.getResourceAllCheckedKeys();
        let resourceIds = this.form.resourceIds.join(",");

        editRoleResource({
          roleId: this.form.roleId,
          resourceIds: resourceIds,
        }).then((response) => {
          this.$modal.msgSuccess("success");
           this.openAuthApi = false;
        });
        // console.log(this.form.resourceIds);
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const roleIds = row.roleId || this.ids;
      this.$modal
        .confirm('Do you confirm to delete the data item with role number"' + roleIds + '"?')
        .then(function () {
          return delRole(roleIds);
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
        "system/role/export",
        {
          ...this.queryParams,
        },
        `role_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
