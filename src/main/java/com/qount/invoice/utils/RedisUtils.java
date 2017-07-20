package com.qount.invoice.utils;

import com.qount.invoice.database.redis.JedisClient;

public class RedisUtils {
	
	public static final String journal_que_prefix = "journals";
	
	public static void writeToQue(String job){
		JedisClient jedisClient = new JedisClient();
		jedisClient.sendToWaitQueue(journal_que_prefix, job);
	}

}
