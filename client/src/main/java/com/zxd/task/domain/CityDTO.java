package com.zxd.task.domain;

import lombok.Data;

/**
 * Created by zxd on 2015/6/16.
 */
@Data
public class CityDTO {
    private Integer id;

    private String name;

    private String countrycode;

    private String district;

    private Integer population;
}
