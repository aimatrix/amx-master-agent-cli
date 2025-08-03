package com.aimatrix.cli.cache

import kotlinx.coroutines.test.runTest
import kotlin.test.*
import com.aimatrix.cli.core.Logger
import com.aimatrix.cli.core.LogLevel
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.milliseconds

class CacheManagerTest {
    
    private val fakeFileSystem = FakeFileSystem()
    private val cacheDirectory = "/tmp/cache".toPath()
    private val logger = Logger(LogLevel.INFO)
    
    private fun createCacheManager(config: CacheConfig = CacheConfig()): CacheManager {
        fakeFileSystem.createDirectories(cacheDirectory)
        return CacheManager(cacheDirectory, logger, config)
    }
    
    @Test
    fun testPutAndGetString() = runTest {
        val cacheManager = createCacheManager()
        
        val key = "test-key"
        val value = "test-value"
        
        cacheManager.put(key, value)
        val retrieved = cacheManager.get<String>(key)
        
        assertEquals(value, retrieved)
    }
    
    @Test
    fun testPutAndGetComplexObject() = runTest {
        val cacheManager = createCacheManager()
        
        data class TestData(val id: Int, val name: String, val values: List<Double>)
        
        val key = "complex-key"
        val value = TestData(1, "test", listOf(1.1, 2.2, 3.3))
        
        cacheManager.put(key, value)
        val retrieved = cacheManager.get<TestData>(key)
        
        assertEquals(value, retrieved)
    }
    
    @Test
    fun testCacheMiss() = runTest {
        val cacheManager = createCacheManager()
        
        val retrieved = cacheManager.get<String>("non-existent-key")
        
        assertNull(retrieved)
    }
    
    @Test
    fun testCacheExpiration() = runTest {
        val cacheManager = createCacheManager()
        
        val key = "expiring-key"
        val value = "expiring-value"
        val shortTTL = 100.milliseconds
        
        cacheManager.put(key, value, ttl = shortTTL)
        
        // Should be available immediately
        val immediate = cacheManager.get<String>(key)
        assertEquals(value, immediate)
        
        // Wait for expiration
        kotlinx.coroutines.delay(200)
        
        val expired = cacheManager.get<String>(key)
        assertNull(expired)
    }
    
    @Test
    fun testPersistentCache() = runTest {
        val cacheManager = createCacheManager()
        
        val key = "persistent-key"
        val value = "persistent-value"
        
        // Store with persistence
        cacheManager.put(key, value, persistent = true)
        
        // Remove from memory cache by creating new instance
        val newCacheManager = createCacheManager()
        
        // Should still be available from disk
        val retrieved = newCacheManager.get<String>(key)
        assertEquals(value, retrieved)
    }
    
    @Test
    fun testCacheEviction() = runTest {
        val smallConfig = CacheConfig(
            maxMemorySize = 1024, // 1KB limit
            evictionRatio = 0.5
        )
        val cacheManager = createCacheManager(smallConfig)
        
        // Fill cache beyond capacity
        repeat(20) { i ->
            cacheManager.put("key-$i", "value-".repeat(100)) // ~500 bytes each
        }
        
        val statistics = cacheManager.getStatistics()
        
        // Some entries should have been evicted
        assertTrue(statistics.memoryEntries < 20)
        assertTrue(statistics.memorySize <= smallConfig.maxMemorySize)
    }
    
    @Test
    fun testCacheRemoval() = runTest {
        val cacheManager = createCacheManager()
        
        val key = "removable-key"
        val value = "removable-value"
        
        cacheManager.put(key, value)
        assertTrue(cacheManager.contains(key))
        
        val removed = cacheManager.remove(key)
        assertTrue(removed)
        assertFalse(cacheManager.contains(key))
        
        val retrieved = cacheManager.get<String>(key)
        assertNull(retrieved)
    }
    
    @Test
    fun testCacheClear() = runTest {
        val cacheManager = createCacheManager()
        
        // Add multiple entries
        repeat(5) { i ->
            cacheManager.put("key-$i", "value-$i")
        }
        
        var statistics = cacheManager.getStatistics()
        assertEquals(5, statistics.memoryEntries)
        
        cacheManager.clear()
        
        statistics = cacheManager.getStatistics()
        assertEquals(0, statistics.memoryEntries)
        assertEquals(0, statistics.diskEntries)
    }
    
    @Test
    fun testGetKeys() = runTest {
        val cacheManager = createCacheManager()
        
        cacheManager.put("user-123", "John")
        cacheManager.put("user-456", "Jane")
        cacheManager.put("config-setting", "value")
        
        val allKeys = cacheManager.getKeys()
        assertEquals(3, allKeys.size)
        assertTrue(allKeys.contains("user-123"))
        assertTrue(allKeys.contains("user-456"))
        assertTrue(allKeys.contains("config-setting"))
        
        val userKeys = cacheManager.getKeys("user-*")
        assertEquals(2, userKeys.size)
        assertTrue(userKeys.contains("user-123"))
        assertTrue(userKeys.contains("user-456"))
        assertFalse(userKeys.contains("config-setting"))
    }
    
    @Test
    fun testPrefetch() = runTest {
        val cacheManager = createCacheManager()
        
        var loaderCalled = false
        val loader: suspend () -> String = {
            loaderCalled = true
            "loaded-value"
        }
        
        val key = "prefetch-key"
        
        // First call should invoke loader
        val value1 = cacheManager.prefetch(key, loader)
        assertEquals("loaded-value", value1)
        assertTrue(loaderCalled)
        
        // Reset flag
        loaderCalled = false
        
        // Second call should use cache
        val value2 = cacheManager.prefetch(key, loader)
        assertEquals("loaded-value", value2)
        assertFalse(loaderCalled) // Should not call loader again
    }
    
    @Test
    fun testWarmUp() = runTest {
        val cacheManager = createCacheManager()
        
        val warmUpEntries = mapOf<String, suspend () -> Any>(
            "user-data" to { "user-info" },
            "config-data" to { "config-info" },
            "metadata" to { mapOf("version" to "1.0") }
        )
        
        cacheManager.warmUp(warmUpEntries)
        
        assertEquals("user-info", cacheManager.get<String>("user-data"))
        assertEquals("config-info", cacheManager.get<String>("config-data"))
        assertNotNull(cacheManager.get<Map<String, String>>("metadata"))
    }
    
    @Test
    fun testCacheStatistics() = runTest {
        val cacheManager = createCacheManager()
        
        // Add some entries
        cacheManager.put("key1", "value1", persistent = true)
        cacheManager.put("key2", "value2", persistent = false)
        
        // Generate some hits and misses
        cacheManager.get<String>("key1") // hit
        cacheManager.get<String>("key2") // hit
        cacheManager.get<String>("nonexistent") // miss
        
        val statistics = cacheManager.getStatistics()
        
        assertEquals(2, statistics.memoryEntries)
        assertTrue(statistics.memorySize > 0)
        assertTrue(statistics.totalHits > 0)
        assertTrue(statistics.totalMisses > 0)
        assertTrue(statistics.hitRate > 0.0)
        assertTrue(statistics.hitRate < 1.0)
    }
    
    @Test
    fun testCacheEvents() = runTest {
        val cacheManager = createCacheManager()
        val events = mutableListOf<CacheEvent>()
        
        val job = kotlinx.coroutines.launch {
            cacheManager.cacheEvents.collect { event ->
                events.add(event)
            }
        }
        
        // Perform cache operations
        cacheManager.put("key1", "value1")
        cacheManager.get<String>("key1") // hit
        cacheManager.get<String>("key2") // miss
        cacheManager.remove("key1")
        
        // Give events time to propagate
        kotlinx.coroutines.delay(100)
        
        job.cancel()
        
        assertTrue(events.any { it is CacheEvent.EntryAdded })
        assertTrue(events.any { it is CacheEvent.CacheHit })
        assertTrue(events.any { it is CacheEvent.CacheMiss })
        assertTrue(events.any { it is CacheEvent.EntryRemoved })
    }
    
    @Test
    fun testMemoryAndDiskCacheSync() = runTest {
        val cacheManager = createCacheManager()
        
        val key = "sync-key"
        val value = "sync-value"
        
        // Store in both memory and disk
        cacheManager.put(key, value, persistent = true)
        
        // Verify it's in memory
        val memoryValue = cacheManager.get<String>(key)
        assertEquals(value, memoryValue)
        
        // Create new cache manager (simulates restart)
        val newCacheManager = createCacheManager()
        
        // Should load from disk into memory
        val diskValue = newCacheManager.get<String>(key)
        assertEquals(value, diskValue)
    }
    
    @Test
    fun testLargeObjectHandling() = runTest {
        val cacheManager = createCacheManager()
        
        val key = "large-object"
        val largeList = (1..10000).map { "item-$it" }
        
        cacheManager.put(key, largeList)
        val retrieved = cacheManager.get<List<String>>(key)
        
        assertEquals(largeList.size, retrieved?.size)
        assertEquals(largeList.first(), retrieved?.first())
        assertEquals(largeList.last(), retrieved?.last())
    }
    
    @Test
    fun testConcurrentAccess() = runTest {
        val cacheManager = createCacheManager()
        
        // Simulate concurrent puts and gets
        val jobs = (1..100).map { i ->
            kotlinx.coroutines.launch {
                cacheManager.put("concurrent-$i", "value-$i")
                val retrieved = cacheManager.get<String>("concurrent-$i")
                assertEquals("value-$i", retrieved)
            }
        }
        
        jobs.forEach { it.join() }
        
        val statistics = cacheManager.getStatistics()
        assertEquals(100, statistics.memoryEntries)
    }
}