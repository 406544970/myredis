package tool;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Redis常规操作工具类
 *
 * @author lianghao
 * @date:2019/7/9
 */
public class RedisAction {
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 过期时间单位，默认：分钟
     */
    private TimeUnit timeUnit;
    /**
     * 过期时间,默认：10
     */
    private long expirationTime;

    public RedisAction() {
        super();
        stringRedisTemplate = new StringRedisTemplate();
        timeUnit = TimeUnit.MINUTES;
        expirationTime = 10;
    }

    /**
     * 保存到HASH
     *
     * @param hashName HASH表名
     * @param key      键
     * @param value    值
     */
    public void saveOrUpdateHash(String hashName, Object key, Object value) {
        stringRedisTemplate.opsForHash().put(hashName, key, value);
    }

    /**
     * 将MAP保存到Hash
     *
     * @param hashName Hash名
     * @param map      Map对象
     */
    public void saveAllMap(String hashName, Map map) {
        stringRedisTemplate.opsForHash().putAll(hashName, map);
    }

    /**
     * 删除指定键
     *
     * @param hashName Hash名
     * @param keyList  键列表
     * @return 删除条数
     */
    public long deleteHashByKeyList(String hashName, List<Object> keyList) {
        if (hashName == null)
            return 0;
        long count = 0;
        if (keyList != null && !keyList.isEmpty()) {
            for (Object key :
                    keyList) {
                count += stringRedisTemplate.opsForHash().delete(hashName, key);
            }
        }
        return count;
    }

    /**
     * 得到Hash键列表
     *
     * @param hashName Hash名
     * @param tClass   泛型类名
     * @param <T>      值类名
     * @return 值集合
     */
    public <T> Set<T> getHashAllKeySet(String hashName, Class<T> tClass) {
        if (hashName == null)
            return null;
        return (Set<T>) stringRedisTemplate.opsForHash().keys(hashName);
    }

    /**
     * 根据指定键，在Hash中得到值对象
     *
     * @param hashName Hash名
     * @param tClass   泛型类名
     * @param <T>值类名
     * @return 值对象
     */
    public <T> T getHashValue(String hashName, Object key, Class<T> tClass) {
        if (hashName == null)
            return null;
        return (T) stringRedisTemplate.opsForHash().get(hashName, key);
    }

    /**
     * 根据键列表，在Hash中得到值对象列表
     *
     * @param hashName Hash名
     * @param keyList  键List
     * @param tClass   泛型类名
     * @param <T>值类名
     * @return 值对象集合
     */
    public <T> List<T> getHashMultValue(String hashName, List<Object> keyList, Class<T> tClass) {
        if (hashName == null)
            return null;
        if (keyList == null || keyList.isEmpty()) {
            return null;
        }
        BoundHashOperations<String, Object, Object> opsForHash = stringRedisTemplate.boundHashOps(hashName);
        List<T> vs = new ArrayList<>();
        for (Object key :
                keyList) {
            vs.add((T) opsForHash.get(key));
        }
        return vs;
    }

    /**
     * 保存，无过期时间
     *
     * @param key   键
     * @param value 值
     */
    public void saveKeyAndValue(String key, String value) {
        saveValue(key, value);
    }

    /**
     * 保存，并指定过期时间
     *
     * @param key            键
     * @param value          值
     * @param expirationTime 过期时间
     * @param timeUnit       过期时间单位
     */
    public void saveKeyAndValue(String key, String value, long expirationTime, TimeUnit timeUnit) {
        saveValueAndTime(key, value, expirationTime, timeUnit);
    }

    /**
     * 保存，默认有过期时间
     *
     * @param key   键
     * @param value 值
     */
    public void saveValueAndTime(String key, String value) {
        saveValueAndTime(key, value, this.expirationTime, this.timeUnit);
    }

    private void saveValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    private void saveValueAndTime(String key, String value, long expirationTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);
    }

    /**
     * 得到指定键值
     *
     * @param key 键
     * @return 返回值
     */
    public String getValue(Object key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 根据键，删除
     *
     * @param key 键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 根据键列表，删除
     *
     * @param keys 键列表
     */
    public void delete(List<String> keys) {
        if (keys != null && keys.size() > 0)
            for (String row : keys
                    ) {
                this.delete(row);
            }
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 将值保存到list中
     *
     * @param listName listName名称
     * @param index   位置
     * @param value   值
     */
    public void saveToList(String listName, long index, String value) {
        stringRedisTemplate.opsForList().set(listName, index, value);
    }

    public long saveRightPush(String listName, String value) {
        return stringRedisTemplate.opsForList().rightPush(listName, value);
    }

    public List<String> getListAll(String listName) {
        return getList(listName, 0, -1);
    }

    public List<String> getList(String listName, long begin, long end) {
        return stringRedisTemplate.opsForList().range(listName, begin, end);
    }

    /**
     * 在列表中，删除第一个指定值的项
     *
     * @param listName  list名称
     * @param oldValue 旧值
     * @param newValue 新值
     * @return 修改项的序号
     */
    public long updateListByValue(String listName, Object oldValue, Object newValue) {
        if (oldValue == null || newValue == null) {
            return -1;
        }
        long index = 0;
        boolean finder = false;
        List<String> listAll = getListAll(listName);
        if (listAll != null && !listAll.isEmpty()) {
            while (!finder) {
                if (oldValue.equals(listAll.get((int) index))) {
                    finder = true;
                    saveToList(listName, index, newValue.toString());
                    break;
                }
                index++;
            }
        }
        return finder ? index : -1;
    }

    /**
     * 根据值，删除List
     *
     * @param listName List名称
     * @param value   值
     * @return
     */
    public long deleteListByValue(String listName, Object value) {
        return stringRedisTemplate.opsForList().remove(listName, 0, value);
    }

    /**
     * 清空List
     *
     * @param listName list名称
     * @return
     */
    public long clearList(String listName) {
        long count = 0;
        List<String> listAll = getListAll(listName);
        if (listAll != null) {
            for (String key :
                    listAll) {
                Long remove = stringRedisTemplate.opsForList().remove(listName, 0, key);
                count = count > remove ? count : remove;
            }
        }
        return count;
    }
}
