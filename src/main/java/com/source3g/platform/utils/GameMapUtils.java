package com.source3g.platform.utils;

import com.alibaba.fastjson.JSON;
import com.source3g.platform.contants.Direct;
import com.source3g.platform.contants.MapType;
import com.source3g.platform.contants.Role;
import com.source3g.platform.dto.GameInfo;
import com.source3g.platform.dto.GameMap;
import com.source3g.platform.dto.Pos;
import com.source3g.platform.dto.PosInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huhuaiyong on 2017/9/11.
 * 地图操作工具类
 */
public class GameMapUtils {
    /**
     * 从文件初始化地图
     * @param file
     * @return
     */
    public static GameMap initFromFile(File file){
        GameMap gameMap = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String data = null;
            List<List<PosInfo>> map = new ArrayList<>();
            gameMap = new GameMap();
            gameMap.setName(file.getName());
            while ((data = br.readLine()) != null) {
                List<PosInfo> row = new ArrayList<>();
                Integer[][] rowInfo = JSON.parseObject(data, Integer[][].class);
                for (Integer[] info: rowInfo) {
                    PosInfo posInfo = new PosInfo();
                    for (Integer mapType: info) {
                        posInfo.setMapType(MapType.indexOf(mapType), true);
                    }
                    row.add(posInfo);
                }
                gameMap.setRowLen(row.size());
                gameMap.setColLen(gameMap.getColLen() + 1);
                map.add(row);
            }
            br.close();
            gameMap.setMap(map);
        }catch (IOException e){

        }
        return gameMap;
    }

    /**
     * 让指定类型的物体在指定的地图上向指定的方向移动1格
     * @param gameMap
     * @param direct
     * @param type
     * @return
     */
    public static GameMap moveByDirect(GameMap gameMap, Direct direct, MapType type) {
        PosInfo[][] posInfos = listToArray(gameMap.getMap());
        for (Pos pos: findPos(posInfos, type)) {
            try {
                switch (direct) {
                    case UP:
                        if (!posInfos[pos.getRow() - 1][pos.getCol()].hasMapType(MapType.BLOCK)) {
                            //走到上一格
                            posInfos[pos.getRow() - 1][pos.getCol()].setMapType(type, true);
                            //在当前格删除他
                            posInfos[pos.getRow()][pos.getCol()].setMapType(type, false);
                        }
                        break;
                    case DOWN:
                        if (!posInfos[pos.getRow() + 1][pos.getCol()].hasMapType(MapType.BLOCK)) {
                            posInfos[pos.getRow() + 1][pos.getCol()].setMapType(type, true);
                            posInfos[pos.getRow()][pos.getCol()].setMapType(type, false);
                        }
                        break;
                    case LEFT:
                        if (!posInfos[pos.getRow()][pos.getCol() - 1].hasMapType(MapType.BLOCK)) {
                            posInfos[pos.getRow()][pos.getCol() - 1].setMapType(type, true);
                            posInfos[pos.getRow()][pos.getCol()].setMapType(type, false);
                        }
                        break;
                    case RIGHT:
                        if (!posInfos[pos.getRow()][pos.getCol() + 1].hasMapType(MapType.BLOCK)) {
                            posInfos[pos.getRow()][pos.getCol() + 1].setMapType(type, true);
                            posInfos[pos.getRow()][pos.getCol()].setMapType(type, false);
                        }
                        break;
                    default:
                        break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                //又撞墙了，不管
            }
        }
        gameMap.setMap(arrayToList(posInfos));
        return  gameMap;
    }

    /**
     * 在移动之前，需要先关闭视野
     * @param gameInfo
     * @param role
     * @return
     */
    public static GameMap darkMapByRole(GameInfo gameInfo, Role role){
        GameMap baseMap = gameInfo.getGameMap();
        GameMap caoMap = gameInfo.getCaoMap();
        GameMap shuMap = gameInfo.getShuMap();
        if (caoMap == null) {
            caoMap = new GameMap("cao", baseMap.getColLen(), baseMap.getRowLen());
        }
        if (shuMap == null) {
            shuMap = new GameMap("shu", baseMap.getColLen(), baseMap.getRowLen());
        }
        PosInfo[][] baseMapTypes = listToArray(baseMap.getMap());
        if (role == Role.CAO){
            PosInfo[][] caoMapTypes = listToArray(caoMap.getMap());
            for (Pos pos: findPos(baseMapTypes, MapType.CAO)) {
                caoMapTypes = resetPosAround(baseMapTypes, caoMapTypes, pos, 2, role);
            }
            caoMap.setMap(arrayToList(caoMapTypes));
            return caoMap;
        }
        else{
            PosInfo[][] shuMapTypes = listToArray(shuMap.getMap());
            for (Pos pos: findPos(baseMapTypes, MapType.SHU1)) {
                shuMapTypes = resetPosAround(baseMapTypes, shuMapTypes, pos, 1, role);
            }
            for (Pos pos: findPos(baseMapTypes, MapType.SHU2)) {
                shuMapTypes = resetPosAround(baseMapTypes, shuMapTypes, pos, 1, role);
            }
            for (Pos pos: findPos(baseMapTypes, MapType.SHU3)) {
                shuMapTypes = resetPosAround(baseMapTypes, shuMapTypes, pos, 1, role);
            }for (Pos pos: findPos(baseMapTypes, MapType.SHU4)) {
                shuMapTypes = resetPosAround(baseMapTypes, shuMapTypes, pos, 1, role);
            }

            shuMap.setMap(arrayToList(shuMapTypes));
            return shuMap;
        }
    }

    /**
     * 根据角色视野，点亮地图(开视野)，进入的前提是gameinfo已经是最新信息
     * @param gameInfo
     * @param role
     * @return
     */
    public static GameMap lightMapByRole(GameInfo gameInfo, Role role) {
        GameMap baseMap = gameInfo.getGameMap();
        GameMap caoMap = gameInfo.getCaoMap();
        GameMap shuMap = gameInfo.getShuMap();
        if (caoMap == null) {
            caoMap = new GameMap("cao", baseMap.getColLen(), baseMap.getRowLen());
        }
        if (shuMap == null) {
            shuMap = new GameMap("shu", baseMap.getColLen(), baseMap.getRowLen());
        }
        PosInfo[][] baseMapTypes = listToArray(baseMap.getMap());
        if (role == Role.CAO){
            PosInfo[][] caoMapTypes = listToArray(caoMap.getMap());
            for (Pos pos: findPos(baseMapTypes, MapType.CAO)) {
                caoMapTypes = copyPosAround(baseMapTypes, caoMapTypes, pos, 2);
            }
            caoMap.setMap(arrayToList(caoMapTypes));
            return caoMap;
        }
        else{
            PosInfo[][] shuMapTypes = listToArray(shuMap.getMap());
            for (Pos pos: findPos(baseMapTypes, MapType.SHU1)) {
                shuMapTypes = copyPosAround(baseMapTypes, shuMapTypes, pos, 1);
            }
            for (Pos pos: findPos(baseMapTypes, MapType.SHU2)) {
                shuMapTypes = copyPosAround(baseMapTypes, shuMapTypes, pos, 1);
            }
            for (Pos pos: findPos(baseMapTypes, MapType.SHU3)) {
                shuMapTypes = copyPosAround(baseMapTypes, shuMapTypes, pos, 1);
            }
            for (Pos pos: findPos(baseMapTypes, MapType.SHU4)) {
                shuMapTypes = copyPosAround(baseMapTypes, shuMapTypes, pos, 1);
            }
            shuMap.setMap(arrayToList(shuMapTypes));
            return shuMap;
        }
    }

    /**
     * 根据指定的源图、当前所在的位置，重置指定视野范围的源图的信息到目标图上
     * @param source
     * @param dest
     * @param pos
     * @param aroundSize
     * @return
     */
    public static PosInfo[][] resetPosAround(PosInfo[][] source, PosInfo[][] dest, Pos pos, int aroundSize, Role role) {
        for(int i = 0; i < 1 + (aroundSize * 2); i++) {
            for(int j = 0; j < 1 + (aroundSize * 2); j++) {
                try {
                    PosInfo srcPosInfo = source[pos.getRow() - aroundSize + i][pos.getCol() - aroundSize + j];
                    PosInfo posInfo = new PosInfo();
                    posInfo.setUnknown(srcPosInfo.isUnknown());
                    posInfo.setBlock(srcPosInfo.isBlock());
                    posInfo.setRoad(srcPosInfo.isRoad());
                    if (role == Role.CAO) {
                        posInfo.setShu1(false);
                        posInfo.setShu2(false);
                        posInfo.setShu3(false);
                        posInfo.setShu4(false);
                        posInfo.setCao(srcPosInfo.isCao());
                    } else {
                        posInfo.setShu1(srcPosInfo.isShu1());
                        posInfo.setShu2(srcPosInfo.isShu2());
                        posInfo.setShu3(srcPosInfo.isShu3());
                        posInfo.setShu4(srcPosInfo.isShu4());
                        posInfo.setCao(false);
                    }
                    dest[pos.getRow() - aroundSize + i][pos.getCol() - aroundSize + j] = posInfo;
                }catch (ArrayIndexOutOfBoundsException e){
                    //撞到墙外了，不管
                }
            }
        }
        return dest;
    }

    /**
     * 根据指定的源图、当前所在的位置，将指定视野范围的源图的信息拷贝到目标图上
     * @param source
     * @param dest
     * @param pos
     * @param aroundSize
     * @return
     */
    public static PosInfo[][] copyPosAround(PosInfo[][] source, PosInfo[][] dest, Pos pos, int aroundSize){
        //1：1 + (aroundSize * 2) 神奇的算法，视野每增加一，意味着可以多看两行，同时加上本身这行
        //2：神奇的for循环，通过上面的算法加上两个for循环，刚好可以表达完所有的视野格数
        for(int i = 0; i < 1 + (aroundSize * 2); i++) {
            for(int j = 0; j < 1 + (aroundSize * 2); j++) {
                try {
                    dest[pos.getRow() - aroundSize + i][pos.getCol() - aroundSize + j] = source[pos.getRow() - aroundSize + i][pos.getCol() - aroundSize + j];
                }catch (ArrayIndexOutOfBoundsException e){
                    //撞到墙外了，不管
                }
            }
        }
        return dest;
    }

    /**
     * 在指定的地图上查找指定类型地图元素的坐标
     * @param posInfos
     * @param type
     * @return
     */
    public static List<Pos> findPos(PosInfo[][] posInfos, MapType type){
        List<Pos> poses = new ArrayList<>();
        for (int i = 0; i < posInfos.length; i++){
            for (int j = 0; j < posInfos[0].length; j++){
                if (posInfos[i][j].hasMapType(type)){
                    poses.add(new Pos(i, j));
                }
            }
        }
        return poses;
    }

    /**
     * 二维数组转二维列表
     * @param posInfos
     * @return
     */
    public static List<List<PosInfo>> arrayToList(PosInfo[][] posInfos){
        List<List<PosInfo>>  list = new ArrayList<>();//先定义一个集合对象

        for(int i=0; i<posInfos.length; i++){//遍历二维数组，对集合进行填充
            List<PosInfo> listSub=new ArrayList<>();//初始化一个ArrayList集合
            for(int j=0; j<posInfos[i].length; j++){
                listSub.add(posInfos[i][j]);//数组的列放到集合中

            }
            list.add(listSub);//二维数组放到集合中
        }
        return list;
    }

    /**
     * 二维列表转二维数组
     * @param posInfos
     * @return
     */
    public static PosInfo[][] listToArray(List<List<PosInfo>> posInfos){
        PosInfo[][] arrPosInfos = new PosInfo[posInfos.size()][posInfos.get(0).size()];
        for (int i = 0; i < posInfos.size(); i++) {
            for (int j = 0; j < posInfos.get(0).size(); j++) {
                arrPosInfos[i][j] = posInfos.get(i).get(j);
            }
        }
        return arrPosInfos;
    }

    /**
     * 判断蜀将是不是已经抓住了曹操
     * @param gameInfo
     * @return
     */
    public static boolean isCatch(GameInfo gameInfo){
        for (List<PosInfo> posInfos: gameInfo.getGameMap().getMap()) {
            for (PosInfo posInfo: posInfos) {
                if (posInfo.isCao()) {
                    if (posInfo.isShu1() || posInfo.isShu2() || posInfo.isShu3() || posInfo.isShu4()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
