package tool;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
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
    public void deletel(List<String> keys) {
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
     * @param listKey listKey名称
     * @param index   位置
     * @param value   值
     */
    public void saveToList(String listKey, long index, String value) {
        stringRedisTemplate.opsForList().set(listKey, index, value);
    }

    public long saveRightPush(String listKey, String value) {
        return stringRedisTemplate.opsForList().rightPush(listKey, value);
    }

    public List<String> getListAll(String listKey) {
        return getList(listKey, 0, -1);
    }

    public List<String> getList(String listKey, long begin, long end) {
        return stringRedisTemplate.opsForList().range(listKey, begin, end);
    }

    /**
     * 在列表中，删除第一个指定值的项
     *
     * @param listKey list名称
     * @param oldValue   旧值
     * @param newValue   新值
     * @return 修改项的序号
     */
    public long updateListByValue(String listKey, Object oldValue, Object newValue) {
        if (oldValue == null || newValue == null) {
            return -1;
        }
        long index = 0;
        boolean finder = false;
        List<String> listAll = getListAll(listKey);
        if (listAll != null && !listAll.isEmpty()) {
            while (!finder) {
                if (oldValue.equals(listAll.get((int) index))) {
                    finder = true;
                    saveToList(listKey, index, newValue.toString());
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
     * @param listKey List名称
     * @param value   值
     * @return
     */
    public long deleteListByValue(String listKey, Object value) {
        return stringRedisTemplate.opsForList().remove(listKey, 0, value);
    }

    /**
     * 清空List
     *
     * @param listKey list名称
     * @return
     */
    public long clearList(String listKey) {
        long count = 0;
        List<String> listAll = getListAll(listKey);
        if (listAll != null) {
            for (String key :
                    listAll) {
                Long remove = stringRedisTemplate.opsForList().remove(listKey, 0, key);
                count = count > remove ? count : remove;
            }
        }
        return count;
    }
}
