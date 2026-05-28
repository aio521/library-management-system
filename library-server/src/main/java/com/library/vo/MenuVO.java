package com.library.vo;

import lombok.Data;
import java.util.List;

@Data
public class MenuVO {
    private Long id;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Long parentId;
    private Integer sort;
    private List<MenuVO> children;
}
