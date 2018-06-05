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

import java.net.SocketTimeoutException;

/**
 * Created by huhuaiyong on 2017/9/11.
 */
@Service
@Slf4j
public class CaoService implements BaseService{
    @Autowired
    private RestTemplate restTemplate;

    public GameMap start(GameInfo gameInfo){
        GameMap gameMap = GameMapUtils.lightMapByRole(gameInfo, Role.CAO);
        try {
            restTemplate.postForObject(gameInfo.getCaoBaseUrl() + "/player/start", gameMap, String.class);
        }catch (Exception e) {
            log.error("Error:url={}, name={}, {}", gameInfo.getCaoBaseUrl() + "/player/start", gameInfo.getCaoName(), e);
        }
        return gameMap;
    }

    @Override
    public GameInfo move(GameInfo gameInfo) {
        GameMap gameMap = GameMapUtils.lightMapByRole(gameInfo, Role.CAO);
        try {
            ClientRes clientRes = restTemplate.postForObject(gameInfo.getCaoBaseUrl() + "/player/caoMove", gameMap, ClientRes.class);
            //先关闭视野(删除看到的对方)
            gameInfo.setCaoMap(GameMapUtils.darkMapByRole(gameInfo, Role.CAO));
            //然后全图移动
            gameInfo.setGameMap(GameMapUtils.moveByDirect(gameInfo.getGameMap(), clientRes.getCao(), MapType.CAO));
            //最后打开角色视野
            gameInfo.setCaoMap(GameMapUtils.lightMapByRole(gameInfo, Role.CAO));
        }catch (Exception e) {
            log.error("Error:url={}, name={}, {}", gameInfo.getCaoBaseUrl() + "/player/caoMove", gameInfo.getCaoName(), e);
        }
        return gameInfo;
    }

    @Override
    public void stop(GameInfo gameInfo) {
        try {
            restTemplate.getForObject(gameInfo.getCaoBaseUrl() + "/player/stop", String.class);
        }catch (Exception e) {
            log.error("Error:url={}, name={}, {}", gameInfo.getCaoBaseUrl() + "/player/stop", gameInfo.getCaoName(), e);
        }
    }
}
