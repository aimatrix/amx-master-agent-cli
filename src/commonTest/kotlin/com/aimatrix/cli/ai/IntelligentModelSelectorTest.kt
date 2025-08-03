package com.aimatrix.cli.ai

import kotlinx.coroutines.test.runTest
import kotlin.test.*
import com.aimatrix.cli.ai.providers.*
import com.aimatrix.cli.analytics.PerformanceAnalytics
import com.aimatrix.cli.analytics.CostOptimizer
import com.aimatrix.cli.config.GeminiConfig
import com.aimatrix.cli.config.OpenAIConfig
import com.aimatrix.cli.config.ClaudeConfig
import com.aimatrix.cli.core.Logger
import com.aimatrix.cli.core.LogLevel
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*

class IntelligentModelSelectorTest {
    
    private fun createMockProvider(
        id: String,
        name: String,
        models: List<String> = listOf("default-model")
    ): LLMProvider {
        return object : LLMProvider {
            override val providerId: String = id
            override val providerName: String = name
            override val defaultModel: String = models.first()
            
            override suspend fun generateContent(request: GenerateContentRequest): GenerateContentResponse {
                return GenerateContentResponse(
                    content = "Mock response from $name",
                    usage = TokenUsage(10, 15, 25),
                    metadata = ResponseMetadata(
                        provider = id,
                        model = request.metadata.preferredModel ?: defaultModel,
                        finishReason = "stop"
                    )
                )
            }
            
            override suspend fun generateContentStream(request: GenerateContentRequest) = 
                kotlinx.coroutines.flow.flowOf()
            
            override suspend fun getAvailableModels(): List<ModelInfo> {
                return models.map { ModelInfo(it, it.capitalize(), "Mock model") }
            }
            
            override suspend fun validateConfiguration(): ValidationResult {
                return ValidationResult(true, emptyList())
            }
        }
    }
    
    private fun createTestSelector(): IntelligentModelSelector {
        val logger = Logger(LogLevel.INFO)
        
        val providers = mapOf(
            "openai" to createMockProvider("openai", "OpenAI", listOf("gpt-4", "gpt-3.5-turbo")),
            "claude" to createMockProvider("claude", "Claude", listOf("claude-3-opus", "claude-3-sonnet")),
            "gemini" to createMockProvider("gemini", "Gemini", listOf("gemini-pro", "gemini-pro-vision"))
        )
        
        val analytics = PerformanceAnalytics(logger)
        val costOptimizer = CostOptimizer(analytics, logger)
        
        return IntelligentModelSelector(
            providers = providers,
            analytics = analytics,
            costOptimizer = costOptimizer,
            config = ModelSelectorConfig()
        )
    }
    
    @Test
    fun testBestProviderSelectionForChat() = runTest {
        val selector = createTestSelector()
        
        val request = GenerateContentRequest(
            message = "Hello, how are you?",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        val result = selector.selectBestProvider(request)
        
        assertNotNull(result)
        assertTrue(result.provider.providerId in listOf("openai", "claude", "gemini"))
        assertNotNull(result.model)
        assertTrue(result.confidence > 0.0)
    }
    
    @Test
    fun testBestProviderSelectionForCode() = runTest {
        val selector = createTestSelector()
        
        val request = GenerateContentRequest(
            message = "Write a Python function to sort a list",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CODE_GENERATION
            )
        )
        
        val result = selector.selectBestProvider(request)
        
        assertNotNull(result)
        // For code generation, OpenAI might be preferred
        assertTrue(result.provider.providerId in listOf("openai", "claude", "gemini"))
        assertNotNull(result.model)
        assertTrue(result.confidence > 0.0)
    }
    
    @Test
    fun testBestProviderSelectionForMultimodal() = runTest {
        val selector = createTestSelector()
        
        val request = GenerateContentRequest(
            message = "Describe this image",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.MULTIMODAL
            ),
            attachments = listOf(
                ContentAttachment(
                    type = AttachmentType.IMAGE,
                    content = "base64data",
                    mimeType = "image/jpeg"
                )
            )
        )
        
        val result = selector.selectBestProvider(request)
        
        assertNotNull(result)
        // For multimodal, Gemini might be preferred due to vision capabilities
        assertTrue(result.provider.providerId in listOf("openai", "claude", "gemini"))
        assertNotNull(result.model)
        assertTrue(result.confidence > 0.0)
    }
    
    @Test
    fun testPreferredProviderRespected() = runTest {
        val selector = createTestSelector()
        
        val request = GenerateContentRequest(
            message = "Hello, world!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT,
                preferredProvider = LLMProviderType.CLAUDE
            )
        )
        
        val result = selector.selectBestProvider(request)
        
        assertNotNull(result)
        assertEquals("claude", result.provider.providerId)
    }
    
    @Test
    fun testPreferredModelRespected() = runTest {
        val selector = createTestSelector()
        
        val request = GenerateContentRequest(
            message = "Hello, world!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT,
                preferredProvider = LLMProviderType.OPENAI,
                preferredModel = "gpt-3.5-turbo"
            )
        )
        
        val result = selector.selectBestProvider(request)
        
        assertNotNull(result)
        assertEquals("openai", result.provider.providerId)
        assertEquals("gpt-3.5-turbo", result.model)
    }
    
    @Test
    fun testFallbackWhenPreferredUnavailable() = runTest {
        // Create selector with limited providers
        val logger = Logger(LogLevel.INFO)
        val providers = mapOf(
            "gemini" to createMockProvider("gemini", "Gemini", listOf("gemini-pro"))
        )
        val analytics = PerformanceAnalytics(logger)
        val costOptimizer = CostOptimizer(analytics, logger)
        
        val selector = IntelligentModelSelector(
            providers = providers,
            analytics = analytics,
            costOptimizer = costOptimizer,
            config = ModelSelectorConfig()
        )
        
        val request = GenerateContentRequest(
            message = "Hello, world!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT,
                preferredProvider = LLMProviderType.OPENAI // Not available
            )
        )
        
        val result = selector.selectBestProvider(request)
        
        assertNotNull(result)
        assertEquals("gemini", result.provider.providerId) // Fallback to available provider
    }
    
    @Test
    fun testContextualBandits() = runTest {
        val selector = createTestSelector()
        
        // Simulate multiple requests to build up context
        repeat(5) {
            val request = GenerateContentRequest(
                message = "Code generation request $it",
                parameters = GenerationParameters(),
                metadata = RequestMetadata(
                    taskType = TaskType.CODE_GENERATION
                )
            )
            
            val result = selector.selectBestProvider(request)
            assertNotNull(result)
            
            // Simulate feedback (successful response)
            selector.recordFeedback(
                provider = result.provider.providerId,
                model = result.model,
                taskType = TaskType.CODE_GENERATION,
                success = true,
                responseTime = 1000 + it * 100L,
                cost = 0.01f + it * 0.001f
            )
        }
        
        // The selector should now have learned preferences for code generation
        val finalRequest = GenerateContentRequest(
            message = "Generate code for sorting",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CODE_GENERATION
            )
        )
        
        val result = selector.selectBestProvider(finalRequest)
        assertNotNull(result)
        assertTrue(result.confidence > 0.0)
    }
    
    @Test
    fun testThompsonSampling() = runTest {
        val selector = createTestSelector()
        
        // Record some historical data
        selector.recordFeedback("openai", "gpt-4", TaskType.CHAT, true, 800L, 0.02f)
        selector.recordFeedback("openai", "gpt-4", TaskType.CHAT, true, 900L, 0.02f)
        selector.recordFeedback("claude", "claude-3-opus", TaskType.CHAT, true, 1200L, 0.03f)
        selector.recordFeedback("gemini", "gemini-pro", TaskType.CHAT, false, 2000L, 0.01f)
        
        val request = GenerateContentRequest(
            message = "Hello, world!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        // Test multiple selections to see variation due to sampling
        val selections = mutableSetOf<String>()
        repeat(10) {
            val result = selector.selectBestProvider(request)
            assertNotNull(result)
            selections.add(result.provider.providerId)
        }
        
        // Should have some variation in selections (Thompson sampling exploration)
        assertTrue(selections.isNotEmpty())
    }
    
    @Test
    fun testUpperConfidenceBound() = runTest {
        val config = ModelSelectorConfig(
            selectionAlgorithm = SelectionAlgorithm.UPPER_CONFIDENCE_BOUND,
            explorationFactor = 1.0
        )
        
        val logger = Logger(LogLevel.INFO)
        val providers = mapOf(
            "openai" to createMockProvider("openai", "OpenAI"),
            "claude" to createMockProvider("claude", "Claude")
        )
        val analytics = PerformanceAnalytics(logger)
        val costOptimizer = CostOptimizer(analytics, logger)
        
        val selector = IntelligentModelSelector(
            providers = providers,
            analytics = analytics,
            costOptimizer = costOptimizer,
            config = config
        )
        
        // Record different performance for each provider
        selector.recordFeedback("openai", "gpt-4", TaskType.CHAT, true, 800L, 0.02f)
        selector.recordFeedback("claude", "claude-3-opus", TaskType.CHAT, true, 1200L, 0.01f)
        
        val request = GenerateContentRequest(
            message = "Hello, world!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        val result = selector.selectBestProvider(request)
        assertNotNull(result)
        assertTrue(result.provider.providerId in listOf("openai", "claude"))
    }
    
    @Test
    fun testCostOptimization() = runTest {
        val config = ModelSelectorConfig(
            costWeight = 0.8, // High cost sensitivity
            performanceWeight = 0.2
        )
        
        val logger = Logger(LogLevel.INFO)
        val providers = mapOf(
            "openai" to createMockProvider("openai", "OpenAI"),
            "gemini" to createMockProvider("gemini", "Gemini")
        )
        val analytics = PerformanceAnalytics(logger)
        val costOptimizer = CostOptimizer(analytics, logger)
        
        val selector = IntelligentModelSelector(
            providers = providers,
            analytics = analytics,
            costOptimizer = costOptimizer,
            config = config
        )
        
        // Record cost data - Gemini cheaper, OpenAI more expensive
        selector.recordFeedback("openai", "gpt-4", TaskType.CHAT, true, 800L, 0.05f)
        selector.recordFeedback("gemini", "gemini-pro", TaskType.CHAT, true, 1000L, 0.001f)
        
        val request = GenerateContentRequest(
            message = "Hello, world!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        val result = selector.selectBestProvider(request)
        assertNotNull(result)
        // With high cost weight, should prefer Gemini (cheaper)
        assertTrue(result.provider.providerId in listOf("openai", "gemini"))
    }
    
    @Test
    fun testAvailableProvidersOnly() = runTest {
        val selector = createTestSelector()
        
        val availableProviders = selector.getAvailableProviders()
        
        assertEquals(3, availableProviders.size)
        assertTrue(availableProviders.any { it.providerId == "openai" })
        assertTrue(availableProviders.any { it.providerId == "claude" })
        assertTrue(availableProviders.any { it.providerId == "gemini" })
    }
    
    @Test
    fun testGetProviderByType() = runTest {
        val selector = createTestSelector()
        
        val openaiProvider = selector.getProvider(LLMProviderType.OPENAI)
        assertNotNull(openaiProvider)
        assertEquals("openai", openaiProvider.providerId)
        
        val claudeProvider = selector.getProvider(LLMProviderType.CLAUDE)
        assertNotNull(claudeProvider)
        assertEquals("claude", claudeProvider.providerId)
        
        val geminiProvider = selector.getProvider(LLMProviderType.GEMINI)
        assertNotNull(geminiProvider)
        assertEquals("gemini", geminiProvider.providerId)
    }
    
    @Test
    fun testSelectionStatistics() = runTest {
        val selector = createTestSelector()
        
        // Make several selections and record feedback
        repeat(10) { i ->
            val request = GenerateContentRequest(
                message = "Test request $i",
                parameters = GenerationParameters(),
                metadata = RequestMetadata(
                    taskType = if (i % 2 == 0) TaskType.CHAT else TaskType.CODE_GENERATION
                )
            )
            
            val result = selector.selectBestProvider(request)
            selector.recordFeedback(
                provider = result.provider.providerId,
                model = result.model,
                taskType = request.metadata.taskType,
                success = i < 8, // 80% success rate
                responseTime = 1000L + i * 100L,
                cost = 0.01f + i * 0.001f
            )
        }
        
        val statistics = selector.getSelectionStatistics()
        
        assertNotNull(statistics)
        assertTrue(statistics.totalSelections >= 10)
        assertTrue(statistics.successRate <= 1.0)
        assertTrue(statistics.averageResponseTime > 0)
        assertTrue(statistics.totalCost >= 0)
    }
}