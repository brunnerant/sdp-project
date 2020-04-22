package ch.epfl.qedit.model;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StringPool {

    public final static  String TITLE_ID = "title";
    private Map<String, String> stringPool;

    public StringPool(){
        stringPool = new HashMap<>();
    }

    public StringPool(Map<String, String> stringPool){
        this.stringPool = new HashMap<>(stringPool);
    }

    private String newUID(){
        String id = UUID.randomUUID().toString();
        while(stringPool.containsKey(id)){
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public String put(String text){
        String id = newUID();
        stringPool.put(id, text);
        return id;
    }


    public String put(String key, String text){
        return stringPool.put(key, text);
    }

    public String get(String key){
        return stringPool.get(key);
    }

    public ImmutableMap<String, String> get(){
        return ImmutableMap.copyOf(stringPool);
    }
}