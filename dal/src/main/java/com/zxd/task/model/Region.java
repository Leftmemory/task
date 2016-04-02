package com.zxd.task.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Region implements Serializable{
    private Integer id;

    private String regionName;

    private String code;

    private Byte level;

    private Integer parentId;

    private String comment;

    private Integer maxNum;

    private Integer regionId;

    private Date gmtModifed;

    private String codeNums;
}