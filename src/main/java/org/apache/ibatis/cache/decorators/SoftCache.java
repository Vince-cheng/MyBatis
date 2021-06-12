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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.ibatis.cache.Cache;

/**
 * Soft Reference cache decorator
 * Thanks to Dr. Heinz Kabutz for his guidance here.
 *
 * @author Clinton Begin
 */
public class SoftCache implements Cache {
  private final Deque<Object> hardLinksToAvoidGarbageCollection;
  private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;
  private final Cache delegate;
  private int numberOfHardLinks;

  public SoftCache(Cache delegate) {
    this.delegate = delegate;
    this.numberOfHardLinks = 256;
    this.hardLinksToAvoidGarbageCollection = new LinkedList<>();
    this.queueOfGarbageCollectedEntries = new ReferenceQueue<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    removeGarbageCollectedItems();
    return delegate.getSize();
  }


  public void setSize(int size) {
    this.numberOfHardLinks = size;
  }

  @Override
  public void putObject(Object key, Object value) {
    // 遍历 queueOfGarbageCollectedEntries 集合，清理已被 GC 回收的缓存数据
    removeGarbageCollectedItems();
    // 将缓存数据写入底层被装饰的 Cache 对象
    delegate.putObject(key, new SoftEntry(key, value, queueOfGarbageCollectedEntries));
  }

  @Override
  public Object getObject(Object key) {
    Object result = null;
    @SuppressWarnings("unchecked")
    // 从底层被装饰的缓存中查找数据
    SoftReference<Object> softReference = (SoftReference<Object>) delegate.getObject(key);
    if (softReference != null) {
      result = softReference.get();

      // Value 为 null，则已被 GC 回收，直接从缓存删除该 Key
      if (result == null) {
        delegate.removeObject(key);
      }
      // 未被 GC 回收
      else {
        // 将 Value 添加到 hardLinksToAvoidGarbageCollection 集合中，防止被 GC 回收
        synchronized (hardLinksToAvoidGarbageCollection) {
          hardLinksToAvoidGarbageCollection.addFirst(result);
          // 检查 hardLinksToAvoidGarbageCollection 长度，超过上限，则清理最早添加的 Value
          if (hardLinksToAvoidGarbageCollection.size() > numberOfHardLinks) {
            hardLinksToAvoidGarbageCollection.removeLast();
          }
        }
      }
    }
    return result;
  }

  @Override
  public Object removeObject(Object key) {
    removeGarbageCollectedItems();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    synchronized (hardLinksToAvoidGarbageCollection) {
      hardLinksToAvoidGarbageCollection.clear();
    }
    removeGarbageCollectedItems();
    delegate.clear();
  }

  private void removeGarbageCollectedItems() {
    SoftEntry sv;
    // 遍历 queueOfGarbageCollectedEntries 集合，其中记录了被 GC 回收的 Key
    while ((sv = (SoftEntry) queueOfGarbageCollectedEntries.poll()) != null) {
      // 清理被回收的 Key
      delegate.removeObject(sv.key);
    }
  }

  private static class SoftEntry extends SoftReference<Object> {
    private final Object key;

    SoftEntry(Object key, Object value, ReferenceQueue<Object> garbageCollectionQueue) {
      // 指向 value 的是软引用，并且关联了引用队列
      super(value, garbageCollectionQueue);
      // 指向 key 的是强引用
      this.key = key;
    }
  }

}
