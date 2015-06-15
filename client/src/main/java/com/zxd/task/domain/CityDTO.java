package com.zxd.task.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zxd on 2015/6/16.
 * dubbo类型接口传递DTO
 */
@Data
public class CityDTO implements Serializable{
    private Integer id;

    private String name;

    private String countrycode;

    private String district;

    private Integer population;
}
