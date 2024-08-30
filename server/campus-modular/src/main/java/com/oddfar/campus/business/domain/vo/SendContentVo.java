package com.oddfar.campus.business.domain.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 发表信息墙信息vo
 */
@Data
public class SendContentVo {

    /**
     * 分类id
     */
    @NotNull(message = "Category cannot be empty")
    private Long categoryId;

    /**
     * 内容
     */
    @Size(min = 0, max = 500, message = "Content length cannot exceed 500 characters")
    private String content;

    /**
     * 0不匿名，1匿名
     */
    @Min(value = 0,message = "Anonymous parameter error")
    @Max(value = 1,message = "Anonymous parameter error")
    @NotNull(message = "Anonymous parameters cannot be empty")
    private Integer isAnonymous;

    /**
     * 类型：0文字,1图片,2视频
     */
    @Min(value = 0,message = "Wrong type parameter")
    @Max(value = 2,message = "Wrong type parameter")
    @NotNull(message = "Type parameters cannot be null")
    private Integer type;

    /**
     * 文件id信息
     */
    private List<Long> fileList;
}
