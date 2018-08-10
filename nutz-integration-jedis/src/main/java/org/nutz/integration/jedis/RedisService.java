package org.nutz.integration.jedis;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

import redis.clients.jedis.*;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.JedisCluster.Reset;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Pool;
import redis.clients.util.Slowlog;

/**
 *
 * @author wendal
 */
@SuppressWarnings("deprecation")
public class RedisService extends Jedis {
    
    protected JedisAgent jedisAgent;
    
    protected Jedis getJedis() {
        Jedis jedis = RedisInterceptor.jedis();
        if (jedis == null)
            jedis = jedisAgent.getResource();
        return jedis;
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1GB).
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Status code reply
     */
    public String set(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
     * GB).
     *
     * @param key
     * @param value
     * @param nxxx  NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     *              if it already exist.
     * @param expx  EX|PX, expire time units: EX = seconds; PX = milliseconds
     * @param time  expire time in the units of <code>expx</code>
     * @return Status code reply
     */
    public String set(String key, String value, String nxxx, String expx, long time) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value, nxxx, expx, time);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Get the value of the specified key. If the key does not exist null is returned. If the value
     * stored at key is not a string an error is returned because GET can only handle string values.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public String get(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Test if the specified key exists. The command returns the number of keys existed Time
     * complexity: O(N)
     *
     * @param keys
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed
     * 0 if none of the specified key existed
     */
    public Long exists(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public String ping() {
        Jedis jedis = getJedis();
        try {
            return jedis.ping();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1GB).
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Status code reply
     */
    public String set(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see java.util.HashMap
     */
    public boolean equals(Object obj) {
        Jedis jedis = getJedis();
        try {
            return jedis.equals(obj);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Test if the specified key exists. The command returns "1" if the key exists, otherwise "0" is
     * returned. Note that even keys set with an empty string as value will return "1". Time
     * complexity: O(1)
     *
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public Boolean exists(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1GB).
     *
     * @param key
     * @param value
     * @param nxxx  NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     *              if it already exist.
     * @param expx  EX|PX, expire time units: EX = seconds; PX = milliseconds
     * @param time  expire time in the units of <code>expx</code>
     * @return Status code reply
     */
    public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value, nxxx, expx, time);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified keys. If a given key does not exist no operation is performed for this
     * key. The command returns the number of keys removed. Time complexity: O(1)
     *
     * @param keys
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed
     * 0 if none of the specified key existed
     */
    public Long del(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Long del(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Get the value of the specified key. If the key does not exist the special value 'nil' is
     * returned. If the value stored at key is not a string an error is returned because GET can only
     * handle string values.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the type of the value stored at key in form of a string. The type can be one of "none",
     * "string", "list", "set". "none" is returned if the key does not exist. Time complexity: O(1)
     *
     * @param key
     * @return Status code reply, specifically: "none" if the key does not exist "string" if the key
     * contains a String value "list" if the key contains a List value "set" if the key
     * contains a Set value "zset" if the key contains a Sorted Set value "hash" if the key
     * contains a Hash value
     */
    public String type(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.type(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Ask the server to silently close the connection.
     */
    public String quit() {
        Jedis jedis = getJedis();
        try {
            return jedis.quit();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Test if the specified keys exist. The command returns the number of keys existed Time
     * complexity: O(N)
     *
     * @param keys
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys existed 0 if
     * none of the specified keys existed
     */
    public Long exists(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Returns all the keys matching the glob-style pattern as space separated strings. For example if
     * you have in the database the keys "foo" and "foobar" the command "KEYS foo*" will return
     * "foo foobar".
     * <p>
     * Note that while the time complexity for this operation is O(n) the constant times are pretty
     * low. For example Redis running on an entry level laptop can scan a 1 million keys database in
     * 40 milliseconds. <b>Still it's better to consider this one of the slow commands that may ruin
     * the DB performance if not used with care.</b>
     * <p>
     * In other words this command is intended only for debugging and special operations like creating
     * a script to change the DB schema. Don't use it in your normal code. Use Redis Sets in order to
     * group together a subset of objects.
     * <p>
     * Glob style patterns examples:
     * <ul>
     * <li>h?llo will match hello hallo hhllo
     * <li>h*llo will match hllo heeeello
     * <li>h[ae]llo will match hello and hallo, but not hillo
     * </ul>
     * <p>
     * Use \ to escape special chars if you want to match them verbatim.
     * <p>
     * Time complexity: O(n) (with n being the number of keys in the DB, and assuming keys and pattern
     * of limited length)
     *
     * @param pattern
     * @return Multi bulk reply
     */
    public Set<String> keys(String pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.keys(pattern);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Test if the specified key exists. The command returns "1" if the key exists, otherwise "0" is
     * returned. Note that even keys set with an empty string as value will return "1". Time
     * complexity: O(1)
     *
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public Boolean exists(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified keys. If a given key does not exist no operation is performed for this
     * key. The command returns the number of keys removed. Time complexity: O(1)
     *
     * @param keys
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed
     * 0 if none of the specified key existed
     */
    public Long del(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Long del(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the type of the value stored at key in form of a string. The type can be one of "none",
     * "string", "list", "set". "none" is returned if the key does not exist. Time complexity: O(1)
     *
     * @param key
     * @return Status code reply, specifically: "none" if the key does not exist "string" if the key
     * contains a String value "list" if the key contains a List value "set" if the key
     * contains a Set value "zset" if the key contains a Sorted Set value "hash" if the key
     * contains a Hash value
     */
    public String type(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.type(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return a randomly selected key from the currently selected DB.
     * <p>
     * Time complexity: O(1)
     *
     * @return Singe line reply, specifically the randomly selected key or an empty string is the
     * database is empty
     */
    public String randomKey() {
        Jedis jedis = getJedis();
        try {
            return jedis.randomKey();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically renames the key oldkey to newkey. If the source and destination name are the same an
     * error is returned. If newkey already exists it is overwritten.
     * <p>
     * Time complexity: O(1)
     *
     * @param oldkey
     * @param newkey
     * @return Status code repy
     */
    public String rename(String oldkey, String newkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.rename(oldkey, newkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Delete all the keys of the currently selected DB. This command never fails.
     *
     * @return Status code reply
     */
    public String flushDB() {
        Jedis jedis = getJedis();
        try {
            return jedis.flushDB();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Rename oldkey into newkey but fails if the destination key newkey already exists.
     * <p>
     * Time complexity: O(1)
     *
     * @param oldkey
     * @param newkey
     * @return Integer reply, specifically: 1 if the key was renamed 0 if the target key already exist
     */
    public Long renamenx(String oldkey, String newkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.renamenx(oldkey, newkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Returns all the keys matching the glob-style pattern as space separated strings. For example if
     * you have in the database the keys "foo" and "foobar" the command "KEYS foo*" will return
     * "foo foobar".
     * <p>
     * Note that while the time complexity for this operation is O(n) the constant times are pretty
     * low. For example Redis running on an entry level laptop can scan a 1 million keys database in
     * 40 milliseconds. <b>Still it's better to consider this one of the slow commands that may ruin
     * the DB performance if not used with care.</b>
     * <p>
     * In other words this command is intended only for debugging and special operations like creating
     * a script to change the DB schema. Don't use it in your normal code. Use Redis Sets in order to
     * group together a subset of objects.
     * <p>
     * Glob style patterns examples:
     * <ul>
     * <li>h?llo will match hello hallo hhllo
     * <li>h*llo will match hllo heeeello
     * <li>h[ae]llo will match hello and hallo, but not hillo
     * </ul>
     * <p>
     * Use \ to escape special chars if you want to match them verbatim.
     * <p>
     * Time complexity: O(n) (with n being the number of keys in the DB, and assuming keys and pattern of limited length)
     *
     * @param pattern
     * @return Multi bulk reply
     */
    public Set<byte[]> keys(byte[] pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.keys(pattern);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set a timeout on the specified key. After the timeout the key will be automatically deleted by
     * the server. A key with an associated timeout is said to be volatile in Redis terminology.
     * <p>
     * Voltile keys are stored on disk like the other keys, the timeout is persistent too like all the
     * other aspects of the dataset. Saving a dataset containing expires and stopping the server does
     * not stop the flow of time as Redis stores on disk the time when the key will no longer be
     * available as Unix time, and not the remaining seconds.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire
     * set. It is also possible to undo the expire at all turning the key into a normal key using the
     * {@link #persist(String) PERSIST} command.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param seconds
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     * the key already has an associated timeout (this may happen only in Redis versions &lt;
     * 2.1.3, Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     */
    public Long expire(String key, int seconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.expire(key, seconds);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return a randomly selected key from the currently selected DB.
     * <p>
     * Time complexity: O(1)
     *
     * @return Singe line reply, specifically the randomly selected key or an empty string is the
     * database is empty
     */
    public byte[] randomBinaryKey() {
        Jedis jedis = getJedis();
        try {
            return jedis.randomBinaryKey();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically renames the key oldkey to newkey. If the source and destination name are the same an
     * error is returned. If newkey already exists it is overwritten.
     * <p>
     * Time complexity: O(1)
     *
     * @param oldkey
     * @param newkey
     * @return Status code repy
     */
    public String rename(byte[] oldkey, byte[] newkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.rename(oldkey, newkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * EXPIREAT works exctly like {@link #expire(String, int) EXPIRE} but instead to get the number of
     * seconds representing the Time To Live of the key as a second argument (that is a relative way
     * of specifing the TTL), it takes an absolute one in the form of a UNIX timestamp (Number of
     * seconds elapsed since 1 Gen 1970).
     * <p>
     * EXPIREAT was introduced in order to implement the Append Only File persistence mode so that
     * EXPIRE commands are automatically translated into EXPIREAT commands for the append only file.
     * Of course EXPIREAT can also used by programmers that need a way to simply specify that a given
     * key should expire at a given time in the future.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire
     * set. It is also possible to undo the expire at all turning the key into a normal key using the
     * {@link #persist(String) PERSIST} command.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param unixTime
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     * the key already has an associated timeout (this may happen only in Redis versions &lt;
     * 2.1.3, Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     */
    public Long expireAt(String key, long unixTime) {
        Jedis jedis = getJedis();
        try {
            return jedis.expireAt(key, unixTime);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Rename oldkey into newkey but fails if the destination key newkey already exists.
     * <p>
     * Time complexity: O(1)
     *
     * @param oldkey
     * @param newkey
     * @return Integer reply, specifically: 1 if the key was renamed 0 if the target key already exist
     */
    public Long renamenx(byte[] oldkey, byte[] newkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.renamenx(oldkey, newkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the number of keys in the currently selected database.
     *
     * @return Integer reply
     */
    public Long dbSize() {
        Jedis jedis = getJedis();
        try {
            return jedis.dbSize();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set a timeout on the specified key. After the timeout the key will be automatically deleted by
     * the server. A key with an associated timeout is said to be volatile in Redis terminology.
     * <p>
     * Voltile keys are stored on disk like the other keys, the timeout is persistent too like all the
     * other aspects of the dataset. Saving a dataset containing expires and stopping the server does
     * not stop the flow of time as Redis stores on disk the time when the key will no longer be
     * available as Unix time, and not the remaining seconds.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire
     * set. It is also possible to undo the expire at all turning the key into a normal key using the
     * {@link #persist(byte[]) PERSIST} command.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param seconds
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     * the key already has an associated timeout (this may happen only in Redis versions &lt;
     * 2.1.3, Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     */
    public Long expire(byte[] key, int seconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.expire(key, seconds);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * The TTL command returns the remaining time to live in seconds of a key that has an
     * {@link #expire(String, int) EXPIRE} set. This introspection capability allows a Redis client to
     * check how many seconds a given key will continue to be part of the dataset.
     *
     * @param key
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an
     * EXPIRE. In Redis 2.6 or older, if the Key does not exists or does not have an
     * associated expire, -1 is returned. In Redis 2.8 or newer, if the Key does not have an
     * associated expire, -1 is returned or if the Key does not exists, -2 is returned.
     */
    public Long ttl(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Move the specified key from the currently selected DB to the specified destination DB. Note
     * that this command returns 1 only if the key was successfully moved, and 0 if the target key was
     * already there or if the source key was not found at all, so it is possible to use MOVE as a
     * locking primitive.
     *
     * @param key
     * @param dbIndex
     * @return Integer reply, specifically: 1 if the key was moved 0 if the key was not moved because
     * already present on the target DB or was not found in the current DB.
     */
    public Long move(String key, int dbIndex) {
        Jedis jedis = getJedis();
        try {
            return jedis.move(key, dbIndex);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * GETSET is an atomic set this value and return the old value command. Set key to the string
     * value and return the old value stored at key. The string can't be longer than 1073741824 bytes (1 GB).
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Bulk reply
     */
    public String getSet(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.getSet(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Get the values of all the specified keys. If one or more keys dont exist or is not of type
     * String, a 'nil' value is returned instead of the value of the specified key, but the operation
     * never fails.
     * <p>
     * Time complexity: O(1) for every key
     *
     * @param keys
     * @return Multi bulk reply
     */
    public List<String> mget(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.mget(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * SETNX works exactly like {@link #set(String, String) SET} with the only difference that if the
     * key already exists no operation is performed. SETNX actually means "SET if Not eXists".
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Integer reply, specifically: 1 if the key was set 0 if the key was not set
     */
    public Long setnx(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setnx(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * EXPIREAT works exctly like {@link #expire(byte[], int) EXPIRE} but instead to get the number of
     * seconds representing the Time To Live of the key as a second argument (that is a relative way
     * of specifing the TTL), it takes an absolute one in the form of a UNIX timestamp (Number of
     * seconds elapsed since 1 Gen 1970).
     * <p>
     * EXPIREAT was introduced in order to implement the Append Only File persistence mode so that
     * EXPIRE commands are automatically translated into EXPIREAT commands for the append only file.
     * Of course EXPIREAT can also used by programmers that need a way to simply specify that a given
     * key should expire at a given time in the future.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire
     * set. It is also possible to undo the expire at all turning the key into a normal key using the
     * {@link #persist(byte[]) PERSIST} command.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param unixTime
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     * the key already has an associated timeout (this may happen only in Redis versions &lt;
     * 2.1.3, Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     */
    public Long expireAt(byte[] key, long unixTime) {
        Jedis jedis = getJedis();
        try {
            return jedis.expireAt(key, unixTime);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * The command is exactly equivalent to the following group of commands:
     * {@link #set(String, String) SET} + {@link #expire(String, int) EXPIRE}. The operation is
     * atomic.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param seconds
     * @param value
     * @return Status code reply
     */
    public String setex(String key, int seconds, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setex(key, seconds, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the the respective keys to the respective values. MSET will replace old values with new
     * values, while {@link #msetnx(String...) MSETNX} will not perform any operation at all even if
     * just a single key already exists.
     * <p>
     * Because of this semantic MSETNX can be used in order to set different keys representing
     * different fields of an unique logic object in a way that ensures that either all the fields or
     * none at all are set.
     * <p>
     * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B
     * are modified, another client talking to Redis can either see the changes to both A and B at
     * once, or no modification at all.
     *
     * @param keysvalues
     * @return Status code reply Basically +OK as MSET can't fail
     * @see #msetnx(String...)
     */
    public String mset(String... keysvalues) {
        Jedis jedis = getJedis();
        try {
            return jedis.mset(keysvalues);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * The TTL command returns the remaining time to live in seconds of a key that has an
     * {@link #expire(byte[], int) EXPIRE} set. This introspection capability allows a Redis client to
     * check how many seconds a given key will continue to be part of the dataset.
     *
     * @param key
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an
     * EXPIRE. If the Key does not exists or does not have an associated expire, -1 is
     * returned.
     */
    public Long ttl(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the the respective keys to the respective values. {@link #mset(String...) MSET} will
     * replace old values with new values, while MSETNX will not perform any operation at all even if
     * just a single key already exists.
     * <p>
     * Because of this semantic MSETNX can be used in order to set different keys representing
     * different fields of an unique logic object in a way that ensures that either all the fields or
     * none at all are set.
     * <p>
     * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B
     * are modified, another client talking to Redis can either see the changes to both A and B at
     * once, or no modification at all.
     *
     * @param keysvalues
     * @return Integer reply, specifically: 1 if the all the keys were set 0 if no key was set (at
     * least one key already existed)
     * @see #mset(String...)
     */
    public Long msetnx(String... keysvalues) {
        Jedis jedis = getJedis();
        try {
            return jedis.msetnx(keysvalues);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Select the DB with having the specified zero-based numeric index. For default every new client
     * connection is automatically selected to DB 0.
     *
     * @param index
     * @return Status code reply
     */
    public String select(int index) {
        Jedis jedis = getJedis();
        try {
            return jedis.select(index);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Move the specified key from the currently selected DB to the specified destination DB. Note
     * that this command returns 1 only if the key was successfully moved, and 0 if the target key was
     * already there or if the source key was not found at all, so it is possible to use MOVE as a
     * locking primitive.
     *
     * @param key
     * @param dbIndex
     * @return Integer reply, specifically: 1 if the key was moved 0 if the key was not moved because
     * already present on the target DB or was not found in the current DB.
     */
    public Long move(byte[] key, int dbIndex) {
        Jedis jedis = getJedis();
        try {
            return jedis.move(key, dbIndex);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * IDECRBY work just like {@link #decr(String) INCR} but instead to decrement by 1 the decrement
     * is integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(String)
     * @see #decr(String)
     * @see #incrBy(String, long)
     */
    public Long decrBy(String key, long integer) {
        Jedis jedis = getJedis();
        try {
            return jedis.decrBy(key, integer);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Delete all the keys of all the existing databases, not just the currently selected one. This
     * command never fails.
     *
     * @return Status code reply
     */
    public String flushAll() {
        Jedis jedis = getJedis();
        try {
            return jedis.flushAll();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * GETSET is an atomic set this value and return the old value command. Set key to the string
     * value and return the old value stored at key. The string can't be longer than 1073741824 bytes (1 GB).
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Bulk reply
     */
    public byte[] getSet(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.getSet(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Decrement the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the decrement operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(String)
     * @see #incrBy(String, long)
     * @see #decrBy(String, long)
     */
    public Long decr(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.decr(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Get the values of all the specified keys. If one or more keys dont exist or is not of type
     * String, a 'nil' value is returned instead of the value of the specified key, but the operation
     * never fails.
     * <p>
     * Time complexity: O(1) for every key
     *
     * @param keys
     * @return Multi bulk reply
     */
    public List<byte[]> mget(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.mget(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * INCRBY work just like {@link #incr(String) INCR} but instead to increment by 1 the increment is
     * integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(String)
     * @see #decr(String)
     * @see #decrBy(String, long)
     */
    public Long incrBy(String key, long integer) {
        Jedis jedis = getJedis();
        try {
            return jedis.incrBy(key, integer);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * SETNX works exactly like {@link #set(byte[], byte[]) SET} with the only difference that if the
     * key already exists no operation is performed. SETNX actually means "SET if Not eXists".
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Integer reply, specifically: 1 if the key was set 0 if the key was not set
     */
    public Long setnx(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setnx(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * The command is exactly equivalent to the following group of commands:
     * {@link #set(byte[], byte[]) SET} + {@link #expire(byte[], int) EXPIRE}. The operation is
     * atomic.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param seconds
     * @param value
     * @return Status code reply
     */
    public String setex(byte[] key, int seconds, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setex(key, seconds, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * INCRBYFLOAT
     * <p>
     * INCRBYFLOAT commands are limited to double precision floating point values.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "double" types.
     * Simply the string stored at the key is parsed as a base double precision floating point value,
     * incremented, and then converted back as a string. There is no DECRYBYFLOAT but providing a
     * negative value will work as expected.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Double reply, this commands will reply with the new value of key after the increment.
     */
    public Double incrByFloat(String key, double value) {
        Jedis jedis = getJedis();
        try {
            return jedis.incrByFloat(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the the respective keys to the respective values. MSET will replace old values with new
     * values, while {@link #msetnx(byte[]...) MSETNX} will not perform any operation at all even if
     * just a single key already exists.
     * <p>
     * Because of this semantic MSETNX can be used in order to set different keys representing
     * different fields of an unique logic object in a way that ensures that either all the fields or
     * none at all are set.
     * <p>
     * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B
     * are modified, another client talking to Redis can either see the changes to both A and B at
     * once, or no modification at all.
     *
     * @param keysvalues
     * @return Status code reply Basically +OK as MSET can't fail
     * @see #msetnx(byte[]...)
     */
    public String mset(byte[]... keysvalues) {
        Jedis jedis = getJedis();
        try {
            return jedis.mset(keysvalues);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Increment the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the increment operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incrBy(String, long)
     * @see #decr(String)
     * @see #decrBy(String, long)
     */
    public Long incr(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.incr(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the the respective keys to the respective values. {@link #mset(byte[]...) MSET} will
     * replace old values with new values, while MSETNX will not perform any operation at all even if
     * just a single key already exists.
     * <p>
     * Because of this semantic MSETNX can be used in order to set different keys representing
     * different fields of an unique logic object in a way that ensures that either all the fields or
     * none at all are set.
     * <p>
     * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B
     * are modified, another client talking to Redis can either see the changes to both A and B at
     * once, or no modification at all.
     *
     * @param keysvalues
     * @return Integer reply, specifically: 1 if the all the keys were set 0 if no key was set (at
     * least one key already existed)
     * @see #mset(byte[]...)
     */
    public Long msetnx(byte[]... keysvalues) {
        Jedis jedis = getJedis();
        try {
            return jedis.msetnx(keysvalues);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * If the key already exists and is a string, this command appends the provided value at the end
     * of the string. If the key does not exist it is created and set as an empty string, so APPEND
     * will be very similar to SET in this special case.
     * <p>
     * Time complexity: O(1). The amortized time complexity is O(1) assuming the appended value is
     * small and the already present value is of any size, since the dynamic string library used by
     * Redis will double the free space available on every reallocation.
     *
     * @param key
     * @param value
     * @return Integer reply, specifically the total length of the string after the append operation.
     */
    public Long append(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.append(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * DECRBY work just like {@link #decr(byte[]) INCR} but instead to decrement by 1 the decrement is
     * integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(byte[])
     * @see #decr(byte[])
     * @see #incrBy(byte[], long)
     */
    public Long decrBy(byte[] key, long integer) {
        Jedis jedis = getJedis();
        try {
            return jedis.decrBy(key, integer);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return a subset of the string from offset start to offset end (both offsets are inclusive).
     * Negative offsets can be used in order to provide an offset starting from the end of the string.
     * So -1 means the last char, -2 the penultimate and so forth.
     * <p>
     * The function handles out of range requests without raising an error, but just limiting the
     * resulting range to the actual length of the string.
     * <p>
     * Time complexity: O(start+n) (with start being the start index and n the total length of the
     * requested range). Note that the lookup part of this command is O(1) so for small strings this
     * is actually an O(1) command.
     *
     * @param key
     * @param start
     * @param end
     * @return Bulk reply
     */
    public String substr(String key, int start, int end) {
        Jedis jedis = getJedis();
        try {
            return jedis.substr(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Decrement the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the decrement operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(byte[])
     * @see #incrBy(byte[], long)
     * @see #decrBy(byte[], long)
     */
    public Long decr(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.decr(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the specified hash field to the specified value.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, and the HSET just produced an update of the value, 0 is
     * returned, otherwise if a new field is created 1 is returned.
     */
    public Long hset(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hset(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * INCRBY work just like {@link #incr(byte[]) INCR} but instead to increment by 1 the increment is
     * integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(byte[])
     * @see #decr(byte[])
     * @see #decrBy(byte[], long)
     */
    public Long incrBy(byte[] key, long integer) {
        Jedis jedis = getJedis();
        try {
            return jedis.incrBy(key, integer);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * If key holds a hash, retrieve the value associated to the specified field.
     * <p>
     * If the field is not found or the key does not exist, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @return Bulk reply
     */
    public String hget(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hget(key, field);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the specified hash field to the specified value if the field not exists. <b>Time
     * complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, 0 is returned, otherwise if a new field is created 1 is
     * returned.
     */
    public Long hsetnx(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hsetnx(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * INCRBYFLOAT work just like {@link #incrBy(byte[], long)} INCRBY} but increments by floats
     * instead of integers.
     * <p>
     * INCRBYFLOAT commands are limited to double precision floating point values.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "double" types.
     * Simply the string stored at the key is parsed as a base double precision floating point value,
     * incremented, and then converted back as a string. There is no DECRYBYFLOAT but providing a
     * negative value will work as expected.
     * <p>
     * Time complexity: O(1)
     *
     * @param key     the key to increment
     * @param integer the value to increment by
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incr(byte[])
     * @see #decr(byte[])
     * @see #decrBy(byte[], long)
     */
    public Double incrByFloat(byte[] key, double integer) {
        Jedis jedis = getJedis();
        try {
            return jedis.incrByFloat(key, integer);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the respective fields to the respective values. HMSET replaces old values with new values.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     *
     * @param key
     * @param hash
     * @return Return OK or Exception if hash is empty
     */
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmset(key, hash);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Retrieve the values associated to the specified fields.
     * <p>
     * If some of the specified fields do not exist, nil values are returned. Non existing keys are
     * considered like empty hashes.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     *
     * @param key
     * @param fields
     * @return Multi Bulk Reply specifically a list of all the values associated with the specified
     * fields, in the same order of the request.
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmget(key, fields);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Increment the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the increment operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
     * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
     * and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     * @see #incrBy(byte[], long)
     * @see #decr(byte[])
     * @see #decrBy(byte[], long)
     */
    public Long incr(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.incr(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Increment the number stored at field in the hash at key by value. If key does not exist, a new
     * key holding a hash is created. If field does not exist or holds a string, the value is set to 0
     * before applying the operation. Since the value argument is signed you can use this command to
     * perform both increments and decrements.
     * <p>
     * The range of values supported by HINCRBY is limited to 64 bit signed integers.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return Integer reply The new value at field after the increment operation.
     */
    public Long hincrBy(String key, String field, long value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hincrBy(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * If the key already exists and is a string, this command appends the provided value at the end
     * of the string. If the key does not exist it is created and set as an empty string, so APPEND
     * will be very similar to SET in this special case.
     * <p>
     * Time complexity: O(1). The amortized time complexity is O(1) assuming the appended value is
     * small and the already present value is of any size, since the dynamic string library used by
     * Redis will double the free space available on every reallocation.
     *
     * @param key
     * @param value
     * @return Integer reply, specifically the total length of the string after the append operation.
     */
    public Long append(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.append(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Increment the number stored at field in the hash at key by a double precision floating point
     * value. If key does not exist, a new key holding a hash is created. If field does not exist or
     * holds a string, the value is set to 0 before applying the operation. Since the value argument
     * is signed you can use this command to perform both increments and decrements.
     * <p>
     * The range of values supported by HINCRBYFLOAT is limited to double precision floating point
     * values.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return Double precision floating point reply The new value at field after the increment
     * operation.
     */
    public Double hincrByFloat(String key, String field, double value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hincrByFloat(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return a subset of the string from offset start to offset end (both offsets are inclusive).
     * Negative offsets can be used in order to provide an offset starting from the end of the string.
     * So -1 means the last char, -2 the penultimate and so forth.
     * <p>
     * The function handles out of range requests without raising an error, but just limiting the
     * resulting range to the actual length of the string.
     * <p>
     * Time complexity: O(start+n) (with start being the start index and n the total length of the
     * requested range). Note that the lookup part of this command is O(1) so for small strings this
     * is actually an O(1) command.
     *
     * @param key
     * @param start
     * @param end
     * @return Bulk reply
     */
    public byte[] substr(byte[] key, int start, int end) {
        Jedis jedis = getJedis();
        try {
            return jedis.substr(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Test for existence of a specified field in a hash. <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @return Return 1 if the hash stored at key contains the specified field. Return 0 if the key is
     * not found or the field is not present.
     */
    public Boolean hexists(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hexists(key, field);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified field from an hash stored at key.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param fields
     * @return If the field was present in the hash it is deleted and 1 is returned, otherwise 0 is
     * returned and no operation is performed.
     */
    public Long hdel(String key, String... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(key, fields);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the specified hash field to the specified value.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, and the HSET just produced an update of the value, 0 is
     * returned, otherwise if a new field is created 1 is returned.
     */
    public Long hset(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hset(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the number of items in a hash.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @return The number of entries (fields) contained in the hash stored at key. If the specified
     * key does not exist, 0 is returned assuming an empty hash.
     */
    public Long hlen(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hlen(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * If key holds a hash, retrieve the value associated to the specified field.
     * <p>
     * If the field is not found or the key does not exist, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @return Bulk reply
     */
    public byte[] hget(byte[] key, byte[] field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hget(key, field);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the fields in a hash.
     * <p>
     * <b>Time complexity:</b> O(N), where N is the total number of entries
     *
     * @param key
     * @return All the fields names contained into a hash.
     */
    public Set<String> hkeys(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hkeys(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the specified hash field to the specified value if the field not exists. <b>Time
     * complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, 0 is returned, otherwise if a new field is created 1 is
     * returned.
     */
    public Long hsetnx(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hsetnx(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the values in a hash.
     * <p>
     * <b>Time complexity:</b> O(N), where N is the total number of entries
     *
     * @param key
     * @return All the fields values contained into a hash.
     */
    public List<String> hvals(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hvals(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set the respective fields to the respective values. HMSET replaces old values with new values.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     *
     * @param key
     * @param hash
     * @return Always OK because HMSET can't fail
     */
    public String hmset(byte[] key, Map<byte[], byte[]> hash) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmset(key, hash);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the fields and associated values in a hash.
     * <p>
     * <b>Time complexity:</b> O(N), where N is the total number of entries
     *
     * @param key
     * @return All the fields and values contained into a hash.
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hgetAll(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key
     * does not exist an empty list is created just before the append operation. If the key exists but
     * is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param strings
     * @return Integer reply, specifically, the number of elements inside the list after the push
     * operation.
     */
    public Long rpush(String key, String... strings) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpush(key, strings);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Retrieve the values associated to the specified fields.
     * <p>
     * If some of the specified fields do not exist, nil values are returned. Non existing keys are
     * considered like empty hashes.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     *
     * @param key
     * @param fields
     * @return Multi Bulk Reply specifically a list of all the values associated with the specified
     * fields, in the same order of the request.
     */
    public List<byte[]> hmget(byte[] key, byte[]... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmget(key, fields);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key
     * does not exist an empty list is created just before the append operation. If the key exists but
     * is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param strings
     * @return Integer reply, specifically, the number of elements inside the list after the push
     * operation.
     */
    public Long lpush(String key, String... strings) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpush(key, strings);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Increment the number stored at field in the hash at key by value. If key does not exist, a new
     * key holding a hash is created. If field does not exist or holds a string, the value is set to 0
     * before applying the operation. Since the value argument is signed you can use this command to
     * perform both increments and decrements.
     * <p>
     * The range of values supported by HINCRBY is limited to 64 bit signed integers.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return Integer reply The new value at field after the increment operation.
     */
    public Long hincrBy(byte[] key, byte[] field, long value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hincrBy(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the length of the list stored at the specified key. If the key does not exist zero is
     * returned (the same behaviour as for empty lists). If the value stored at key is not a list an
     * error is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return The length of the list.
     */
    public Long llen(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.llen(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Increment the number stored at field in the hash at key by a double precision floating point
     * value. If key does not exist, a new key holding a hash is created. If field does not exist or
     * holds a string, the value is set to 0 before applying the operation. Since the value argument
     * is signed you can use this command to perform both increments and decrements.
     * <p>
     * The range of values supported by HINCRBYFLOAT is limited to double precision floating point
     * values.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @param value
     * @return Double precision floating point reply The new value at field after the increment
     * operation.
     */
    public Double hincrByFloat(byte[] key, byte[] field, double value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hincrByFloat(key, field, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the specified elements of the list stored at the specified key. Start and end are
     * zero-based indexes. 0 is the first element of the list (the list head), 1 the next element and
     * so on.
     * <p>
     * For example LRANGE foobar 0 2 will return the first three elements of the list.
     * <p>
     * start and end can also be negative numbers indicating offsets from the end of the list. For
     * example -1 is the last element of the list, -2 the penultimate element and so on.
     * <p>
     * <b>Consistency with range functions in various programming languages</b>
     * <p>
     * Note that if you have a list of numbers from 0 to 100, LRANGE 0 10 will return 11 elements,
     * that is, rightmost item is included. This may or may not be consistent with behavior of
     * range-related functions in your programming language of choice (think Ruby's Range.new,
     * Array#slice or Python's range() function).
     * <p>
     * LRANGE behavior is consistent with one of Tcl.
     * <p>
     * <b>Out-of-range indexes</b>
     * <p>
     * Indexes out of range will not produce an error: if start is over the end of the list, or start
     * &gt; end, an empty list is returned. If end is over the end of the list Redis will threat it
     * just like the last element of the list.
     * <p>
     * Time complexity: O(start+n) (with n being the length of the range and start being the start
     * offset)
     *
     * @param key
     * @param start
     * @param end
     * @return Multi bulk reply, specifically a list of elements in the specified range.
     */
    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.lrange(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Test for existence of a specified field in a hash. <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param field
     * @return Return 1 if the hash stored at key contains the specified field. Return 0 if the key is
     * not found or the field is not present.
     */
    public Boolean hexists(byte[] key, byte[] field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hexists(key, field);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified field from an hash stored at key.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param fields
     * @return If the field was present in the hash it is deleted and 1 is returned, otherwise 0 is
     * returned and no operation is performed.
     */
    public Long hdel(byte[] key, byte[]... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(key, fields);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Trim an existing list so that it will contain only the specified range of elements specified.
     * Start and end are zero-based indexes. 0 is the first element of the list (the list head), 1 the
     * next element and so on.
     * <p>
     * For example LTRIM foobar 0 2 will modify the list stored at foobar key so that only the first
     * three elements of the list will remain.
     * <p>
     * start and end can also be negative numbers indicating offsets from the end of the list. For
     * example -1 is the last element of the list, -2 the penultimate element and so on.
     * <p>
     * Indexes out of range will not produce an error: if start is over the end of the list, or start
     * &gt; end, an empty list is left as value. If end over the end of the list Redis will threat it
     * just like the last element of the list.
     * <p>
     * Hint: the obvious use of LTRIM is together with LPUSH/RPUSH. For example:
     * <p>
     * {@code lpush("mylist", "someelement"); ltrim("mylist", 0, 99); * }
     * <p>
     * The above two commands will push elements in the list taking care that the list will not grow
     * without limits. This is very useful when using Redis to store logs for example. It is important
     * to note that when used in this way LTRIM is an O(1) operation because in the average case just
     * one element is removed from the tail of the list.
     * <p>
     * Time complexity: O(n) (with n being len of list - len of range)
     *
     * @param key
     * @param start
     * @param end
     * @return Status code reply
     */
    public String ltrim(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.ltrim(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the number of items in a hash.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @return The number of entries (fields) contained in the hash stored at key. If the specified
     * key does not exist, 0 is returned assuming an empty hash.
     */
    public Long hlen(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hlen(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the fields in a hash.
     * <p>
     * <b>Time complexity:</b> O(N), where N is the total number of entries
     *
     * @param key
     * @return All the fields names contained into a hash.
     */
    public Set<byte[]> hkeys(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hkeys(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the values in a hash.
     * <p>
     * <b>Time complexity:</b> O(N), where N is the total number of entries
     *
     * @param key
     * @return All the fields values contained into a hash.
     */
    public List<byte[]> hvals(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hvals(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the fields and associated values in a hash.
     * <p>
     * <b>Time complexity:</b> O(N), where N is the total number of entries
     *
     * @param key
     * @return All the fields and values contained into a hash.
     */
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hgetAll(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the specified element of the list stored at the specified key. 0 is the first element, 1
     * the second and so on. Negative indexes are supported, for example -1 is the last element, -2
     * the penultimate and so on.
     * <p>
     * If the value stored at key is not of list type an error is returned. If the index is out of
     * range a 'nil' reply is returned.
     * <p>
     * Note that even if the average time complexity is O(n) asking for the first or the last element
     * of the list is O(1).
     * <p>
     * Time complexity: O(n) (with n being the length of the list)
     *
     * @param key
     * @param index
     * @return Bulk reply, specifically the requested element
     */
    public String lindex(String key, long index) {
        Jedis jedis = getJedis();
        try {
            return jedis.lindex(key, index);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key
     * does not exist an empty list is created just before the append operation. If the key exists but
     * is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param strings
     * @return Integer reply, specifically, the number of elements inside the list after the push
     * operation.
     * @see BinaryJedis#rpush(byte[], byte[]...)
     */
    public Long rpush(byte[] key, byte[]... strings) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpush(key, strings);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key
     * does not exist an empty list is created just before the append operation. If the key exists but
     * is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param strings
     * @return Integer reply, specifically, the number of elements inside the list after the push
     * operation.
     * @see BinaryJedis#rpush(byte[], byte[]...)
     */
    public Long lpush(byte[] key, byte[]... strings) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpush(key, strings);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set a new value as the element at index position of the List at key.
     * <p>
     * Out of range indexes will generate an error.
     * <p>
     * Similarly to other list commands accepting indexes, the index can be negative to access
     * elements starting from the end of the list. So -1 is the last element, -2 is the penultimate,
     * and so forth.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(N) (with N being the length of the list), setting the first or last elements of the list is
     * O(1).
     *
     * @param key
     * @param index
     * @param value
     * @return Status code reply
     * @see #lindex(String, long)
     */
    public String lset(String key, long index, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.lset(key, index, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the length of the list stored at the specified key. If the key does not exist zero is
     * returned (the same behaviour as for empty lists). If the value stored at key is not a list an
     * error is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return The length of the list.
     */
    public Long llen(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.llen(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the first count occurrences of the value element from the list. If count is zero all the
     * elements are removed. If count is negative elements are removed from tail to head, instead to
     * go from head to tail that is the normal behaviour. So for example LREM with count -2 and hello
     * as value to remove against the list (a,b,c,hello,x,hello,hello) will lave the list
     * (a,b,c,hello,x). The number of removed elements is returned as an integer, see below for more
     * information about the returned value. Note that non existing keys are considered like empty
     * lists by LREM, so LREM against non existing keys will always return 0.
     * <p>
     * Time complexity: O(N) (with N being the length of the list)
     *
     * @param key
     * @param count
     * @param value
     * @return Integer Reply, specifically: The number of removed elements if the operation succeeded
     */
    public Long lrem(String key, long count, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.lrem(key, count, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the specified elements of the list stored at the specified key. Start and end are
     * zero-based indexes. 0 is the first element of the list (the list head), 1 the next element and
     * so on.
     * <p>
     * For example LRANGE foobar 0 2 will return the first three elements of the list.
     * <p>
     * start and end can also be negative numbers indicating offsets from the end of the list. For
     * example -1 is the last element of the list, -2 the penultimate element and so on.
     * <p>
     * <b>Consistency with range functions in various programming languages</b>
     * <p>
     * Note that if you have a list of numbers from 0 to 100, LRANGE 0 10 will return 11 elements,
     * that is, rightmost item is included. This may or may not be consistent with behavior of
     * range-related functions in your programming language of choice (think Ruby's Range.new,
     * Array#slice or Python's range() function).
     * <p>
     * LRANGE behavior is consistent with one of Tcl.
     * <p>
     * <b>Out-of-range indexes</b>
     * <p>
     * Indexes out of range will not produce an error: if start is over the end of the list, or start
     * &gt; end, an empty list is returned. If end is over the end of the list Redis will threat it
     * just like the last element of the list.
     * <p>
     * Time complexity: O(start+n) (with n being the length of the range and start being the start
     * offset)
     *
     * @param key
     * @param start
     * @param end
     * @return Multi bulk reply, specifically a list of elements in the specified range.
     */
    public List<byte[]> lrange(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.lrange(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" LPOP will return "a" and the list will become
     * "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     *
     * @param key
     * @return Bulk reply
     * @see #rpop(String)
     */
    public String lpop(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpop(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" RPOP will return "c" and the list will become
     * "a","b".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     *
     * @param key
     * @return Bulk reply
     * @see #lpop(String)
     */
    public String rpop(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpop(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Trim an existing list so that it will contain only the specified range of elements specified.
     * Start and end are zero-based indexes. 0 is the first element of the list (the list head), 1 the
     * next element and so on.
     * <p>
     * For example LTRIM foobar 0 2 will modify the list stored at foobar key so that only the first
     * three elements of the list will remain.
     * <p>
     * start and end can also be negative numbers indicating offsets from the end of the list. For
     * example -1 is the last element of the list, -2 the penultimate element and so on.
     * <p>
     * Indexes out of range will not produce an error: if start is over the end of the list, or start
     * &gt; end, an empty list is left as value. If end over the end of the list Redis will threat it
     * just like the last element of the list.
     * <p>
     * Hint: the obvious use of LTRIM is together with LPUSH/RPUSH. For example:
     * <p>
     * {@code lpush("mylist", "someelement"); ltrim("mylist", 0, 99); * }
     * <p>
     * The above two commands will push elements in the list taking care that the list will not grow
     * without limits. This is very useful when using Redis to store logs for example. It is important
     * to note that when used in this way LTRIM is an O(1) operation because in the average case just
     * one element is removed from the tail of the list.
     * <p>
     * Time complexity: O(n) (with n being len of list - len of range)
     *
     * @param key
     * @param start
     * @param end
     * @return Status code reply
     */
    public String ltrim(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.ltrim(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically return and remove the last (tail) element of the srckey list, and push the element
     * as the first (head) element of the dstkey list. For example if the source list contains the
     * elements "a","b","c" and the destination list contains the elements "foo","bar" after an
     * RPOPLPUSH command the content of the two lists will be "a","b" and "c","foo","bar".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned. If
     * the srckey and dstkey are the same the operation is equivalent to removing the last element
     * from the list and pusing it as first element of the list, so it's a "list rotation" command.
     * <p>
     * Time complexity: O(1)
     *
     * @param srckey
     * @param dstkey
     * @return Bulk reply
     */
    public String rpoplpush(String srckey, String dstkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpoplpush(srckey, dstkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the specified member to the set value stored at key. If member is already a member of the
     * set no operation is performed. If key does not exist a new set with the specified member as
     * sole member is created. If the key exists but does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param members
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
     * already a member of the set
     */
    public Long sadd(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.sadd(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the specified element of the list stored at the specified key. 0 is the first element, 1
     * the second and so on. Negative indexes are supported, for example -1 is the last element, -2
     * the penultimate and so on.
     * <p>
     * If the value stored at key is not of list type an error is returned. If the index is out of
     * range a 'nil' reply is returned.
     * <p>
     * Note that even if the average time complexity is O(n) asking for the first or the last element
     * of the list is O(1).
     * <p>
     * Time complexity: O(n) (with n being the length of the list)
     *
     * @param key
     * @param index
     * @return Bulk reply, specifically the requested element
     */
    public byte[] lindex(byte[] key, long index) {
        Jedis jedis = getJedis();
        try {
            return jedis.lindex(key, index);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the members (elements) of the set value stored at key. This is just syntax glue for
     * {@link #sinter(String...) SINTER}.
     * <p>
     * Time complexity O(N)
     *
     * @param key
     * @return Multi bulk reply
     */
    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.smembers(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified member from the set value stored at key. If member was not a member of the
     * set no operation is performed. If key does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param members
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was
     * not a member of the set
     */
    public Long srem(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.srem(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Set a new value as the element at index position of the List at key.
     * <p>
     * Out of range indexes will generate an error.
     * <p>
     * Similarly to other list commands accepting indexes, the index can be negative to access
     * elements starting from the end of the list. So -1 is the last element, -2 is the penultimate,
     * and so forth.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(N) (with N being the length of the list), setting the first or last elements of the list is
     * O(1).
     *
     * @param key
     * @param index
     * @param value
     * @return Status code reply
     * @see #lindex(byte[], long)
     */
    public String lset(byte[] key, long index, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.lset(key, index, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the key
     * does not exist, a nil object is returned.
     * <p>
     * The {@link #srandmember(String)} command does a similar work but the returned element is not
     * removed from the Set.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public String spop(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.spop(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> spop(String key, long count) {
        Jedis jedis = getJedis();
        try {
            return jedis.spop(key, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the first count occurrences of the value element from the list. If count is zero all the
     * elements are removed. If count is negative elements are removed from tail to head, instead to
     * go from head to tail that is the normal behaviour. So for example LREM with count -2 and hello
     * as value to remove against the list (a,b,c,hello,x,hello,hello) will have the list
     * (a,b,c,hello,x). The number of removed elements is returned as an integer, see below for more
     * information about the returned value. Note that non existing keys are considered like empty
     * lists by LREM, so LREM against non existing keys will always return 0.
     * <p>
     * Time complexity: O(N) (with N being the length of the list)
     *
     * @param key
     * @param count
     * @param value
     * @return Integer Reply, specifically: The number of removed elements if the operation succeeded
     */
    public Long lrem(byte[] key, long count, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.lrem(key, count, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Move the specifided member from the set at srckey to the set at dstkey. This operation is
     * atomic, in every given moment the element will appear to be in the source or destination set
     * for accessing clients.
     * <p>
     * If the source set does not exist or does not contain the specified element no operation is
     * performed and zero is returned, otherwise the element is removed from the source set and added
     * to the destination set. On success one is returned, even if the element was already present in
     * the destination set.
     * <p>
     * An error is raised if the source or destination keys contain a non Set value.
     * <p>
     * Time complexity O(1)
     *
     * @param srckey
     * @param dstkey
     * @param member
     * @return Integer reply, specifically: 1 if the element was moved 0 if the element was not found
     * on the first set and no operation was performed
     */
    public Long smove(String srckey, String dstkey, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.smove(srckey, dstkey, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" LPOP will return "a" and the list will become
     * "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     *
     * @param key
     * @return Bulk reply
     * @see #rpop(byte[])
     */
    public byte[] lpop(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpop(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the set cardinality (number of elements). If the key does not exist 0 is returned, like
     * for empty sets.
     *
     * @param key
     * @return Integer reply, specifically: the cardinality (number of elements) of the set as an
     * integer.
     */
    public Long scard(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.scard(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" LPOP will return "a" and the list will become
     * "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     *
     * @param key
     * @return Bulk reply
     * @see #lpop(byte[])
     */
    public byte[] rpop(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpop(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return 1 if member is a member of the set stored at key, otherwise 0 is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the element is a member of the set 0 if the element
     * is not a member of the set OR if the key does not exist
     */
    public Boolean sismember(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.sismember(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Atomically return and remove the last (tail) element of the srckey list, and push the element
     * as the first (head) element of the dstkey list. For example if the source list contains the
     * elements "a","b","c" and the destination list contains the elements "foo","bar" after an
     * RPOPLPUSH command the content of the two lists will be "a","b" and "c","foo","bar".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned. If
     * the srckey and dstkey are the same the operation is equivalent to removing the last element
     * from the list and pusing it as first element of the list, so it's a "list rotation" command.
     * <p>
     * Time complexity: O(1)
     *
     * @param srckey
     * @param dstkey
     * @return Bulk reply
     */
    public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpoplpush(srckey, dstkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the members of a set resulting from the intersection of all the sets hold at the
     * specified keys. Like in {@link #lrange(String, long, long) LRANGE} the result is sent to the
     * client as a multi-bulk reply (see the protocol specification for more information). If just a
     * single key is specified, then this command produces the same result as
     * {@link #smembers(String) SMEMBERS}. Actually SMEMBERS is just syntax sugar for SINTER.
     * <p>
     * Non existing keys are considered like empty sets, so if one of the keys is missing an empty set
     * is returned (since the intersection with an empty set always is an empty set).
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the
     * number of sets
     *
     * @param keys
     * @return Multi bulk reply, specifically the list of common elements.
     */
    public Set<String> sinter(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sinter(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the specified member to the set value stored at key. If member is already a member of the
     * set no operation is performed. If key does not exist a new set with the specified member as
     * sole member is created. If the key exists but does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param members
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
     * already a member of the set
     */
    public Long sadd(byte[] key, byte[]... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.sadd(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * This commnad works exactly like {@link #sinter(String...) SINTER} but instead of being returned
     * the resulting set is sotred as dstkey.
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the
     * number of sets
     *
     * @param dstkey
     * @param keys
     * @return Status code reply
     */
    public Long sinterstore(String dstkey, String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sinterstore(dstkey, keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return all the members (elements) of the set value stored at key. This is just syntax glue for
     * {@link #sinter(byte[]...)} SINTER}.
     * <p>
     * Time complexity O(N)
     *
     * @param key the key of the set
     * @return Multi bulk reply
     */
    public Set<byte[]> smembers(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.smembers(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the members of a set resulting from the union of all the sets hold at the specified
     * keys. Like in {@link #lrange(String, long, long) LRANGE} the result is sent to the client as a
     * multi-bulk reply (see the protocol specification for more information). If just a single key is
     * specified, then this command produces the same result as {@link #smembers(String) SMEMBERS}.
     * <p>
     * Non existing keys are considered like empty sets.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     *
     * @param keys
     * @return Multi bulk reply, specifically the list of common elements.
     */
    public Set<String> sunion(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sunion(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified member from the set value stored at key. If member was not a member of the
     * set no operation is performed. If key does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key    the key of the set
     * @param member the set member to remove
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was
     * not a member of the set
     */
    public Long srem(byte[] key, byte[]... member) {
        Jedis jedis = getJedis();
        try {
            return jedis.srem(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the key
     * does not exist, a nil object is returned.
     * <p>
     * The {@link #srandmember(byte[])} command does a similar work but the returned element is not
     * removed from the Set.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public byte[] spop(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.spop(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * This command works exactly like {@link #sunion(String...) SUNION} but instead of being returned
     * the resulting set is stored as dstkey. Any existing value in dstkey will be over-written.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     *
     * @param dstkey
     * @param keys
     * @return Status code reply
     */
    public Long sunionstore(String dstkey, String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sunionstore(dstkey, keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> spop(byte[] key, long count) {
        Jedis jedis = getJedis();
        try {
            return jedis.spop(key, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the difference between the Set stored at key1 and all the Sets key2, ..., keyN
     * <p>
     * <b>Example:</b>
     * <p>
     * <pre>
     * key1 = [x, a, b, c]
     * key2 = [c]
     * key3 = [a, d]
     * SDIFF key1,key2,key3 =&gt; [x, b]
     * </pre>
     * <p>
     * Non existing keys are considered like empty sets.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(N) with N being the total number of elements of all the sets
     *
     * @param keys
     * @return Return the members of a set resulting from the difference between the first set
     * provided and all the successive sets.
     */
    public Set<String> sdiff(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sdiff(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Move the specified member from the set at srckey to the set at dstkey. This operation is
     * atomic, in every given moment the element will appear to be in the source or destination set
     * for accessing clients.
     * <p>
     * If the source set does not exist or does not contain the specified element no operation is
     * performed and zero is returned, otherwise the element is removed from the source set and added
     * to the destination set. On success one is returned, even if the element was already present in
     * the destination set.
     * <p>
     * An error is raised if the source or destination keys contain a non Set value.
     * <p>
     * Time complexity O(1)
     *
     * @param srckey
     * @param dstkey
     * @param member
     * @return Integer reply, specifically: 1 if the element was moved 0 if the element was not found
     * on the first set and no operation was performed
     */
    public Long smove(byte[] srckey, byte[] dstkey, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.smove(srckey, dstkey, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * This command works exactly like {@link #sdiff(String...) SDIFF} but instead of being returned
     * the resulting set is stored in dstkey.
     *
     * @param dstkey
     * @param keys
     * @return Status code reply
     */
    public Long sdiffstore(String dstkey, String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sdiffstore(dstkey, keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return a random element from a Set, without removing the element. If the Set is empty or the
     * key does not exist, a nil object is returned.
     * <p>
     * The SPOP command does a similar work but the returned element is popped (removed) from the Set.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public String srandmember(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.srandmember(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the set cardinality (number of elements). If the key does not exist 0 is returned, like
     * for empty sets.
     *
     * @param key
     * @return Integer reply, specifically: the cardinality (number of elements) of the set as an
     * integer.
     */
    public Long scard(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.scard(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return 1 if member is a member of the set stored at key, otherwise 0 is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the element is a member of the set 0 if the element
     * is not a member of the set OR if the key does not exist
     */
    public Boolean sismember(byte[] key, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.sismember(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> srandmember(String key, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.srandmember(key, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     *
     * @param key
     * @param score
     * @param member
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
     * already a member of the sorted set and the score was updated
     */
    public Long zadd(String key, double score, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, score, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the members of a set resulting from the intersection of all the sets hold at the
     * specified keys. Like in {@link #lrange(byte[], long, long)} LRANGE} the result is sent to the
     * client as a multi-bulk reply (see the protocol specification for more information). If just a
     * single key is specified, then this command produces the same result as
     * {@link #smembers(byte[]) SMEMBERS}. Actually SMEMBERS is just syntax sugar for SINTER.
     * <p>
     * Non existing keys are considered like empty sets, so if one of the keys is missing an empty set
     * is returned (since the intersection with an empty set always is an empty set).
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the
     * number of sets
     *
     * @param keys
     * @return Multi bulk reply, specifically the list of common elements.
     */
    public Set<byte[]> sinter(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sinter(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zadd(String key, double score, String member, ZAddParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, score, member, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, scoreMembers);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * This commnad works exactly like {@link #sinter(byte[]...) SINTER} but instead of being returned
     * the resulting set is sotred as dstkey.
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the
     * number of sets
     *
     * @param dstkey
     * @param keys
     * @return Status code reply
     */
    public Long sinterstore(byte[] dstkey, byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sinterstore(dstkey, keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, scoreMembers, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrange(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the members of a set resulting from the union of all the sets hold at the specified
     * keys. Like in {@link #lrange(byte[], long, long)} LRANGE} the result is sent to the client as a
     * multi-bulk reply (see the protocol specification for more information). If just a single key is
     * specified, then this command produces the same result as {@link #smembers(byte[]) SMEMBERS}.
     * <p>
     * Non existing keys are considered like empty sets.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     *
     * @param keys
     * @return Multi bulk reply, specifically the list of common elements.
     */
    public Set<byte[]> sunion(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sunion(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified member from the sorted set value stored at key. If member was not a member
     * of the set no operation is performed. If key does not not hold a set value an error is
     * returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     *
     * @param key
     * @param members
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was
     * not a member of the set
     */
    public Long zrem(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrem(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * If member already exists in the sorted set adds the increment to its score and updates the
     * position of the element in the sorted set accordingly. If member does not already exist in the
     * sorted set it is added with increment as score (that is, like if the previous score was
     * virtually zero). If key does not exist a new sorted set with the specified member as sole
     * member is crated. If the key exists but does not hold a sorted set value an error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number.
     * It's possible to provide a negative value to perform a decrement.
     * <p>
     * For an introduction to sorted sets check the Introduction to Redis data types page.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     *
     * @param key
     * @param score
     * @param member
     * @return The new score
     */
    public Double zincrby(String key, double score, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zincrby(key, score, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * This command works exactly like {@link #sunion(byte[]...) SUNION} but instead of being returned
     * the resulting set is stored as dstkey. Any existing value in dstkey will be over-written.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     *
     * @param dstkey
     * @param keys
     * @return Status code reply
     */
    public Long sunionstore(byte[] dstkey, byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sunionstore(dstkey, keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the difference between the Set stored at key1 and all the Sets key2, ..., keyN
     * <p>
     * <b>Example:</b>
     * <p>
     * <pre>
     * key1 = [x, a, b, c]
     * key2 = [c]
     * key3 = [a, d]
     * SDIFF key1,key2,key3 =&gt; [x, b]
     * </pre>
     * <p>
     * Non existing keys are considered like empty sets.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(N) with N being the total number of elements of all the sets
     *
     * @param keys
     * @return Return the members of a set resulting from the difference between the first set
     * provided and all the successive sets.
     */
    public Set<byte[]> sdiff(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sdiff(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Double zincrby(String key, double score, String member, ZIncrByParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zincrby(key, score, member, params);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * This command works exactly like {@link #sdiff(byte[]...) SDIFF} but instead of being returned
     * the resulting set is stored in dstkey.
     *
     * @param dstkey
     * @param keys
     * @return Status code reply
     */
    public Long sdiffstore(byte[] dstkey, byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.sdiffstore(dstkey, keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from
     * low to high.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned.
     * The returned rank (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     *
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer
     * reply if the element exists. A nil bulk reply if there is no such element.
     * @see #zrevrank(String, String)
     */
    public Long zrank(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrank(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return a random element from a Set, without removing the element. If the Set is empty or the
     * key does not exist, a nil object is returned.
     * <p>
     * The SPOP command does a similar work but the returned element is popped (removed) from the Set.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public byte[] srandmember(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.srandmember(key);
        } finally {Streams.safeClose(jedis);}
    }

    public List<byte[]> srandmember(byte[] key, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.srandmember(key, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from
     * high to low.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned.
     * The returned rank (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     *
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer
     * reply if the element exists. A nil bulk reply if there is no such element.
     * @see #zrank(String, String)
     */
    public Long zrevrank(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrank(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member
     * is already a member of the sorted set the score is updated, and the element reinserted in the
     * right position to ensure sorting. If key does not exist a new sorted set with the specified
     * member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     *
     * @param key
     * @param score
     * @param member
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
     * already a member of the sorted set and the score was updated
     */
    public Long zadd(byte[] key, double score, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, score, member);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrange(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeWithScores(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zadd(byte[] key, double score, byte[] member, ZAddParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, score, member, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeWithScores(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, scoreMembers);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the sorted set cardinality (number of elements). If the key does not exist 0 is
     * returned, like for empty sorted sets.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return the cardinality (number of elements) of the set as an integer.
     */
    public Long zcard(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.zcard(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, scoreMembers, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrange(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrange(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the score of the specified element of the sorted set at key. If the specified element
     * does not exist in the sorted set, or the key does not exist at all, a special 'nil' value is
     * returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param member
     * @return the score
     */
    public Double zscore(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscore(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove the specified member from the sorted set value stored at key. If member was not a member
     * of the set no operation is performed. If key does not not hold a set value an error is
     * returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     *
     * @param key
     * @param members
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was
     * not a member of the set
     */
    public Long zrem(byte[] key, byte[]... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrem(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    public String watch(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.watch(keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List.
     * <p>
     * Sort the elements contained in the List, Set, or Sorted Set value at key. By default sorting is
     * numeric with elements being compared as double precision floating point numbers. This is the
     * simplest form of SORT.
     *
     * @param key
     * @return Assuming the Set/List at key contains a list of numbers, the return value will be the
     * list of numbers ordered from the smallest to the biggest number.
     * @see #sort(String, String)
     * @see #sort(String, SortingParams)
     * @see #sort(String, SortingParams, String)
     */
    public List<String> sort(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * If member already exists in the sorted set adds the increment to its score and updates the
     * position of the element in the sorted set accordingly. If member does not already exist in the
     * sorted set it is added with increment as score (that is, like if the previous score was
     * virtually zero). If key does not exist a new sorted set with the specified member as sole
     * member is crated. If the key exists but does not hold a sorted set value an error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number.
     * It's possible to provide a negative value to perform a decrement.
     * <p>
     * For an introduction to sorted sets check the Introduction to Redis data types page.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     *
     * @param key
     * @param score
     * @param member
     * @return The new score
     */
    public Double zincrby(byte[] key, double score, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zincrby(key, score, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters.
     * <p>
     * <b>examples:</b>
     * <p>
     * Given are the following sets and key/values:
     * <p>
     * <pre>
     * x = [1, 2, 3]
     * y = [a, b, c]
     *
     * k1 = z
     * k2 = y
     * k3 = x
     *
     * w1 = 9
     * w2 = 8
     * w3 = 7
     * </pre>
     * <p>
     * Sort Order:
     * <p>
     * <pre>
     * sort(x) or sort(x, sp.asc())
     * -&gt; [1, 2, 3]
     *
     * sort(x, sp.desc())
     * -&gt; [3, 2, 1]
     *
     * sort(y)
     * -&gt; [c, a, b]
     *
     * sort(y, sp.alpha())
     * -&gt; [a, b, c]
     *
     * sort(y, sp.alpha().desc())
     * -&gt; [c, a, b]
     * </pre>
     * <p>
     * Limit (e.g. for Pagination):
     * <p>
     * <pre>
     * sort(x, sp.limit(0, 2))
     * -&gt; [1, 2]
     *
     * sort(y, sp.alpha().desc().limit(1, 2))
     * -&gt; [b, a]
     * </pre>
     * <p>
     * Sorting by external keys:
     * <p>
     * <pre>
     * sort(x, sb.by(w*))
     * -&gt; [3, 2, 1]
     *
     * sort(x, sb.by(w*).desc())
     * -&gt; [1, 2, 3]
     * </pre>
     * <p>
     * Getting external keys:
     * <p>
     * <pre>
     * sort(x, sp.by(w*).get(k*))
     * -&gt; [x, y, z]
     *
     * sort(x, sp.by(w*).get(#).get(k*))
     * -&gt; [3, x, 2, y, 1, z]
     * </pre>
     *
     * @param key
     * @param sortingParameters
     * @return a list of sorted elements.
     * @see #sort(String)
     * @see #sort(String, SortingParams, String)
     */
    public List<String> sort(String key, SortingParams sortingParameters) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key, sortingParameters);
        } finally {Streams.safeClose(jedis);}
    }

    public Double zincrby(byte[] key, double score, byte[] member, ZIncrByParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zincrby(key, score, member, params);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from
     * low to high.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned.
     * The returned rank (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     *
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer
     * reply if the element exists. A nil bulk reply if there is no such element.
     * @see #zrevrank(byte[], byte[])
     */
    public Long zrank(byte[] key, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrank(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
     * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
     * lists.
     * <p>
     * The following is a description of the exact semantic. We describe BLPOP but the two commands
     * are identical, the only difference is that BLPOP pops the element from the left (head) of the
     * list, and BRPOP pops from the right (tail).
     * <p>
     * <b>Non blocking behavior</b>
     * <p>
     * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
     * element is popped from the head of the list and returned to the caller together with the name
     * of the key (BLPOP returns a two elements array, the first element is the key, the second the
     * popped value).
     * <p>
     * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
     * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
     * guarantees to return an element from the list stored at list2 (since it is the first non empty
     * list starting from the left).
     * <p>
     * <b>Blocking behavior</b>
     * <p>
     * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
     * client performs a LPUSH or an RPUSH operation against one of the lists.
     * <p>
     * Once new data is present on one of the lists, the client finally returns with the name of the
     * key unblocking it and the popped value.
     * <p>
     * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
     * special value if the specified amount of seconds passed without a push operation against at
     * least one of the specified keys.
     * <p>
     * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
     * block forever.
     * <p>
     * <b>Multiple clients blocking for the same keys</b>
     * <p>
     * Multiple clients can block for the same key. They are put into a queue, so the first to be
     * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
     * <p>
     * <b>blocking POP inside a MULTI/EXEC transaction</b>
     * <p>
     * BLPOP and BRPOP can be used with pipelining (sending multiple commands and reading the replies
     * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (a Redis
     * transaction).
     * <p>
     * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
     * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
     * it like if inside MULTI/EXEC the time will flow at infinite speed :)
     * <p>
     * Time complexity: O(1)
     *
     * @param timeout
     * @param keys
     * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
     * unblocking key and the popped value.
     * <p>
     * When a non-zero timeout is specified, and the BLPOP operation timed out, the return
     * value is a nil multi bulk reply. Most client values will return false or nil
     * accordingly to the programming language used.
     * @see #brpop(int, String...)
     */
    public List<String> blpop(int timeout, String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(timeout, keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from
     * high to low.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned.
     * The returned rank (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     *
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer
     * reply if the element exists. A nil bulk reply if there is no such element.
     * @see #zrank(byte[], byte[])
     */
    public Long zrevrank(byte[] key, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrank(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrevrange(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrange(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeWithScores(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeWithScores(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeWithScores(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the sorted set cardinality (number of elements). If the key does not exist 0 is
     * returned, like for empty sorted sets.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return the cardinality (number of elements) of the set as an integer.
     */
    public Long zcard(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.zcard(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the score of the specified element of the sorted set at key. If the specified element
     * does not exist in the sorted set, or the key does not exist at all, a special 'nil' value is
     * returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param member
     * @return the score
     */
    public Double zscore(byte[] key, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscore(key, member);
        } finally {Streams.safeClose(jedis);}
    }

    public Transaction multi() {
        Jedis jedis = getJedis();
        try {
            return jedis.multi();
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to its error prone
     * and will be removed on next major release
     * You can use multi() instead
     * @see https://github.com/xetorthio/jedis/pull/498
     */
    public List<Object> multi(TransactionBlock jedisTransaction) {
        Jedis jedis = getJedis();
        try {
            return jedis.multi(jedisTransaction);
        } finally {Streams.safeClose(jedis);}
    }

    public void connect() {
        jedis().connect();
    }

    public void disconnect() {
        jedis().disconnect();
    }

    public void resetState() {
        jedis().resetState();
    }

    public List<String> blpop(String... args) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(args);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> brpop(String... args) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(args);
        } finally {Streams.safeClose(jedis);}
    }

    public String watch(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.watch(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public String unwatch() {
        Jedis jedis = getJedis();
        try {
            return jedis.unwatch();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * @deprecated unusable command, this command will be removed in 3.0.0.
     */
    public List<String> blpop(String arg) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(arg);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List.
     * <p>
     * Sort the elements contained in the List, Set, or Sorted Set value at key. By default sorting is
     * numeric with elements being compared as double precision floating point numbers. This is the
     * simplest form of SORT.
     *
     * @param key
     * @return Assuming the Set/List at key contains a list of numbers, the return value will be the
     * list of numbers ordered from the smallest to the biggest number.
     * @see #sort(byte[], byte[])
     * @see #sort(byte[], SortingParams)
     * @see #sort(byte[], SortingParams, byte[])
     */
    public List<byte[]> sort(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * @deprecated unusable command, this command will be removed in 3.0.0.
     */
    public List<String> brpop(String arg) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(arg);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters and store the result at dstkey.
     *
     * @param key
     * @param sortingParameters
     * @param dstkey
     * @return The number of elements of the list at dstkey.
     * @see #sort(String, SortingParams)
     * @see #sort(String)
     * @see #sort(String, String)
     */
    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key, sortingParameters, dstkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters.
     * <p>
     * <b>examples:</b>
     * <p>
     * Given are the following sets and key/values:
     * <p>
     * <pre>
     * x = [1, 2, 3]
     * y = [a, b, c]
     *
     * k1 = z
     * k2 = y
     * k3 = x
     *
     * w1 = 9
     * w2 = 8
     * w3 = 7
     * </pre>
     * <p>
     * Sort Order:
     * <p>
     * <pre>
     * sort(x) or sort(x, sp.asc())
     * -&gt; [1, 2, 3]
     *
     * sort(x, sp.desc())
     * -&gt; [3, 2, 1]
     *
     * sort(y)
     * -&gt; [c, a, b]
     *
     * sort(y, sp.alpha())
     * -&gt; [a, b, c]
     *
     * sort(y, sp.alpha().desc())
     * -&gt; [c, a, b]
     * </pre>
     * <p>
     * Limit (e.g. for Pagination):
     * <p>
     * <pre>
     * sort(x, sp.limit(0, 2))
     * -&gt; [1, 2]
     *
     * sort(y, sp.alpha().desc().limit(1, 2))
     * -&gt; [b, a]
     * </pre>
     * <p>
     * Sorting by external keys:
     * <p>
     * <pre>
     * sort(x, sb.by(w*))
     * -&gt; [3, 2, 1]
     *
     * sort(x, sb.by(w*).desc())
     * -&gt; [1, 2, 3]
     * </pre>
     * <p>
     * Getting external keys:
     * <p>
     * <pre>
     * sort(x, sp.by(w*).get(k*))
     * -&gt; [x, y, z]
     *
     * sort(x, sp.by(w*).get(#).get(k*))
     * -&gt; [3, x, 2, y, 1, z]
     * </pre>
     *
     * @param key
     * @param sortingParameters
     * @return a list of sorted elements.
     * @see #sort(byte[])
     * @see #sort(byte[], SortingParams, byte[])
     */
    public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key, sortingParameters);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List and Store the Result at dstkey.
     * <p>
     * Sort the elements contained in the List, Set, or Sorted Set value at key and store the result
     * at dstkey. By default sorting is numeric with elements being compared as double precision
     * floating point numbers. This is the simplest form of SORT.
     *
     * @param key
     * @param dstkey
     * @return The number of elements of the list at dstkey.
     * @see #sort(String)
     * @see #sort(String, SortingParams)
     * @see #sort(String, SortingParams, String)
     */
    public Long sort(String key, String dstkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key, dstkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
     * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
     * lists.
     * <p>
     * The following is a description of the exact semantic. We describe BLPOP but the two commands
     * are identical, the only difference is that BLPOP pops the element from the left (head) of the
     * list, and BRPOP pops from the right (tail).
     * <p>
     * <b>Non blocking behavior</b>
     * <p>
     * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
     * element is popped from the head of the list and returned to the caller together with the name
     * of the key (BLPOP returns a two elements array, the first element is the key, the second the
     * popped value).
     * <p>
     * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
     * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
     * guarantees to return an element from the list stored at list2 (since it is the first non empty
     * list starting from the left).
     * <p>
     * <b>Blocking behavior</b>
     * <p>
     * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
     * client performs a LPUSH or an RPUSH operation against one of the lists.
     * <p>
     * Once new data is present on one of the lists, the client finally returns with the name of the
     * key unblocking it and the popped value.
     * <p>
     * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
     * special value if the specified amount of seconds passed without a push operation against at
     * least one of the specified keys.
     * <p>
     * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
     * block forever.
     * <p>
     * <b>Multiple clients blocking for the same keys</b>
     * <p>
     * Multiple clients can block for the same key. They are put into a queue, so the first to be
     * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
     * <p>
     * <b>blocking POP inside a MULTI/EXEC transaction</b>
     * <p>
     * BLPOP and BRPOP can be used with pipelining (sending multiple commands and reading the replies
     * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (a Redis
     * transaction).
     * <p>
     * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
     * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
     * it like if inside MULTI/EXEC the time will flow at infinite speed :)
     * <p>
     * Time complexity: O(1)
     *
     * @param timeout
     * @param keys
     * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
     * unblocking key and the popped value.
     * <p>
     * When a non-zero timeout is specified, and the BLPOP operation timed out, the return
     * value is a nil multi bulk reply. Most client values will return false or nil
     * accordingly to the programming language used.
     * @see #blpop(int, String...)
     */
    public List<String> brpop(int timeout, String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(timeout, keys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
     * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
     * lists.
     * <p>
     * The following is a description of the exact semantic. We describe BLPOP but the two commands
     * are identical, the only difference is that BLPOP pops the element from the left (head) of the
     * list, and BRPOP pops from the right (tail).
     * <p>
     * <b>Non blocking behavior</b>
     * <p>
     * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
     * element is popped from the head of the list and returned to the caller together with the name
     * of the key (BLPOP returns a two elements array, the first element is the key, the second the
     * popped value).
     * <p>
     * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
     * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
     * guarantees to return an element from the list stored at list2 (since it is the first non empty
     * list starting from the left).
     * <p>
     * <b>Blocking behavior</b>
     * <p>
     * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
     * client performs a LPUSH or an RPUSH operation against one of the lists.
     * <p>
     * Once new data is present on one of the lists, the client finally returns with the name of the
     * key unblocking it and the popped value.
     * <p>
     * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
     * special value if the specified amount of seconds passed without a push operation against at
     * least one of the specified keys.
     * <p>
     * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
     * block forever.
     * <p>
     * <b>Multiple clients blocking for the same keys</b>
     * <p>
     * Multiple clients can block for the same key. They are put into a queue, so the first to be
     * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
     * <p>
     * <b>blocking POP inside a MULTI/EXEC transaction</b>
     * <p>
     * BLPOP and BRPOP can be used with pipelining (sending multiple commands and reading the replies
     * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (a Redis
     * transaction).
     * <p>
     * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
     * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
     * it like if inside MULTI/EXEC the time will flow at infinite speed :)
     * <p>
     * Time complexity: O(1)
     *
     * @param timeout
     * @param keys
     * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
     * unblocking key and the popped value.
     * <p>
     * When a non-zero timeout is specified, and the BLPOP operation timed out, the return
     * value is a nil multi bulk reply. Most client values will return false or nil
     * accordingly to the programming language used.
     * @see #brpop(int, byte[]...)
     */
    public List<byte[]> blpop(int timeout, byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(timeout, keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zcount(String key, double min, double max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zcount(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zcount(String key, String min, String max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zcount(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min a double or Double.MIN_VALUE for "-inf"
     * @param max a double or Double.MAX_VALUE for "+inf"
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, String, String)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<String> zrangeByScore(String key, double min, double max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters and store the result at dstkey.
     *
     * @param key
     * @param sortingParameters
     * @param dstkey
     * @return The number of elements of the list at dstkey.
     * @see #sort(byte[], SortingParams)
     * @see #sort(byte[])
     * @see #sort(byte[], byte[])
     */
    public Long sort(byte[] key, SortingParams sortingParameters, byte[] dstkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key, sortingParameters, dstkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sort a Set or a List and Store the Result at dstkey.
     * <p>
     * Sort the elements contained in the List, Set, or Sorted Set value at key and store the result
     * at dstkey. By default sorting is numeric with elements being compared as double precision
     * floating point numbers. This is the simplest form of SORT.
     *
     * @param key
     * @param dstkey
     * @return The number of elements of the list at dstkey.
     * @see #sort(byte[])
     * @see #sort(byte[], SortingParams)
     * @see #sort(byte[], SortingParams, byte[])
     */
    public Long sort(byte[] key, byte[] dstkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.sort(key, dstkey);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
     * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
     * lists.
     * <p>
     * The following is a description of the exact semantic. We describe BLPOP but the two commands
     * are identical, the only difference is that BLPOP pops the element from the left (head) of the
     * list, and BRPOP pops from the right (tail).
     * <p>
     * <b>Non blocking behavior</b>
     * <p>
     * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
     * element is popped from the head of the list and returned to the caller together with the name
     * of the key (BLPOP returns a two elements array, the first element is the key, the second the
     * popped value).
     * <p>
     * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
     * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
     * guarantees to return an element from the list stored at list2 (since it is the first non empty
     * list starting from the left).
     * <p>
     * <b>Blocking behavior</b>
     * <p>
     * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
     * client performs a LPUSH or an RPUSH operation against one of the lists.
     * <p>
     * Once new data is present on one of the lists, the client finally returns with the name of the
     * key unblocking it and the popped value.
     * <p>
     * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
     * special value if the specified amount of seconds passed without a push operation against at
     * least one of the specified keys.
     * <p>
     * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
     * block forever.
     * <p>
     * <b>Multiple clients blocking for the same keys</b>
     * <p>
     * Multiple clients can block for the same key. They are put into a queue, so the first to be
     * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
     * <p>
     * <b>blocking POP inside a MULTI/EXEC transaction</b>
     * <p>
     * BLPOP and BRPOP can be used with pipelining (sending multiple commands and reading the replies
     * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (a Redis
     * transaction).
     * <p>
     * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
     * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
     * it like if inside MULTI/EXEC the time will flow at infinite speed :)
     * <p>
     * Time complexity: O(1)
     *
     * @param timeout
     * @param keys
     * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
     * unblocking key and the popped value.
     * <p>
     * When a non-zero timeout is specified, and the BLPOP operation timed out, the return
     * value is a nil multi bulk reply. Most client values will return false or nil
     * accordingly to the programming language used.
     * @see #blpop(int, byte[]...)
     */
    public List<byte[]> brpop(int timeout, byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(timeout, keys);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrangeByScore(String key, String min, String max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * @deprecated unusable command, this command will be removed in 3.0.0.
     */
    public List<byte[]> blpop(byte[] arg) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(arg);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * @deprecated unusable command, this command will be removed in 3.0.0.
     */
    public List<byte[]> brpop(byte[] arg) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(arg);
        } finally {Streams.safeClose(jedis);}
    }

    public List<byte[]> blpop(byte[]... args) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(args);
        } finally {Streams.safeClose(jedis);}
    }

    public List<byte[]> brpop(byte[]... args) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(args);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Request for authentication in a password protected Redis server. A Redis server can be
     * instructed to require a password before to allow clients to issue commands. This is done using
     * the requirepass directive in the Redis configuration file. If the password given by the client
     * is correct the server replies with an OK status code reply and starts accepting commands from
     * the client. Otherwise an error is returned and the clients needs to try a new password. Note
     * that for the high performance nature of Redis it is possible to try a lot of passwords in
     * parallel in very short time, so make sure to generate a strong and very long password so that
     * this attack is infeasible.
     *
     * @param password
     * @return Status code reply
     */
    public String auth(String password) {
        Jedis jedis = getJedis();
        try {
            return jedis.auth(password);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to its error prone with multi
     * and will be removed on next major release
     * You can use pipelined() instead
     *
     * @see https://github.com/xetorthio/jedis/pull/498
     */
    public List<Object> pipelined(PipelineBlock jedisPipeline) {
        throw Lang.noImplement();
    }

    @Deprecated
    public Pipeline pipelined() {
        throw Lang.noImplement();
    }

    public Long zcount(byte[] key, double min, double max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zcount(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zcount(byte[] key, byte[] min, byte[] max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zcount(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(byte[], double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(byte[], double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(byte[], double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(byte[], double, double)
     * @see #zrangeByScore(byte[], double, double, int, int)
     * @see #zrangeByScoreWithScores(byte[], double, double)
     * @see #zrangeByScoreWithScores(byte[], double, double, int, int)
     * @see #zcount(byte[], double, double)
     */
    public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<Tuple> zrangeByScoreWithScores(String key,
                                              double min,
                                              double max,
                                              int offset,
                                              int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(byte[], double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(byte[], double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(byte[], double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     * @see #zrangeByScore(byte[], double, double)
     * @see #zrangeByScore(byte[], double, double, int, int)
     * @see #zrangeByScoreWithScores(byte[], double, double)
     * @see #zrangeByScoreWithScores(byte[], double, double, int, int)
     * @see #zcount(byte[], double, double)
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    public Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrangeByScoreWithScores(String key,
                                              String min,
                                              String max,
                                              int offset,
                                              int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrangeByScore(String key, String max, String min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(byte[], double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(byte[], double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(byte[], double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(byte[], double, double)
     * @see #zrangeByScore(byte[], double, double, int, int)
     * @see #zrangeByScoreWithScores(byte[], double, double)
     * @see #zrangeByScoreWithScores(byte[], double, double, int, int)
     * @see #zcount(byte[], double, double)
     */
    public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key,
                                                 double max,
                                                 double min,
                                                 int offset,
                                                 int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key,
                                                 String max,
                                                 String min,
                                                 int offset,
                                                 int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove all elements in the sorted set at key with rank between start and end. Start and end are
     * 0-based with rank 0 being the element with the lowest score. Both start and end can be negative
     * numbers, where they indicate offsets starting at the element with the highest rank. For
     * example: -1 is the element with the highest score, -2 the element with the second highest score
     * and so forth.
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set
     * and M the number of elements removed by the operation
     */
    public Long zremrangeByRank(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByRank(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove all the elements in the sorted set at key with a score between min and max (including
     * elements with score equal to min or max).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements removed by the operation
     *
     * @param key
     * @param start
     * @param end
     * @return Integer reply, specifically the number of elements removed.
     */
    public Long zremrangeByScore(String key, double start, double end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByScore(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this
     * follows from a property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(byte[], double, double, int, int) LIMIT} it's possible
     * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
     * the commands needs to traverse the list for offset elements and this adds up to the O(M)
     * figure.
     * <p>
     * The {@link #zcount(byte[], double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(byte[], double, double) ZRANGEBYSCORE} but instead of returning the
     * actual elements in the specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
     * smallest element in order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open
     * intervals prefixing the score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score &gt; 5 and &lt; 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(log(N))
     *
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(byte[], double, double)
     * @see #zrangeByScore(byte[], double, double, int, int)
     * @see #zrangeByScoreWithScores(byte[], double, double)
     * @see #zrangeByScoreWithScores(byte[], double, double, int, int)
     * @see #zcount(byte[], double, double)
     */
    public Set<Tuple> zrangeByScoreWithScores(byte[] key,
                                              double min,
                                              double max,
                                              int offset,
                                              int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zremrangeByScore(String key, String start, String end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByScore(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(String, String...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The
     * {@link #zunionstore(String, String...) ZUNIONSTORE} command inserts all elements across all
     * inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zunionstore(String dstkey, String... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zunionstore(dstkey, sets);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(String, String...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The
     * {@link #zunionstore(String, String...) ZUNIONSTORE} command inserts all elements across all
     * inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @param params
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zunionstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zunionstore(dstkey, params, sets);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrangeByScoreWithScores(byte[] key,
                                              byte[] min,
                                              byte[] max,
                                              int offset,
                                              int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(String, String...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The
     * {@link #zunionstore(String, String...) ZUNIONSTORE} command inserts all elements across all
     * inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zinterstore(String dstkey, String... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zinterstore(dstkey, sets);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key,
                                                 double max,
                                                 double min,
                                                 int offset,
                                                 int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<Tuple> zrevrangeByScoreWithScores(byte[] key,
                                                 byte[] max,
                                                 byte[] min,
                                                 int offset,
                                                 int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove all elements in the sorted set at key with rank between start and end. Start and end are
     * 0-based with rank 0 being the element with the lowest score. Both start and end can be negative
     * numbers, where they indicate offsets starting at the element with the highest rank. For
     * example: -1 is the element with the highest score, -2 the element with the second highest score
     * and so forth.
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set
     * and M the number of elements removed by the operation
     */
    public Long zremrangeByRank(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByRank(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Remove all the elements in the sorted set at key with a score between min and max (including
     * elements with score equal to min or max).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements removed by the operation
     *
     * @param key
     * @param start
     * @param end
     * @return Integer reply, specifically the number of elements removed.
     */
    public Long zremrangeByScore(byte[] key, double start, double end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByScore(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(String, String...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The
     * {@link #zunionstore(String, String...) ZUNIONSTORE} command inserts all elements across all
     * inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @param params
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zinterstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zinterstore(dstkey, params, sets);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zremrangeByScore(byte[] key, byte[] start, byte[] end) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByScore(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(byte[], byte[]...)} ZINTERSTORE} command requires
     * an element to be present in each of the given inputs to be inserted in the result. The {@link
     * #zunionstore(byte[], byte[]...)} command inserts all elements across all inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(byte[], byte[]...)
     * @see #zunionstore(byte[], ZParams, byte[]...)
     * @see #zinterstore(byte[], byte[]...)
     * @see #zinterstore(byte[], ZParams, byte[]...)
     */
    public Long zunionstore(byte[] dstkey, byte[]... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zunionstore(dstkey, sets);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zlexcount(String key, String min, String max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zlexcount(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrangeByLex(String key, String min, String max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByLex(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByLex(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(byte[], byte[]...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The {@link
     * #zunionstore(byte[], byte[]...) ZUNIONSTORE} command inserts all elements across all inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @param params
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(byte[], byte[]...)
     * @see #zunionstore(byte[], ZParams, byte[]...)
     * @see #zinterstore(byte[], byte[]...)
     * @see #zinterstore(byte[], ZParams, byte[]...)
     */
    public Long zunionstore(byte[] dstkey, ZParams params, byte[]... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zunionstore(dstkey, params, sets);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrangeByLex(String key, String max, String min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByLex(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByLex(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zremrangeByLex(String key, String min, String max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByLex(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Long strlen(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.strlen(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long lpushx(String key, String... string) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpushx(key, string);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Undo a {@link #expire(String, int) expire} at turning the expire key into a normal key.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Integer reply, specifically: 1: the key is now persist. 0: the key is not persist (only
     * happens when key not set).
     */
    public Long persist(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.persist(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long rpushx(String key, String... string) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpushx(key, string);
        } finally {Streams.safeClose(jedis);}
    }

    public String echo(String string) {
        Jedis jedis = getJedis();
        try {
            return jedis.echo(string);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(byte[], byte[]...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The {@link
     * #zunionstore(byte[], byte[]...) ZUNIONSTORE} command inserts all elements across all inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(byte[], byte[]...)
     * @see #zunionstore(byte[], ZParams, byte[]...)
     * @see #zinterstore(byte[], byte[]...)
     * @see #zinterstore(byte[], ZParams, byte[]...)
     */
    public Long zinterstore(byte[] dstkey, byte[]... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zinterstore(dstkey, sets);
        } finally {Streams.safeClose(jedis);}
    }

    public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.linsert(key, where, pivot, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Pop a value from a list, push it to another list and return it; or block until one is available
     *
     * @param source
     * @param destination
     * @param timeout
     * @return the element
     */
    public String brpoplpush(String source, String destination, int timeout) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpoplpush(source, destination, timeout);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sets or clears the bit at offset in the string value stored at key
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    public Boolean setbit(String key, long offset, boolean value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setbit(key, offset, value);
        } finally {Streams.safeClose(jedis);}
    }

    public Boolean setbit(String key, long offset, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setbit(key, offset, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Returns the bit value at offset in the string value stored at key
     *
     * @param key
     * @param offset
     * @return
     */
    public Boolean getbit(String key, long offset) {
        Jedis jedis = getJedis();
        try {
            return jedis.getbit(key, offset);
        } finally {Streams.safeClose(jedis);}
    }

    public Long setrange(String key, long offset, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setrange(key, offset, value);
        } finally {Streams.safeClose(jedis);}
    }

    public String getrange(String key, long startOffset, long endOffset) {
        Jedis jedis = getJedis();
        try {
            return jedis.getrange(key, startOffset, endOffset);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitpos(String key, boolean value) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitpos(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitpos(String key, boolean value, BitPosParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitpos(key, value, params);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
     * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
     * and the other (optional) arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(byte[], byte[]...) ZINTERSTORE} command requires an
     * element to be present in each of the given inputs to be inserted in the result. The {@link
     * #zunionstore(byte[], byte[]...) ZUNIONSTORE} command inserts all elements across all inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
     * that the score of each element in the sorted set is first multiplied by this weight before
     * being passed to the aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of the union or
     * intersection are aggregated. This option defaults to SUM, where the score of an element is
     * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
     * resulting set will contain the minimum or maximum score of an element across the inputs where
     * it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the sizes of the input
     * sorted sets, and M being the number of elements in the resulting sorted set
     *
     * @param dstkey
     * @param sets
     * @param params
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(byte[], byte[]...)
     * @see #zunionstore(byte[], ZParams, byte[]...)
     * @see #zinterstore(byte[], byte[]...)
     * @see #zinterstore(byte[], ZParams, byte[]...)
     */
    public Long zinterstore(byte[] dstkey, ZParams params, byte[]... sets) {
        Jedis jedis = getJedis();
        try {
            return jedis.zinterstore(dstkey, params, sets);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Retrieve the configuration of a running Redis server. Not all the configuration parameters are
     * supported.
     * <p>
     * CONFIG GET returns the current configuration parameters. This sub command only accepts a single
     * argument, that is glob style pattern. All the configuration parameters matching this parameter
     * are reported as a list of key-value pairs.
     * <p>
     * <b>Example:</b>
     * <p>
     * <pre>
     * $ redis-cli config get '*'
     * 1. "dbfilename"
     * 2. "dump.rdb"
     * 3. "requirepass"
     * 4. (nil)
     * 5. "masterauth"
     * 6. (nil)
     * 7. "maxmemory"
     * 8. "0\n"
     * 9. "appendfsync"
     * 10. "everysec"
     * 11. "save"
     * 12. "3600 1 300 100 60 10000"
     *
     * $ redis-cli config get 'm*'
     * 1. "masterauth"
     * 2. (nil)
     * 3. "maxmemory"
     * 4. "0\n"
     * </pre>
     *
     * @param pattern
     * @return Bulk reply.
     */
    public List<String> configGet(String pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.configGet(pattern);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Alter the configuration of a running Redis server. Not all the configuration parameters are
     * supported.
     * <p>
     * The list of configuration parameters supported by CONFIG SET can be obtained issuing a
     * {@link #configGet(String) CONFIG GET *} command.
     * <p>
     * The configuration set using CONFIG SET is immediately loaded by the Redis server that will
     * start acting as specified starting from the next command.
     * <p>
     * <b>Parameters value format</b>
     * <p>
     * The value of the configuration parameter is the same as the one of the same parameter in the
     * Redis configuration file, with the following exceptions:
     * <p>
     * <ul>
     * <li>The save paramter is a list of space-separated integers. Every pair of integers specify the
     * time and number of changes limit to trigger a save. For instance the command CONFIG SET save
     * "3600 10 60 10000" will configure the server to issue a background saving of the RDB file every
     * 3600 seconds if there are at least 10 changes in the dataset, and every 60 seconds if there are
     * at least 10000 changes. To completely disable automatic snapshots just set the parameter as an
     * empty string.
     * <li>All the integer parameters representing memory are returned and accepted only using bytes
     * as unit.
     * </ul>
     *
     * @param parameter
     * @param value
     * @return Status code reply
     */
    public String configSet(String parameter, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.configSet(parameter, value);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zlexcount(byte[] key, byte[] min, byte[] max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zlexcount(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByLex(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrangeByLex(key, min, max, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Object eval(String script, int keyCount, String... params) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script, keyCount, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByLex(key, max, min);
        } finally {Streams.safeClose(jedis);}
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        jedis().subscribe(jedisPubSub, channels);
    }

    public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrevrangeByLex(key, max, min, offset, count);
        } finally {Streams.safeClose(jedis);}
    }

    public Long publish(String channel, String message) {
        Jedis jedis = getJedis();
        try {
            return jedis.publish(channel, message);
        } finally {Streams.safeClose(jedis);}
    }

    public Long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
        Jedis jedis = getJedis();
        try {
            return jedis.zremrangeByLex(key, min, max);
        } finally {Streams.safeClose(jedis);}
    }

    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        jedis().psubscribe(jedisPubSub, patterns);
    }

    /**
     * Synchronously save the DB on disk.
     * <p>
     * Save the whole dataset on disk (this means that all the databases are saved, as well as keys
     * with an EXPIRE set (the expire is preserved). The server hangs while the saving is not
     * completed, no connection is served in the meanwhile. An OK code is returned when the DB was
     * fully stored in disk.
     * <p>
     * The background variant of this command is {@link #bgsave() BGSAVE} that is able to perform the
     * saving in the background while the server continues serving other clients.
     * <p>
     *
     * @return Status code reply
     */
    public String save() {
        Jedis jedis = getJedis();
        try {
            return jedis.save();
        } finally {Streams.safeClose(jedis);}
    }

    public Object eval(String script, List<String> keys, List<String> args) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script, keys, args);
        } finally {Streams.safeClose(jedis);}
    }

    public Object eval(String script) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Asynchronously save the DB on disk.
     * <p>
     * Save the DB in background. The OK code is immediately returned. Redis forks, the parent
     * continues to server the clients, the child saves the DB on disk then exit. A client my be able
     * to check if the operation succeeded using the LASTSAVE command.
     *
     * @return Status code reply
     */
    public String bgsave() {
        Jedis jedis = getJedis();
        try {
            return jedis.bgsave();
        } finally {Streams.safeClose(jedis);}
    }

    public Object evalsha(String script) {
        Jedis jedis = getJedis();
        try {
            return jedis.evalsha(script);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Rewrite the append only file in background when it gets too big. Please for detailed
     * information about the Redis Append Only File check the <a
     * href="http://code.google.com/p/redis/wiki/AppendOnlyFileHowto">Append Only File Howto</a>.
     * <p>
     * BGREWRITEAOF rewrites the Append Only File in background when it gets too big. The Redis Append
     * Only File is a Journal, so every operation modifying the dataset is logged in the Append Only
     * File (and replayed at startup). This means that the Append Only File always grows. In order to
     * rebuild its content the BGREWRITEAOF creates a new version of the append only file starting
     * directly form the dataset in memory in order to guarantee the generation of the minimal number
     * of commands needed to rebuild the database.
     * <p>
     *
     * @return Status code reply
     */
    public String bgrewriteaof() {
        Jedis jedis = getJedis();
        try {
            return jedis.bgrewriteaof();
        } finally {Streams.safeClose(jedis);}
    }

    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        Jedis jedis = getJedis();
        try {
            return jedis.evalsha(sha1, keys, args);
        } finally {Streams.safeClose(jedis);}
    }

    public Object evalsha(String sha1, int keyCount, String... params) {
        Jedis jedis = getJedis();
        try {
            return jedis.evalsha(sha1, keyCount, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Boolean scriptExists(String sha1) {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptExists(sha1);
        } finally {Streams.safeClose(jedis);}
    }

    public List<Boolean> scriptExists(String... sha1) {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptExists(sha1);
        } finally {Streams.safeClose(jedis);}
    }

    public String scriptLoad(String script) {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptLoad(script);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Return the UNIX time stamp of the last successfully saving of the dataset on disk.
     * <p>
     * Return the UNIX TIME of the last DB save executed with success. A client may check if a
     * {@link #bgsave() BGSAVE} command succeeded reading the LASTSAVE value, then issuing a BGSAVE
     * command and checking at regular intervals every N seconds if LASTSAVE changed.
     *
     * @return Integer reply, specifically an UNIX time stamp.
     */
    public Long lastsave() {
        Jedis jedis = getJedis();
        try {
            return jedis.lastsave();
        } finally {Streams.safeClose(jedis);}
    }

    public List<Slowlog> slowlogGet() {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogGet();
        } finally {Streams.safeClose(jedis);}
    }

    public List<Slowlog> slowlogGet(long entries) {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogGet(entries);
        } finally {Streams.safeClose(jedis);}
    }

    public Long objectRefcount(String string) {
        Jedis jedis = getJedis();
        try {
            return jedis.objectRefcount(string);
        } finally {Streams.safeClose(jedis);}
    }

    public String objectEncoding(String string) {
        Jedis jedis = getJedis();
        try {
            return jedis.objectEncoding(string);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Synchronously save the DB on disk, then shutdown the server.
     * <p>
     * Stop all the clients, save the DB, then quit the server. This commands makes sure that the DB
     * is switched off without the lost of any data. This is not guaranteed if the client uses simply
     * {@link #save() SAVE} and then {@link #quit() QUIT} because other clients may alter the DB data
     * between the two commands.
     *
     * @return Status code reply on error. On success nothing is returned since the server quits and
     * the connection is closed.
     */
    public String shutdown() {
        Jedis jedis = getJedis();
        try {
            return jedis.shutdown();
        } finally {Streams.safeClose(jedis);}
    }

    public Long objectIdletime(String string) {
        Jedis jedis = getJedis();
        try {
            return jedis.objectIdletime(string);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitcount(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitcount(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitcount(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitcount(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitop(BitOP op, String destKey, String... srcKeys) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitop(op, destKey, srcKeys);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * <pre>
     * redis 127.0.0.1:26381&gt; sentinel masters
     * 1)  1) "name"
     *     2) "mymaster"
     *     3) "ip"
     *     4) "127.0.0.1"
     *     5) "port"
     *     6) "6379"
     *     7) "runid"
     *     8) "93d4d4e6e9c06d0eea36e27f31924ac26576081d"
     *     9) "flags"
     *    10) "master"
     *    11) "pending-commands"
     *    12) "0"
     *    13) "last-ok-ping-reply"
     *    14) "423"
     *    15) "last-ping-reply"
     *    16) "423"
     *    17) "info-refresh"
     *    18) "6107"
     *    19) "num-slaves"
     *    20) "1"
     *    21) "num-other-sentinels"
     *    22) "2"
     *    23) "quorum"
     *    24) "2"
     *
     * </pre>
     *
     * @return
     */
    public List<Map<String, String>> sentinelMasters() {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelMasters();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Provide information and statistics about the server.
     * <p>
     * The info command returns different information and statistics about the server in an format
     * that's simple to parse by computers and easy to read by humans.
     * <p>
     * <b>Format of the returned String:</b>
     * <p>
     * All the fields are in the form field:value
     * <p>
     * <pre>
     * edis_version:0.07
     * connected_clients:1
     * connected_slaves:0
     * used_memory:3187
     * changes_since_last_save:0
     * last_save_time:1237655729
     * total_connections_received:1
     * total_commands_processed:1
     * uptime_in_seconds:25
     * uptime_in_days:0
     * </pre>
     * <p>
     * <b>Notes</b>
     * <p>
     * used_memory is returned in bytes, and is the total number of bytes allocated by the program
     * using malloc.
     * <p>
     * uptime_in_days is redundant since the uptime in seconds contains already the full uptime
     * information, this field is only mainly present for humans.
     * <p>
     * changes_since_last_save does not refer to the number of key changes, but to the number of
     * operations that produced some kind of change in the dataset.
     * <p>
     *
     * @return Bulk reply
     */
    public String info() {
        Jedis jedis = getJedis();
        try {
            return jedis.info();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * <pre>
     * redis 127.0.0.1:26381&gt; sentinel get-master-addr-by-name mymaster
     * 1) "127.0.0.1"
     * 2) "6379"
     * </pre>
     *
     * @param masterName
     * @return two elements list of strings : host and port.
     */
    public List<String> sentinelGetMasterAddrByName(String masterName) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelGetMasterAddrByName(masterName);
        } finally {Streams.safeClose(jedis);}
    }

    public String info(String section) {
        Jedis jedis = getJedis();
        try {
            return jedis.info(section);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * <pre>
     * redis 127.0.0.1:26381&gt; sentinel reset mymaster
     * (integer) 1
     * </pre>
     *
     * @param pattern
     * @return
     */
    public Long sentinelReset(String pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelReset(pattern);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Dump all the received requests in real time.
     * <p>
     * MONITOR is a debugging command that outputs the whole sequence of commands received by the
     * Redis server. is very handy in order to understand what is happening into the database. This
     * command is used directly via telnet.
     *
     * @param jedisMonitor
     */
    public void monitor(JedisMonitor jedisMonitor) {
        jedis().monitor(jedisMonitor);
    }

    /**
     * <pre>
     * redis 127.0.0.1:26381&gt; sentinel slaves mymaster
     * 1)  1) "name"
     *     2) "127.0.0.1:6380"
     *     3) "ip"
     *     4) "127.0.0.1"
     *     5) "port"
     *     6) "6380"
     *     7) "runid"
     *     8) "d7f6c0ca7572df9d2f33713df0dbf8c72da7c039"
     *     9) "flags"
     *    10) "slave"
     *    11) "pending-commands"
     *    12) "0"
     *    13) "last-ok-ping-reply"
     *    14) "47"
     *    15) "last-ping-reply"
     *    16) "47"
     *    17) "info-refresh"
     *    18) "657"
     *    19) "master-link-down-time"
     *    20) "0"
     *    21) "master-link-status"
     *    22) "ok"
     *    23) "master-host"
     *    24) "localhost"
     *    25) "master-port"
     *    26) "6379"
     *    27) "slave-priority"
     *    28) "100"
     * </pre>
     *
     * @param masterName
     * @return
     */
    public List<Map<String, String>> sentinelSlaves(String masterName) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelSlaves(masterName);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Change the replication settings.
     * <p>
     * The SLAVEOF command can change the replication settings of a slave on the fly. If a Redis
     * server is arleady acting as slave, the command SLAVEOF NO ONE will turn off the replicaiton
     * turning the Redis server into a MASTER. In the proper form SLAVEOF hostname port will make the
     * server a slave of the specific server listening at the specified hostname and port.
     * <p>
     * If a server is already a slave of some master, SLAVEOF hostname port will stop the replication
     * against the old server and start the synchrnonization against the new one discarding the old
     * dataset.
     * <p>
     * The form SLAVEOF no one will stop replication turning the server into a MASTER but will not
     * discard the replication. So if the old master stop working it is possible to turn the slave
     * into a master and set the application to use the new master in read/write. Later when the other
     * Redis server will be fixed it can be configured in order to work as slave.
     * <p>
     *
     * @param host
     * @param port
     * @return Status code reply
     */
    public String slaveof(String host, int port) {
        Jedis jedis = getJedis();
        try {
            return jedis.slaveof(host, port);
        } finally {Streams.safeClose(jedis);}
    }

    public String sentinelFailover(String masterName) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelFailover(masterName);
        } finally {Streams.safeClose(jedis);}
    }

    public String sentinelMonitor(String masterName, String ip, int port, int quorum) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelMonitor(masterName, ip, port, quorum);
        } finally {Streams.safeClose(jedis);}
    }

    public String slaveofNoOne() {
        Jedis jedis = getJedis();
        try {
            return jedis.slaveofNoOne();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Retrieve the configuration of a running Redis server. Not all the configuration parameters are
     * supported.
     * <p>
     * CONFIG GET returns the current configuration parameters. This sub command only accepts a single
     * argument, that is glob style pattern. All the configuration parameters matching this parameter
     * are reported as a list of key-value pairs.
     * <p>
     * <b>Example:</b>
     * <p>
     * <pre>
     * $ redis-cli config get '*'
     * 1. "dbfilename"
     * 2. "dump.rdb"
     * 3. "requirepass"
     * 4. (nil)
     * 5. "masterauth"
     * 6. (nil)
     * 7. "maxmemory"
     * 8. "0\n"
     * 9. "appendfsync"
     * 10. "everysec"
     * 11. "save"
     * 12. "3600 1 300 100 60 10000"
     *
     * $ redis-cli config get 'm*'
     * 1. "masterauth"
     * 2. (nil)
     * 3. "maxmemory"
     * 4. "0\n"
     * </pre>
     *
     * @param pattern
     * @return Bulk reply.
     */
    public List<byte[]> configGet(byte[] pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.configGet(pattern);
        } finally {Streams.safeClose(jedis);}
    }

    public String sentinelRemove(String masterName) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelRemove(masterName);
        } finally {Streams.safeClose(jedis);}
    }

    public String sentinelSet(String masterName, Map<String, String> parameterMap) {
        Jedis jedis = getJedis();
        try {
            return jedis.sentinelSet(masterName, parameterMap);
        } finally {Streams.safeClose(jedis);}
    }

    public byte[] dump(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.dump(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String restore(String key, int ttl, byte[] serializedValue) {
        Jedis jedis = getJedis();
        try {
            return jedis.restore(key, ttl, serializedValue);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Reset the stats returned by INFO
     *
     * @return
     */
    public String configResetStat() {
        Jedis jedis = getJedis();
        try {
            return jedis.configResetStat();
        } finally {Streams.safeClose(jedis);}
    }

    public Long pexpire(String key, int milliseconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.pexpire(key, milliseconds);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Alter the configuration of a running Redis server. Not all the configuration parameters are
     * supported.
     * <p>
     * The list of configuration parameters supported by CONFIG SET can be obtained issuing a
     * {@link #configGet(byte[]) CONFIG GET *} command.
     * <p>
     * The configuration set using CONFIG SET is immediately loaded by the Redis server that will
     * start acting as specified starting from the next command.
     * <p>
     * <b>Parameters value format</b>
     * <p>
     * The value of the configuration parameter is the same as the one of the same parameter in the
     * Redis configuration file, with the following exceptions:
     * <p>
     * <ul>
     * <li>The save paramter is a list of space-separated integers. Every pair of integers specify the
     * time and number of changes limit to trigger a save. For instance the command CONFIG SET save
     * "3600 10 60 10000" will configure the server to issue a background saving of the RDB file every
     * 3600 seconds if there are at least 10 changes in the dataset, and every 60 seconds if there are
     * at least 10000 changes. To completely disable automatic snapshots just set the parameter as an
     * empty string.
     * <li>All the integer parameters representing memory are returned and accepted only using bytes
     * as unit.
     * </ul>
     *
     * @param parameter
     * @param value
     * @return Status code reply
     */
    public byte[] configSet(byte[] parameter, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.configSet(parameter, value);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pexpire(String key, long milliseconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.pexpire(key, milliseconds);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pexpireAt(String key, long millisecondsTimestamp) {
        Jedis jedis = getJedis();
        try {
            return jedis.pexpireAt(key, millisecondsTimestamp);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pttl(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.pttl(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String psetex(String key, int milliseconds, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.psetex(key, milliseconds, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * PSETEX works exactly like {@link #setex(String, int, String)} with the sole difference that the
     * expire time is specified in milliseconds instead of seconds. Time complexity: O(1)
     *
     * @param key
     * @param milliseconds
     * @param value
     * @return Status code reply
     */
    public String psetex(String key, long milliseconds, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.psetex(key, milliseconds, value);
        } finally {Streams.safeClose(jedis);}
    }

    public String set(String key, String value, String nxxx) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value, nxxx);
        } finally {Streams.safeClose(jedis);}
    }

    public String set(String key, String value, String nxxx, String expx, int time) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value, nxxx, expx, time);
        } finally {Streams.safeClose(jedis);}
    }

    public boolean isConnected() {
        Jedis jedis = getJedis();
        try {
            return jedis.isConnected();
        } finally {Streams.safeClose(jedis);}
    }

    public Long strlen(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.strlen(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String clientKill(String client) {
        Jedis jedis = getJedis();
        try {
            return jedis.clientKill(client);
        } finally {Streams.safeClose(jedis);}
    }

    public void sync() {
        jedis().sync();
    }

    public Long lpushx(byte[] key, byte[]... string) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpushx(key, string);
        } finally {Streams.safeClose(jedis);}
    }

    public String clientSetname(String name) {
        Jedis jedis = getJedis();
        try {
            return jedis.clientSetname(name);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Undo a {@link #expire(byte[], int) expire} at turning the expire key into a normal key.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Integer reply, specifically: 1: the key is now persist. 0: the key is not persist (only
     * happens when key not set).
     */
    public Long persist(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.persist(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String migrate(String host, int port, String key, int destinationDb, int timeout) {
        Jedis jedis = getJedis();
        try {
            return jedis.migrate(host, port, key, destinationDb, timeout);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<String> scan(int cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public Long rpushx(byte[] key, byte[]... string) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpushx(key, string);
        } finally {Streams.safeClose(jedis);}
    }

    public byte[] echo(byte[] string) {
        Jedis jedis = getJedis();
        try {
            return jedis.echo(string);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<String> scan(int cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.linsert(key, where, pivot, value);
        } finally {Streams.safeClose(jedis);}
    }

    public String debug(DebugParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.debug(params);
        } finally {Streams.safeClose(jedis);}
    }

    public Client getClient() {
        Jedis jedis = getJedis();
        try {
            return jedis.getClient();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Pop a value from a list, push it to another list and return it; or block until one is available
     *
     * @param source
     * @param destination
     * @param timeout
     * @return the element
     */
    public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpoplpush(source, destination, timeout);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.hscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Sets or clears the bit at offset in the string value stored at key
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    public Boolean setbit(byte[] key, long offset, boolean value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setbit(key, offset, value);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<Entry<String, String>> hscan(String key, int cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.hscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Boolean setbit(byte[] key, long offset, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setbit(key, offset, value);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Returns the bit value at offset in the string value stored at key
     *
     * @param key
     * @param offset
     * @return
     */
    public Boolean getbit(byte[] key, long offset) {
        Jedis jedis = getJedis();
        try {
            return jedis.getbit(key, offset);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitpos(byte[] key, boolean value) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitpos(key, value);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitpos(byte[] key, boolean value, BitPosParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitpos(key, value, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long setrange(byte[] key, long offset, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.setrange(key, offset, value);
        } finally {Streams.safeClose(jedis);}
    }

    public byte[] getrange(byte[] key, long startOffset, long endOffset) {
        Jedis jedis = getJedis();
        try {
            return jedis.getrange(key, startOffset, endOffset);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<String> sscan(String key, int cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.sscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public Long publish(byte[] channel, byte[] message) {
        Jedis jedis = getJedis();
        try {
            return jedis.publish(channel, message);
        } finally {Streams.safeClose(jedis);}
    }

    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        jedis().subscribe(jedisPubSub, channels);
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<String> sscan(String key, int cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.sscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
        jedis().psubscribe(jedisPubSub, patterns);
    }

    public Long getDB() {
        Jedis jedis = getJedis();
        try {
            return jedis.getDB();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Evaluates scripts using the Lua interpreter built into Redis starting from version 2.6.0.
     * <p>
     *
     * @return Script result
     */
    public Object eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script, keys, args);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<Tuple> zscan(String key, int cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    public ScanResult<Tuple> zscan(String key, int cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Object eval(byte[] script, byte[] keyCount, byte[]... params) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script, keyCount, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Object eval(byte[] script, int keyCount, byte[]... params) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script, keyCount, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Object eval(byte[] script) {
        Jedis jedis = getJedis();
        try {
            return jedis.eval(script);
        } finally {Streams.safeClose(jedis);}
    }

    public Object evalsha(byte[] sha1) {
        Jedis jedis = getJedis();
        try {
            return jedis.evalsha(sha1);
        } finally {Streams.safeClose(jedis);}
    }

    public Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
        Jedis jedis = getJedis();
        try {
            return jedis.evalsha(sha1, keys, args);
        } finally {Streams.safeClose(jedis);}
    }

    public Object evalsha(byte[] sha1, int keyCount, byte[]... params) {
        Jedis jedis = getJedis();
        try {
            return jedis.evalsha(sha1, keyCount, params);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<String> scan(String cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public String scriptFlush() {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptFlush();
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<String> scan(String cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long scriptExists(byte[] sha1) {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptExists(sha1);
        } finally {Streams.safeClose(jedis);}
    }

    public List<Long> scriptExists(byte[]... sha1) {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptExists(sha1);
        } finally {Streams.safeClose(jedis);}
    }

    public byte[] scriptLoad(byte[] script) {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptLoad(script);
        } finally {Streams.safeClose(jedis);}
    }

    public String scriptKill() {
        Jedis jedis = getJedis();
        try {
            return jedis.scriptKill();
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.hscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public String slowlogReset() {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogReset();
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.hscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long slowlogLen() {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogLen();
        } finally {Streams.safeClose(jedis);}
    }

    public List<byte[]> slowlogGetBinary() {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogGetBinary();
        } finally {Streams.safeClose(jedis);}
    }

    public List<byte[]> slowlogGetBinary(long entries) {
        Jedis jedis = getJedis();
        try {
            return jedis.slowlogGetBinary(entries);
        } finally {Streams.safeClose(jedis);}
    }

    public Long objectRefcount(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.objectRefcount(key);
        } finally {Streams.safeClose(jedis);}
    }

    public byte[] objectEncoding(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.objectEncoding(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long objectIdletime(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.objectIdletime(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitcount(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitcount(key);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<String> sscan(String key, String cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.sscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitcount(byte[] key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitcount(key, start, end);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.sscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long bitop(BitOP op, byte[] destKey, byte[]... srcKeys) {
        Jedis jedis = getJedis();
        try {
            return jedis.bitop(op, destKey, srcKeys);
        } finally {Streams.safeClose(jedis);}
    }

    public byte[] dump(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.dump(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String restore(byte[] key, int ttl, byte[] serializedValue) {
        Jedis jedis = getJedis();
        try {
            return jedis.restore(key, ttl, serializedValue);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Tuple> zscan(String key, String cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pexpire(byte[] key, int milliseconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.pexpire(key, milliseconds);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pexpire(byte[] key, long milliseconds) {
        Jedis jedis = getJedis();
        try {
            return jedis.pexpire(key, milliseconds);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pexpireAt(byte[] key, long millisecondsTimestamp) {
        Jedis jedis = getJedis();
        try {
            return jedis.pexpireAt(key, millisecondsTimestamp);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pttl(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.pttl(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String psetex(byte[] key, int milliseconds, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.psetex(key, milliseconds, value);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterNodes() {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterNodes();
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * PSETEX works exactly like {@link #setex(byte[], int, byte[])} with the sole difference that the
     * expire time is specified in milliseconds instead of seconds. Time complexity: O(1)
     *
     * @param key
     * @param milliseconds
     * @param value
     * @return Status code reply
     */
    public String psetex(byte[] key, long milliseconds, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.psetex(key, milliseconds, value);
        } finally {Streams.safeClose(jedis);}
    }

    public String readonly() {
        Jedis jedis = getJedis();
        try {
            return jedis.readonly();
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterMeet(String ip, int port) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterMeet(ip, port);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterReset(Reset resetType) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterReset(resetType);
        } finally {Streams.safeClose(jedis);}
    }

    public String set(byte[] key, byte[] value, byte[] nxxx) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value, nxxx);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterAddSlots(int... slots) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterAddSlots(slots);
        } finally {Streams.safeClose(jedis);}
    }

    public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, int time) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, value, nxxx, expx, time);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterDelSlots(int... slots) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterDelSlots(slots);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterInfo() {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterInfo();
        } finally {Streams.safeClose(jedis);}
    }

    public String clientKill(byte[] client) {
        Jedis jedis = getJedis();
        try {
            return jedis.clientKill(client);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> clusterGetKeysInSlot(int slot, int count) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterGetKeysInSlot(slot, count);
        } finally {Streams.safeClose(jedis);}
    }

    public String clientGetname() {
        Jedis jedis = getJedis();
        try {
            return jedis.clientGetname();
        } finally {Streams.safeClose(jedis);}
    }

    public String clientList() {
        Jedis jedis = getJedis();
        try {
            return jedis.clientList();
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterSetSlotNode(int slot, String nodeId) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSetSlotNode(slot, nodeId);
        } finally {Streams.safeClose(jedis);}
    }

    public String clientSetname(byte[] name) {
        Jedis jedis = getJedis();
        try {
            return jedis.clientSetname(name);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterSetSlotMigrating(int slot, String nodeId) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSetSlotMigrating(slot, nodeId);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> time() {
        Jedis jedis = getJedis();
        try {
            return jedis.time();
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterSetSlotImporting(int slot, String nodeId) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSetSlotImporting(slot, nodeId);
        } finally {Streams.safeClose(jedis);}
    }

    public String migrate(byte[] host, int port, byte[] key, int destinationDb, int timeout) {
        Jedis jedis = getJedis();
        try {
            return jedis.migrate(host, port, key, destinationDb, timeout);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterSetSlotStable(int slot) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSetSlotStable(slot);
        } finally {Streams.safeClose(jedis);}
    }

    /**
     * Syncrhonous replication of Redis as described here: http://antirez.com/news/66 Since Java
     * Object class has implemented "wait" method, we cannot use it, so I had to change the name of
     * the method. Sorry :S
     */
    public Long waitReplicas(int replicas, long timeout) {
        Jedis jedis = getJedis();
        try {
            return jedis.waitReplicas(replicas, timeout);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterForget(String nodeId) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterForget(nodeId);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterFlushSlots() {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterFlushSlots();
        } finally {Streams.safeClose(jedis);}
    }

    public Long pfadd(byte[] key, byte[]... elements) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfadd(key, elements);
        } finally {Streams.safeClose(jedis);}
    }

    public Long clusterKeySlot(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterKeySlot(key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long clusterCountKeysInSlot(int slot) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterCountKeysInSlot(slot);
        } finally {Streams.safeClose(jedis);}
    }

    public long pfcount(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfcount(key);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterSaveConfig() {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSaveConfig();
        } finally {Streams.safeClose(jedis);}
    }

    public String pfmerge(byte[] destkey, byte[]... sourcekeys) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfmerge(destkey, sourcekeys);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterReplicate(String nodeId) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterReplicate(nodeId);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pfcount(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfcount(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> clusterSlaves(String nodeId) {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSlaves(nodeId);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<byte[]> scan(byte[] cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<byte[]> scan(byte[] cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public String clusterFailover() {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterFailover();
        } finally {Streams.safeClose(jedis);}
    }

    public List<Object> clusterSlots() {
        Jedis jedis = getJedis();
        try {
            return jedis.clusterSlots();
        } finally {Streams.safeClose(jedis);}
    }

    public String asking() {
        Jedis jedis = getJedis();
        try {
            return jedis.asking();
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.hscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> pubsubChannels(String pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.pubsubChannels(pattern);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.hscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pubsubNumPat() {
        Jedis jedis = getJedis();
        try {
            return jedis.pubsubNumPat();
        } finally {Streams.safeClose(jedis);}
    }

    public Map<String, String> pubsubNumSub(String... channels) {
        Jedis jedis = getJedis();
        try {
            return jedis.pubsubNumSub(channels);
        } finally {Streams.safeClose(jedis);}
    }

    public void close() {
        jedis().close();
    }

    public void setDataSource(Pool<Jedis> jedisPool) {
        jedis().setDataSource(jedisPool);
    }

    public ScanResult<byte[]> sscan(byte[] key, byte[] cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.sscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public Long pfadd(String key, String... elements) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfadd(key, elements);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<byte[]> sscan(byte[] key, byte[] cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.sscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public long pfcount(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfcount(key);
        } finally {Streams.safeClose(jedis);}
    }

    public long pfcount(String... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfcount(keys);
        } finally {Streams.safeClose(jedis);}
    }

    public String pfmerge(String destkey, String... sourcekeys) {
        Jedis jedis = getJedis();
        try {
            return jedis.pfmerge(destkey, sourcekeys);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Tuple> zscan(byte[] key, byte[] cursor) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscan(key, cursor);
        } finally {Streams.safeClose(jedis);}
    }

    public ScanResult<Tuple> zscan(byte[] key, byte[] cursor, ScanParams params) {
        Jedis jedis = getJedis();
        try {
            return jedis.zscan(key, cursor, params);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> blpop(int timeout, String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.blpop(timeout, key);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.brpop(timeout, key);
        } finally {Streams.safeClose(jedis);}
    }

    public Long geoadd(String key, double longitude, double latitude, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.geoadd(key, longitude, latitude, member);
        } finally {Streams.safeClose(jedis);}
    }

    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        Jedis jedis = getJedis();
        try {
            return jedis.geoadd(key, memberCoordinateMap);
        } finally {Streams.safeClose(jedis);}
    }

    public Long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
        Jedis jedis = getJedis();
        try {
            return jedis.geoadd(key, longitude, latitude, member);
        } finally {Streams.safeClose(jedis);}
    }

    public Double geodist(String key, String member1, String member2) {
        Jedis jedis = getJedis();
        try {
            return jedis.geodist(key, member1, member2);
        } finally {Streams.safeClose(jedis);}
    }

    public Long geoadd(byte[] key, Map<byte[], GeoCoordinate> memberCoordinateMap) {
        Jedis jedis = getJedis();
        try {
            return jedis.geoadd(key, memberCoordinateMap);
        } finally {Streams.safeClose(jedis);}
    }

    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis = getJedis();
        try {
            return jedis.geodist(key, member1, member2, unit);
        } finally {Streams.safeClose(jedis);}
    }

    public Double geodist(byte[] key, byte[] member1, byte[] member2) {
        Jedis jedis = getJedis();
        try {
            return jedis.geodist(key, member1, member2);
        } finally {Streams.safeClose(jedis);}
    }

    public List<String> geohash(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.geohash(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    public Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit) {
        Jedis jedis = getJedis();
        try {
            return jedis.geodist(key, member1, member2, unit);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoCoordinate> geopos(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.geopos(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    public List<byte[]> geohash(byte[] key, byte[]... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.geohash(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadius(String key,
                                             double longitude,
                                             double latitude,
                                             double radius,
                                             GeoUnit unit) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoCoordinate> geopos(byte[] key, byte[]... members) {
        Jedis jedis = getJedis();
        try {
            return jedis.geopos(key, members);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadius(String key,
                                             double longitude,
                                             double latitude,
                                             double radius,
                                             GeoUnit unit,
                                             GeoRadiusParam param) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit, param);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadius(byte[] key,
                                             double longitude,
                                             double latitude,
                                             double radius,
                                             GeoUnit unit) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadiusByMember(String key,
                                                     String member,
                                                     double radius,
                                                     GeoUnit unit) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadiusByMember(key, member, radius, unit);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadius(byte[] key,
                                             double longitude,
                                             double latitude,
                                             double radius,
                                             GeoUnit unit,
                                             GeoRadiusParam param) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit, param);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadiusByMember(String key,
                                                     String member,
                                                     double radius,
                                                     GeoUnit unit,
                                                     GeoRadiusParam param) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadiusByMember(key, member, radius, unit, param);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadiusByMember(byte[] key,
                                                     byte[] member,
                                                     double radius,
                                                     GeoUnit unit) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadiusByMember(key, member, radius, unit);
        } finally {Streams.safeClose(jedis);}
    }

    public List<GeoRadiusResponse> georadiusByMember(byte[] key,
                                                     byte[] member,
                                                     double radius,
                                                     GeoUnit unit,
                                                     GeoRadiusParam param) {
        Jedis jedis = getJedis();
        try {
            return jedis.georadiusByMember(key, member, radius, unit, param);
        } finally {Streams.safeClose(jedis);}
    }

    public void setJedisAgent(JedisAgent jedisAgent) {
        this.jedisAgent = jedisAgent;
    }
}
