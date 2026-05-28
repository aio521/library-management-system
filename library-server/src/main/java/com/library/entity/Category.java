package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "分类编码不能为空")
    private String code;
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private Long parentId;

    @TableField(exist = false)
    private List<Category> children;
}
