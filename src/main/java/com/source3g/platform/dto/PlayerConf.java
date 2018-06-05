package com.source3g.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by huhuaiyong on 2017/9/13.
 */
@Data
@Builder
public class PlayerConf {
    private String baseUrl;
    private String[] part;
}
