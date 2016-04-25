/*
 * Copyright 2009 Arne Vajhøj.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package dk.vajhoej.record;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class StructInfoCache caches StructInfo objects in a singleton cache.
 */
public class StructInfoCache {
    private static Logger log = Logger.getLogger(StructInfoCache.class.getName());
	private static StructInfoCache instance = null;
	private Map<Class<?>, StructInfo> cache;
	private int found;
	private int total;
	private StructInfoCache() {
		reset();
	}
	/**
	 * Get singleton instance.
	 * @return instance
	 */
	public synchronized static StructInfoCache getInstance() {
		if(instance == null) {
			instance = new StructInfoCache();
		}
		return instance;
	}
	/**
	 * Get StructInfo from cache.
	 * @param clz class we want StructInfo for
	 * @return StructInfo
	 */
	public synchronized StructInfo get(Class<?> clz) {
		StructInfo res = cache.get(clz);
		if(res != null) {
			found++;
            log.finer(clz.getName() + " found in StructInfoCache");
		} else {
            log.finer(clz.getName() + " not found in StructInfoCache");
		}
		total++;
		return res;
	}
	/**
	 * Put StructInfo into cache.
	 * @param clz class we have StructInfo for
	 * @param si StructInfo
	 */
	public synchronized void put(Class<?> clz, StructInfo si) {
		cache.put(clz, si);
		log.finer(clz.getName() + " put in StructInfoCache");
	}
	/**
	 * Get cache hit rate.
	 * @return hit rate
	 */
	public synchronized double getHitRate() {
		return (total > 0) ? (double)found/(double)total : -1;
	}
	/**
	 * Reset cache.
	 */
	public synchronized void reset() {
		cache = new HashMap<Class<?>, StructInfo>();
		found = 0;
		total = 0;
		log.finer("StructInfoCache reset");
	}
	/**
	 * Convenience method to get StructInfo from cache and analyze class if not in cache.
	 * @param t class
	 * @return StructInfo StructInfo for class
	 * @throws RecordException if error analyzing cache
	 */
	public static <T> StructInfo analyze(Class<T> t) throws RecordException  {
		StructInfoCache cache = StructInfoCache.getInstance();
		StructInfo si = cache.get(t);
		if(si == null) {
			si = StructInfo.analyze(t);
			cache.put(t, si);
		}
		return si;
	}
}
