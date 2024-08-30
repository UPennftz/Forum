<template>
  <el-form ref="genInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <span slot="label">Generate Template</span>
          <el-select v-model="info.tplCategory" @change="tplSelectChange">
            <el-option label="Single table (add, delete, modify, and query)" value="crud" />
            <el-option label="Tree table (add, delete, modify, and query)" value="tree" />
            <el-option label="Master and child tables (add, delete, modify, and query)" value="sub" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="packageName">
          <span slot="label">
            生成包路径
            <el-tooltip content="Under which Java package is it generated e.g. com.ruoyi.system" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.packageName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="moduleName">
          <span slot="label">
            生成模块名
            <el-tooltip content="Can be understood as a subsystem name, for example system" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.moduleName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="businessName">
          <span slot="label">
            生成业务名
            <el-tooltip content="It can be understood as the English name of the function, for example user" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.businessName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="functionName">
          <span slot="label">
            生成功能名
            <el-tooltip content="Used as a class description, e.g. User" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.functionName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            PrevMenu
            <el-tooltip content="Assign to a specific menu, such as System Management" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <treeselect
            :append-to-body="true"
            v-model="info.parentMenuId"
            :options="menus"
            :normalizer="normalizer"
            :show-count="true"
            placeholder="Please select the system menu"
          />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="genType">
          <span slot="label">
            生成代码方式
            <el-tooltip content="The default is to download a zip compressed package, and you can also customize the generation path" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-radio v-model="info.genType" label="0">zip archive</el-radio>
          <el-radio v-model="info.genType" label="1">Custom Path</el-radio>
        </el-form-item>
      </el-col>

      <el-col :span="24" v-if="info.genType == '1'">
        <el-form-item prop="genPath">
          <span slot="label">
            自定义路径
            <el-tooltip content="Fill in the absolute disk path. If not filled in, it will be generated under the current Web project." placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.genPath">
            <el-dropdown slot="append">
              <el-button type="primary">
                最近路径快速选择
                <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="info.genPath = '/'">Restore the default build base path</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row v-show="info.tplCategory == 'tree'">
      <h4 class="form-header">Additional Information</h4>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            树编码字段
            <el-tooltip content="The coded field name displayed in the tree, such as: dept_id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.treeCode" placeholder="Please select">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            Tree parent code field
            <el-tooltip content="The parent code field name displayed in the tree, such as: parent_Id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.treeParentCode" placeholder="Please select">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            Tree Name Field
            <el-tooltip content="The display name field name of the tree node, such as: dept_name" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.treeName" placeholder="Please select">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-show="info.tplCategory == 'sub'">
      <h4 class="form-header">Related information</h4>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            The table name of the associated child table
            <el-tooltip content="The table name of the associated subtable, such as: sys_user" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.subTableName" placeholder="Please select" @change="subSelectChange">
            <el-option
              v-for="(table, index) in tables"
              :key="index"
              :label="table.tableName + '：' + table.tableComment"
              :value="table.tableName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            Foreign key name of child table association
            <el-tooltip content="The foreign key name associated with the child table, such as: user_id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.subTableFkName" placeholder="Please select">
            <el-option
              v-for="(column, index) in subColumns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  components: { Treeselect },
  props: {
    info: {
      type: Object,
      default: null
    },
    tables: {
      type: Array,
      default: null
    },
    menus: {
      type: Array,
      default: []
    },
  },
  data() {
    return {
      subColumns: [],
      rules: {
        tplCategory: [
          { required: true, message: "Please select a template to generate", trigger: "blur" }
        ],
        packageName: [
          { required: true, message: "Please enter the build package path", trigger: "blur" }
        ],
        moduleName: [
          { required: true, message: "Please enter the name of the generated module", trigger: "blur" }
        ],
        businessName: [
          { required: true, message: "Please enter the generated business name", trigger: "blur" }
        ],
        functionName: [
          { required: true, message: "Please enter the name of the generated function", trigger: "blur" }
        ],
      }
    };
  },
  created() {},
  watch: {
    'info.subTableName': function(val) {
      this.setSubTableColumns(val);
    }
  },
  methods: {
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      };
    },
    /** 选择子表名触发 */
    subSelectChange(value) {
      this.info.subTableFkName = '';
    },
    /** 选择生成模板触发 */
    tplSelectChange(value) {
      if(value !== 'sub') {
        this.info.subTableName = '';
        this.info.subTableFkName = '';
      }
    },
    /** 设置关联外键 */
    setSubTableColumns(value) {
      for (var item in this.tables) {
        const name = this.tables[item].tableName;
        if (value === name) {
          this.subColumns = this.tables[item].columns;
          break;
        }
      }
    }
  }
};
</script>
