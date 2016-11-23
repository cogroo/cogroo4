package org.cogroo.util;

import opennlp.tools.util.Cache;

public abstract class CacheWrapper <T> {
  
  private volatile Cache tagCache = new Cache(500);
  
  @SuppressWarnings("unchecked")
  public T get(String key) {
    T result = null;
    synchronized (this) {
      result = (T) tagCache.get(key);
      if(result == null) {
        result = compute(key);
        tagCache.put(key, result);
      }
    }

    return result;
  }
  
  public abstract T compute(String key);
}
 