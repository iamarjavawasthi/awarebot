package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

@SpringBootApplication
public class AwarebotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwarebotApplication.class, args);
	}

}
