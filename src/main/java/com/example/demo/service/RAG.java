
package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

@Component
public class RAG {

	@Autowired
	OpenAiChatModel model;

	private EmbeddingModel embeddingModel;

	private EmbeddingStore<TextSegment> embeddingStore;
	/*
	 * @Autowired OpenAiChatModel model;
	 */

	public Assistant newAssistant(String file) {

		DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);

		// write a code to convert file to document
		Document document = Document.from(file);

		List<TextSegment> segments = splitter.split(document);
		EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
		List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

		EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
		embeddingStore.addAll(embeddings, segments);

		ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel).maxResults(2).minScore(0.5).build();
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
		return AiServices.builder(Assistant.class).chatLanguageModel(model).contentRetriever(contentRetriever)
				.chatMemory(chatMemory).build();
	}

	public Assistant newAssistant2(String file) {

		System.out.println("chatLanguageModel: " + model);

		DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
		Document document = Document.from(file);

		List<TextSegment> segments = splitter.split(document);
		EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
		List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

		/*
		 * EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
		 * embeddingStore.addAll(embeddings, segments);
		 */

		/*
		 * EmbeddingStore<TextSegment> embeddingStore =
		 * ElasticsearchEmbeddingStore.builder()
		 * .serverUrl("https://localhost:9200").userName("elastic").password(
		 * "Jbyc8ck8+EVlg3mx=4u9") .indexName("awarebot").dimension(2) .build();
		 */
		EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
				.serverUrl("https://54.213.37.166:9200").userName("elastic").password("Jbyc8ck8+EVlg3mx=4u9").dimension(384)
				.build();
		embeddingStore.addAll(embeddings, segments);

		ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel).maxResults(2).minScore(0.5).build();
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
		return AiServices.builder(Assistant.class).chatLanguageModel(model).contentRetriever(contentRetriever)
				.chatMemory(chatMemory).build();
	}

	public String persistFile(String content) {
		DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
		Document document = Document.from(content);

		List<TextSegment> segments = splitter.split(document);
		embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
		List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

		embeddingStore = ElasticsearchEmbeddingStore.builder().serverUrl("https://localhost:9200").userName("elastic")
				.password("Jbyc8ck8+EVlg3mx=4u9").dimension(384).build();
		List<String> all = embeddingStore.addAll(embeddings, segments);
		if (all.size() > 0) {
			return "Ask Questions";

		}
		return "Exception";
	}

	public Assistant assist() {
		embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

		embeddingStore = ElasticsearchEmbeddingStore.builder().serverUrl("https://localhost:9200").userName("elastic")
				.password("Jbyc8ck8+EVlg3mx=4u9").dimension(384).build();
		ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel).maxResults(2).minScore(0.5).build();
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

		return AiServices.builder(Assistant.class).chatLanguageModel(model).contentRetriever(contentRetriever)
				.chatMemory(chatMemory).build();

	}
}
