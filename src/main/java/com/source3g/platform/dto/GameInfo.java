package com.source3g.platform.dto;

import com.source3g.platform.contants.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by huhuaiyong on 2017/9/11.
 */
@Data
@Builder
@AllArgsConstructor
public class GameInfo implements Serializable {
    private String caoName;
    private String caoBaseUrl;
    private String[] caoPart;
    private String shuName;
    private String shuBaseUrl;
    private String[] shuPart;
    private String mapName;
    private GameMap gameMap;
    private int steps = 0;
    private String winner;
    private boolean isStop = false;
    private GameMap caoMap;
    private GameMap shuMap;
    private Role winnerRole;
}
