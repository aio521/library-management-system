package com.library.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReaderQueryDTO extends PageDTO {
    private String readerNo;
    private String name;
    private String dept;
    private Integer status;
}
