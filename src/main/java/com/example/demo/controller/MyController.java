package com.example.demo.controller;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.Assistant;
import com.example.demo.service.RAG;

@RestController
public class MyController {

	private Assistant newAssistant;

	@Autowired
	private RAG rag;

	@PostMapping("/hello")
	public String hello(@RequestParam("text") String text) {

		newAssistant = rag.newAssistant(text);

		return "hello you may ask questions now";
	}

	@PostMapping("/hellofile")
	public String fs(@RequestParam("file") MultipartFile file) throws IOException {

		String content = new String(file.getBytes());

		// newAssistant = rag.newAssistant2(content);

		String response = rag.persistFile(content);

		return response;
	}

	/*
	 * @PostMapping("/ask") public String ask(@RequestBody String question) { try {
	 * Assistant assist = rag.assist(); String answer = assist.answer(question);
	 * return answer; }catch(Exception e) { return "Exception occured"; }
	 * 
	 * }
	 */
	@PostMapping("/ask")
	public ResponseEntity<String> ask(@RequestBody String question) {
		try {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Callable<String> task = () -> {
				// Your existing code here
				Assistant assist = rag.assist();
				return assist.answer(question);
			};

			Future<String> future = executor.submit(task);
			String answer = future.get(15, TimeUnit.SECONDS); // Set your desired timeout

			executor.shutdown();
			return ResponseEntity.ok(answer); // Return 200 OK
		} catch (TimeoutException e) {
			// Handle timeout
			return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
					.body("Timeout occurred while processing the request.");
		} catch (Exception e) {
			// Handle other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Exception occurred: " + e.getMessage());
		}
	}

}
