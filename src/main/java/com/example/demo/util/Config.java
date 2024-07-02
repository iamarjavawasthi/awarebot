package com.example.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class Config {

	@Bean
	OpenAiChatModel model() {
		return OpenAiChatModel.builder().apiKey("demo").modelName("gpt-3.5-turbo").build();
	}

	/*
	 * @Bean OllamaChatModel ollamaChatModel() { return
	 * OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName(
	 * "llama2").build(); }
	 */

}
