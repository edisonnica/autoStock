/**
 * 
 */
package com.autoStock.signal.extras;

import java.util.ArrayList;
import java.util.HashMap;

import org.encog.neural.networks.BasicNetwork;

import com.google.gson.internal.Pair;

/**
 * @author Kevin
 *
 */
public class EncogNetworkCache {
	private HashMap<String, Object> networks = new HashMap<String, Object>();
	
	public static EncogNetworkCache instance = new EncogNetworkCache();

	public static EncogNetworkCache getInstance() {
		return instance;
	}
	
	public boolean contains(String key){
		return networks.containsKey(key);
	}
	
	public Object get(String key){
		return networks.get(key);
	}

	public void put(String key, Object network) {
		networks.put(key, network);		
	}
}