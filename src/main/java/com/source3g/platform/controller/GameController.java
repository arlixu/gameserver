package com.source3g.platform.controller;

import com.source3g.platform.contants.MapType;
import com.source3g.platform.contants.Role;
import com.source3g.platform.dto.ApiResult;
import com.source3g.platform.dto.GameInfo;
import com.source3g.platform.dto.GameMap;
import com.source3g.platform.dto.PlayerConf;
import com.source3g.platform.services.CaoService;
import com.source3g.platform.services.ShuService;
import com.source3g.platform.utils.GameMapUtils;
import com.source3g.platform.utils.PlayerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huhuaiyong on 2017/9/11.
 */
@RestController
@RequestMapping("/play")
@Slf4j
public class GameController {
    @Autowired
    private CaoService caoService;

    @Autowired
    private ShuService shuService;

    @Value("${base.player.maxStep}")
    private int maxStep;

    /**
     * 启动比赛
     * @param caoName
     * @param mapName
     * @param shuName
     * @param session
     * @return
     */
    @GetMapping(path = "/start")
    public ApiResult start(String caoName, String mapName, String shuName, HttpSession session){
        PlayerConf caoConf = PlayerUtils.initFromFile(new File("players/" + caoName));
        PlayerConf shuConf = PlayerUtils.initFromFile(new File("players/" + shuName));
        GameInfo gameInfo = GameInfo.builder()
                .caoName(caoName).shuName(shuName).mapName(mapName)
                .caoBaseUrl(caoConf.getBaseUrl())
                .caoPart(caoConf.getPart())
                .shuBaseUrl(shuConf.getBaseUrl())
                .shuPart(shuConf.getPart())
                .gameMap(GameMapUtils.initFromFile(new File("maps/" + mapName))).build();
        gameInfo.setCaoMap(caoService.start(gameInfo));
        gameInfo.setShuMap(shuService.start(gameInfo));
        session.setAttribute("gameInfo", gameInfo);
        return ApiResult.success(session.getId());
    }

    /**
     * 获取移动信息
     * @param session
     * @return
     */
    @GetMapping(path = "/move")
    public ApiResult move(HttpSession session) {
        GameInfo gameInfo = (GameInfo) session.getAttribute("gameInfo");
        //仲裁是否结束比赛
        try {
            boolean isStop = false;
            if (GameMapUtils.isCatch(gameInfo)) {
                isStop = true;
                gameInfo.setWinner(gameInfo.getShuName());
                gameInfo.setWinnerRole(Role.SHU);
            } else if (gameInfo.getSteps() >= maxStep) {
                isStop = true;
                gameInfo.setWinner(gameInfo.getCaoName());
                gameInfo.setWinnerRole(Role.CAO);
            }
            if (isStop) {
                //运行结束
                gameInfo.setStop(true);
                caoService.stop(gameInfo);
                shuService.stop(gameInfo);
            } else {
                if (gameInfo.getSteps() % 2 == 0) {
                    //曹操轮次
                    gameInfo = caoService.move(gameInfo);
                } else {
                    //蜀将轮次
                    gameInfo = shuService.move(gameInfo);
                }
                GameMapUtils.findPos(GameMapUtils.listToArray(gameInfo.getGameMap().getMap()), MapType.CAO);
                gameInfo.setSteps(gameInfo.getSteps() + 1);
            }
            log.info("steps:" + gameInfo.getSteps());
            session.setAttribute("gameInfo", gameInfo);
            return ApiResult.success(gameInfo);
        }catch (NullPointerException e) {
            return ApiResult.fail("调用该接口需要先调用/play/start接口,否则服务器不知道该谁移动。");
        }
    }

    /**
     * 获取地图列表
     * @return
     */
    @GetMapping(path = "/maps")
    public ApiResult maps(){
        String dirPath = "maps";
        File fileDir=new File(dirPath);
        List<GameMap> maps = new ArrayList<>();
        for (File file: fileDir.listFiles()){
            GameMap gameMap = GameMapUtils.initFromFile(file);
            if (!ObjectUtils.isEmpty(gameMap)) {
                maps.add(GameMapUtils.initFromFile(file));
            }
        }
        return ApiResult.success(maps);
    }

    /**
     * 获取玩家列表
     * @return
     */
    @GetMapping(path = "/players")
    public ApiResult players(){
        String dirPath = "players";
        File file=new File(dirPath);
        return ApiResult.success(file.list());
    }
}
