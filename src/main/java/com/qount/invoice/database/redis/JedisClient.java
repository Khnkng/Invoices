package com.qount.invoice.database.redis;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qount.invoice.common.PropertyManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClient {

	private static final JedisClient JedisClient = new JedisClient();

	public static JedisClient getInstance() {
		return JedisClient;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(JedisClient.class);

	private static final JedisPool jedisPool = buildJedisPool();

	private static JedisPool buildJedisPool() {
		JedisPool jedisPool = new JedisPool(buildPoolConfig(), PropertyManager.getProperty("redis.remoteHost"));
		LOGGER.info(jedisPool.toString());
		return jedisPool;
	}

	private static JedisPoolConfig buildPoolConfig() {
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
		poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		return poolConfig;
	}

	/**
	 * sends batch job to a queue for further processing.
	 * 
	 * @param job
	 *            task that will be serialized and sent to queue
	 * @return true if job has been successfully queued
	 */
	public boolean sendToWaitQueue(String prefix, String jobJson) {

		LOGGER.debug("Trying to push job to queue: " + jobJson);

		Jedis instance = null;

		try {
			instance = jedisPool.getResource();

			// left push to a wait queue
			instance.rpush(prefix, jobJson);

			LOGGER.debug("Job successfully published to channel {} {}", prefix, jobJson);

			return true;
		} catch (Exception e) {
			LOGGER.error("Problem while publishing message to a channel", e);
			return false;
		} finally {
			if (instance != null) {
				instance.close();

			}
		}
	}

	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public List<String> getAll(String prefix) {
		LOGGER.debug("Fetching all the elements from list: " + prefix);
		List<String> jobs = null;
		Jedis instance = null;
		try {
			instance = jedisPool.getResource();
			jobs = instance.lrange(prefix, 0, -1);

		} catch (Exception e) {
			LOGGER.error("Problem fetching messages from the que", e);
		} finally {
			if(instance != null){
				instance.close();
			}
		}
		return jobs;
	}

	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public boolean removeFromWaitQue(String prefix, String element) {
		LOGGER.debug("removing the first element in the list: " + prefix);
		boolean isDeleted = false;
		Jedis instance = null;
		try {
			instance = jedisPool.getResource();
			Long deletedRows = instance.lrem(prefix, 0, element);
			isDeleted = deletedRows != 0;

		} catch (Exception e) {
			LOGGER.error("Error removing the first element in que", e);
		} finally {
			instance.close();
		}
		return isDeleted;
	}

	/**
	 * makes sure ChannelAdapter will stop its activities in a secure manner,
	 * closing all connections.
	 */
	public void stopActivities() {
		jedisPool.close();
	}

	public static void main(String[] args) {
		JedisClient adapter = new JedisClient();
//		adapter.sendToWaitQueue("test_prod", "test1");
		System.out.println(adapter.getAll("qmart"));
	}

}