package com.library.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageVO<T> {
    private List<T> records;
    private Long total;
    private Integer page;
    private Integer pageSize;
}
