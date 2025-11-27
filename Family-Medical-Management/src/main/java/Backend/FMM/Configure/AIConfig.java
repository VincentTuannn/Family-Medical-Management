package Backend.FMM.Configure;

import org.springframework.context.annotation.Configuration;

/**
 * AI Configuration
 * 
 * Spring Boot starter (spring-ai-ollama-spring-boot-starter) will automatically
 * configure ChatModel and EmbeddingModel beans from application.properties:
 * 
 * - spring.ai.ollama.base-url=http://localhost:11434
 * - spring.ai.ollama.chat.options.model=llama3.2
 * - spring.ai.ollama.embedding.options.model=llama3.2
 * 
 * No manual bean configuration needed unless you want to override defaults.
 */
@Configuration
public class AIConfig {
    // Spring Boot auto-configuration will create ChatModel and EmbeddingModel beans
    // based on application.properties settings
}

