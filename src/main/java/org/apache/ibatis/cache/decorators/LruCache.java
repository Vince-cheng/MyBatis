/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache.decorators;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ibatis.cache.Cache;

/**
 * Lru (least recently used) cache decorator.
 *
 * @author Clinton Begin
 */
public class LruCache implements Cache {

  private final Cache delegate;
  private Map<Object, Object> keyMap;
  private Object eldestKey;

  public LruCache(Cache delegate) {
    this.delegate = delegate;
    setSize(1024);
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  public void setSize(final int size) {
    // 调用 LinkedHashMap.put() 方法时，会调用 removeEldestEntry() 方法决定是否删除 head 指向的 Entry 数据
    keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
      private static final long serialVersionUID = 4267176411845948333L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
        boolean tooBig = size() > size;
        // 已到达缓存上限，更新 eldestKey 字段，并返回 true，LinkedHashMap 会删除该 Key
        if (tooBig) {
          eldestKey = eldest.getKey();
        }
        return tooBig;
      }
    };
  }

  @Override
  public void putObject(Object key, Object value) {
    // 写入缓存数据
    delegate.putObject(key, value);
    // 将 KV 数据同时写入 keyMap，其中可能触发缓存删除
    cycleKeyList(key);
  }

  @Override
  public Object getObject(Object key) {
    // 修改当前 Key 在 LinkedHashMap 中记录的顺序
    keyMap.get(key);
    // 查询缓存数据
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
    keyMap.clear();
  }

  private void cycleKeyList(Object key) {
    // 将 KV 数据写入到 keyMap 集合
    keyMap.put(key, key);
    if (eldestKey != null) {
      // 如果 eldestKey 不为空，则将从底层 Cache 中删除
      delegate.removeObject(eldestKey);
      eldestKey = null;
    }
  }

}
