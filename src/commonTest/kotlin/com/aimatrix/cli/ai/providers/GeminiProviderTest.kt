package com.aimatrix.cli.ai.providers

import kotlinx.coroutines.test.runTest
import kotlin.test.*
import com.aimatrix.cli.ai.*
import com.aimatrix.cli.config.GeminiConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json

class GeminiProviderTest {
    
    private fun createMockHttpClient(responseContent: String, statusCode: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = ByteReadChannel(responseContent),
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
    }
    
    @Test
    fun testProviderInitialization() = runTest {
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient("{}")
        val provider = GeminiProvider(config, mockClient)
        
        assertEquals("gemini", provider.providerId)
        assertEquals("Gemini", provider.providerName)
        assertEquals("gemini-pro", provider.defaultModel)
    }
    
    @Test
    fun testConfigurationValidation() = runTest {
        val validConfig = GeminiConfig(
            apiKey = "valid-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient("{}")
        val provider = GeminiProvider(validConfig, mockClient)
        
        val validation = provider.validateConfiguration()
        assertTrue(validation.isValid)
        assertTrue(validation.issues.isEmpty())
    }
    
    @Test
    fun testInvalidConfigurationValidation() = runTest {
        val invalidConfig = GeminiConfig(
            apiKey = "", // Empty API key
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient("{}")
        val provider = GeminiProvider(invalidConfig, mockClient)
        
        val validation = provider.validateConfiguration()
        assertFalse(validation.isValid)
        assertTrue(validation.issues.isNotEmpty())
        assertTrue(validation.issues.any { it.contains("API key") })
    }
    
    @Test
    fun testGenerateContentSuccess() = runTest {
        val mockResponse = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {
                                "text": "This is a test response from Gemini."
                            }
                        ]
                    },
                    "finishReason": "STOP"
                }
            ],
            "usageMetadata": {
                "promptTokenCount": 10,
                "candidatesTokenCount": 8,
                "totalTokenCount": 18
            }
        }
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(mockResponse)
        val provider = GeminiProvider(config, mockClient)
        
        val request = GenerateContentRequest(
            message = "Hello, Gemini!",
            parameters = GenerationParameters(
                temperature = 0.7f,
                maxTokens = 100
            ),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        val response = provider.generateContent(request)
        
        assertNotNull(response)
        assertEquals("This is a test response from Gemini.", response.content)
        assertEquals(18, response.usage.totalTokens)
        assertEquals(10, response.usage.inputTokens)
        assertEquals(8, response.usage.outputTokens)
        assertEquals("gemini", response.metadata.provider)
        assertEquals("gemini-pro", response.metadata.model)
    }
    
    @Test
    fun testGenerateContentWithStreamingSuccess() = runTest {
        val mockStreamResponse = """
        data: {"candidates":[{"content":{"parts":[{"text":"Hello"}]}}]}

        data: {"candidates":[{"content":{"parts":[{"text":" there!"}]}}]}

        data: {"candidates":[{"content":{"parts":[{"text":" How"}]}}]}

        data: {"candidates":[{"content":{"parts":[{"text":" can I help you?"}]}}],"usageMetadata":{"totalTokenCount":20}}

        data: [DONE]
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(mockStreamResponse)
        val provider = GeminiProvider(config, mockClient)
        
        val request = GenerateContentRequest(
            message = "Hello, Gemini!",
            parameters = GenerationParameters(
                temperature = 0.7f,
                maxTokens = 100,
                stream = true
            ),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        val chunks = mutableListOf<String>()
        provider.generateContentStream(request).collect { chunk ->
            chunks.add(chunk.content)
        }
        
        assertTrue(chunks.isNotEmpty())
        assertEquals("Hello", chunks[0])
        assertEquals(" there!", chunks[1])
        assertEquals(" How", chunks[2])
        assertEquals(" can I help you?", chunks[3])
    }
    
    @Test
    fun testGenerateContentWithError() = runTest {
        val errorResponse = """
        {
            "error": {
                "code": 400,
                "message": "Invalid request format",
                "status": "INVALID_ARGUMENT"
            }
        }
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(errorResponse, HttpStatusCode.BadRequest)
        val provider = GeminiProvider(config, mockClient)
        
        val request = GenerateContentRequest(
            message = "Hello, Gemini!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        assertFailsWith<Exception> {
            provider.generateContent(request)
        }
    }
    
    @Test
    fun testGetAvailableModels() = runTest {
        val modelsResponse = """
        {
            "models": [
                {
                    "name": "models/gemini-pro",
                    "displayName": "Gemini Pro",
                    "description": "The best model for scaling across a wide range of tasks"
                },
                {
                    "name": "models/gemini-pro-vision", 
                    "displayName": "Gemini Pro Vision",
                    "description": "The best image understanding model"
                }
            ]
        }
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(modelsResponse)
        val provider = GeminiProvider(config, mockClient)
        
        val models = provider.getAvailableModels()
        
        assertEquals(2, models.size)
        assertTrue(models.any { it.id == "gemini-pro" })
        assertTrue(models.any { it.id == "gemini-pro-vision" })
        assertTrue(models.any { it.name == "Gemini Pro" })
        assertTrue(models.any { it.name == "Gemini Pro Vision" })
    }
    
    @Test
    fun testFunctionCalling() = runTest {
        val functionCallResponse = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {
                                "functionCall": {
                                    "name": "get_weather",
                                    "args": {
                                        "location": "San Francisco"
                                    }
                                }
                            }
                        ]
                    },
                    "finishReason": "STOP"
                }
            ],
            "usageMetadata": {
                "totalTokenCount": 25
            }
        }
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(functionCallResponse)
        val provider = GeminiProvider(config, mockClient)
        
        val functions = listOf(
            FunctionDefinition(
                name = "get_weather",
                description = "Get the current weather for a location",
                parameters = mapOf(
                    "location" to FunctionParameter(
                        type = "string",
                        description = "The city name"
                    )
                )
            )
        )
        
        val request = GenerateContentRequest(
            message = "What's the weather in San Francisco?",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.FUNCTION_CALLING
            ),
            functions = functions
        )
        
        val response = provider.generateContent(request)
        
        assertNotNull(response)
        assertTrue(response.functionCalls.isNotEmpty())
        
        val functionCall = response.functionCalls.first()
        assertEquals("get_weather", functionCall.name)
        assertEquals("San Francisco", functionCall.arguments["location"])
    }
    
    @Test
    fun testRateLimitHandling() = runTest {
        val rateLimitResponse = """
        {
            "error": {
                "code": 429,
                "message": "Quota exceeded. Please try again later.",
                "status": "RESOURCE_EXHAUSTED"
            }
        }
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(rateLimitResponse, HttpStatusCode.TooManyRequests)
        val provider = GeminiProvider(config, mockClient)
        
        val request = GenerateContentRequest(
            message = "Hello, Gemini!",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.CHAT
            )
        )
        
        val exception = assertFailsWith<Exception> {
            provider.generateContent(request)
        }
        
        assertTrue(exception.message?.contains("429") == true || 
                  exception.message?.contains("rate") == true ||
                  exception.message?.contains("quota") == true)
    }
    
    @Test
    fun testMultimodalContent() = runTest {
        val multimodalResponse = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {
                                "text": "I can see this is an image of a cat sitting on a windowsill."
                            }
                        ]
                    },
                    "finishReason": "STOP"
                }
            ],
            "usageMetadata": {
                "totalTokenCount": 30
            }
        }
        """.trimIndent()
        
        val config = GeminiConfig(
            apiKey = "test-api-key",
            model = "gemini-pro-vision",
            baseUrl = "https://api.gemini.test",
            enabled = true
        )
        
        val mockClient = createMockHttpClient(multimodalResponse)
        val provider = GeminiProvider(config, mockClient)
        
        val request = GenerateContentRequest(
            message = "What do you see in this image?",
            parameters = GenerationParameters(),
            metadata = RequestMetadata(
                taskType = TaskType.MULTIMODAL
            ),
            attachments = listOf(
                ContentAttachment(
                    type = AttachmentType.IMAGE,
                    content = "base64encodedimagedata",
                    mimeType = "image/jpeg"
                )
            )
        )
        
        val response = provider.generateContent(request)
        
        assertNotNull(response)
        assertEquals("I can see this is an image of a cat sitting on a windowsill.", response.content)
        assertEquals(30, response.usage.totalTokens)
    }
}