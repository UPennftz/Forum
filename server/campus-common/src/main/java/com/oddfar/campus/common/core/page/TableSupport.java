package com.oddfar.campus.common.core.page;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oddfar.campus.common.core.text.Convert;
import com.oddfar.campus.common.utils.ServletUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 表格数据处理
 *
 */
public class TableSupport {
    @NotNull(message = "Page number cannot be empty")
    @Min(value = 1, message = "The minimum page number is 1")
    @JsonIgnore
    private Integer pageNum;

    @NotNull(message = "The number of entries per page cannot be empty")
    @Min(value = 1, message = "The minimum number of entries per page is 1")
    @Max(value = 100, message = "The maximum number of entries per page is 100")
    @JsonIgnore
    private Integer pageSize;

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter(PAGE_NUM), 1));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageDomain;
    }

    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }

}
