package com.source3g.platform.services;

import com.source3g.platform.dto.GameInfo;
import com.source3g.platform.dto.GameMap;

/**
 * Created by huhuaiyong on 2017/9/11.
 */
public interface BaseService {
    GameMap start(GameInfo gameInfo);
    GameInfo move(GameInfo gameInfo);
    void stop(GameInfo gameInfo);
}
