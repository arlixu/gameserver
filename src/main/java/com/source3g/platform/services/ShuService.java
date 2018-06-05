package com.source3g.platform.services;

import com.source3g.platform.contants.MapType;
import com.source3g.platform.contants.Role;
import com.source3g.platform.dto.ClientRes;
import com.source3g.platform.dto.GameInfo;
import com.source3g.platform.dto.GameMap;
import com.source3g.platform.utils.GameMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by huhuaiyong on 2017/9/11.
 */
@Service
@Slf4j
public class ShuService implements BaseService {
    @Autowired
    private RestTemplate restTemplate;

    public GameMap start(GameInfo gameInfo){
        GameMap gameMap = GameMapUtils.lightMapByRole(gameInfo, Role.SHU);
        try{
            restTemplate.postForObject(gameInfo.getShuBaseUrl() + "/player/start", gameMap, String.class);
        }catch (Exception e) {
            log.error("Error:url={}, name={}, {}", gameInfo.getShuBaseUrl() + "/player/start", gameInfo.getShuName(), e);
        }
        return gameMap;
    }

    @Override
    public GameInfo move(GameInfo gameInfo) {
        GameMap gameMap = GameMapUtils.lightMapByRole(gameInfo, Role.SHU);
        try {
            ClientRes clientRes = restTemplate.postForObject(gameInfo.getShuBaseUrl() + "/player/shuMove", gameMap, ClientRes.class);
            //先关闭视野(删除看到的对方)
            gameInfo.setShuMap(GameMapUtils.darkMapByRole(gameInfo, Role.SHU));
            //然后全图移动
            GameMap baseMap = gameInfo.getGameMap();
            baseMap = GameMapUtils.moveByDirect(baseMap, clientRes.getShu1(), MapType.SHU1);
            baseMap = GameMapUtils.moveByDirect(baseMap, clientRes.getShu2(), MapType.SHU2);
            baseMap = GameMapUtils.moveByDirect(baseMap, clientRes.getShu3(), MapType.SHU3);
            baseMap = GameMapUtils.moveByDirect(baseMap, clientRes.getShu4(), MapType.SHU4);
            gameInfo.setGameMap(baseMap);
            //最后打开角色视野
            gameInfo.setShuMap(GameMapUtils.lightMapByRole(gameInfo, Role.SHU));
        }catch (Exception e) {
            log.error("Error:url={}, name={}, {}", gameInfo.getShuBaseUrl() + "/player/shuMove", gameInfo.getShuName(), e);
        }
        return gameInfo;
    }

    @Override
    public void stop(GameInfo gameInfo) {
        try{
            restTemplate.getForObject(gameInfo.getShuBaseUrl() + "/player/stop", String.class);
        }catch (Exception e) {
            log.error("Error:url={}, name={}, {}", gameInfo.getShuBaseUrl() + "/player/stop", gameInfo.getShuName(), e);
        }
    }
}
