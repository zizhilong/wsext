package com.daima.exthelp.extdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.daima.exthelp.Tools.StringHelper.trimQuotes;

public class Load {

    private final List<Extclass> extclassList;
    private final Map<String, String> xtypeMap;
    private final Map<String, Extclass> nameToExtclassMap;

    private Load() {
        extclassList = loadJsonData();
        //
        xtypeMap = createXtypeMap(extclassList);
        //通过类名获得Ext数据对象
        nameToExtclassMap = createNameToExtclassMap(extclassList);
        //定义EXT对象分级类名map
        Map<String, Object> extClassMap=buildTreeMap(extclassList);
    }


    public Map<String, String> getXtypeMap() {
        return xtypeMap;
    }
    private List<Extclass> loadJsonData() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = Load.class.getClassLoader().getResourceAsStream("data.json");
            List<Extclass> users = objectMapper.readValue(inputStream , new TypeReference<List<Extclass>>() {});
            return users;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> createXtypeMap(List<Extclass> extclassList) {
        Map<String, String> map = new HashMap<>();
        for (Extclass extclass : extclassList) {
            for (String xtype : extclass.getXtype()) {
                map.put(xtype, extclass.getName());
            }
        }
        return map;
    }

    private Map<String, Extclass> createNameToExtclassMap(List<Extclass> extclassList) {
        Map<String, Extclass> map = new HashMap<>();
        for (Extclass extclass : extclassList) {
            map.put(extclass.getName(), extclass);
        }
        return map;
    }

    public Extclass getExtclassByName(String name) {
        name=trimQuotes(name);
        String actualName = xtypeMap.get(name);
        if (actualName == null) {
            actualName = name;
        }
        if (actualName.startsWith("Ext.")) {
            return nameToExtclassMap.get(actualName);
        } else {
            return null;
        }
    }

    private static Load instance;

    public static synchronized Load getInstance() {
        if (instance == null) {
            instance = new Load();
        }
        return instance;
    }
    public static Map<String, Object> buildTreeMap(List<Extclass> extclassList) {
        Map<String, Object> treeMap = new HashMap<>();

        for (Extclass extclass : extclassList) {
            String[] parts = extclass.getName().split("\\.");
            Map<String, Object> currentMap = treeMap;

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (i == parts.length - 1) {
                    // 最后一部分，设置为叶子节点
                    currentMap.put(part, null);
                } else {
                    // 中间部分，设置为新的Map
                    currentMap = (Map<String, Object>) currentMap.computeIfAbsent(part, k -> new HashMap<>());
                }
            }
        }

        return treeMap;
    }

}