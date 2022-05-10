package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@RestController
public class ParseTheNumberApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParseTheNumberApplication.class, args);
	}

	private final Map<Integer, String[]> map = new HashMap<>();

	public ParseTheNumberApplication() {
		String[] uniqueValues = {
				"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
				"Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
		};
		String[] dozens = { "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety" };
		String[] suffixes = { "", "Thousand", "Million", "Billion" };
		this.map.put(0, uniqueValues);
		this.map.put(1, dozens);
		this.map.put(2, suffixes);
	}

	@GetMapping("/parse")
	public String parse(@RequestBody Map<String, String> map) {
		StringBuilder parsedValue = new StringBuilder();

		String numberAsString = map.get("number").trim();
		numberAsString = removeLeadingZeros(numberAsString);

		final int number = Integer.parseInt(numberAsString);
		final String[] numberAsArray = numberAsString.split("");

		if(number == 0) return "Zero";

		Pattern pattern = Pattern.compile("\\d{1,3}");
		Matcher matcher = pattern.matcher(reverse(numberAsString));

		List<String> chunks = new ArrayList<>();
		while (matcher.find()){
			chunks.add(matcher.group());
		}
		chunks = reverse(chunks).stream().map(this::reverse).toList();
		for(int i = 0; i < chunks.size(); i++){
			parsedValue.append(" ").append(parseChunk(chunks.get(i)))
						.append(" ").append(this.map.get(2)[chunks.size() - i - 1]);
		}
		matcher = Pattern.compile("\\s+").matcher(parsedValue.toString().trim());
		return matcher.replaceAll(" ");
	}

	private String removeLeadingZeros(String value){
		int i = -1;
		while (value.charAt(++i) == '0' && i < value.length() - 1);
		return value.substring(i);
	}

	private String parseChunk(String value){
		StringBuilder parsedValue = new StringBuilder();
		value = removeLeadingZeros(value);
		String[] numberAsArray = value.split("");

		for (int i = 0; i < numberAsArray.length; i++){
			int currentDigit = Integer.parseInt(numberAsArray[i]);
			if (currentDigit == 0)
				continue;
			switch (numberAsArray.length - i - 1) {
				case 0 -> {
					parsedValue.append(" ").append(this.map.get(0)[currentDigit - 1]);
				}
				case 1 -> {
					if (currentDigit == 1) {
						String lastTwoDigits = numberAsArray[numberAsArray.length - 2] + numberAsArray[numberAsArray.length - 1];
						parsedValue.append(" ").append(this.map.get(0)[Integer.parseInt(lastTwoDigits) - 1]);
						return parsedValue.toString();
					} else {
						parsedValue.append(" ").append(this.map.get(1)[currentDigit - 2]);
					}
				}
				case 2 -> {
					parsedValue.append(" ").append(this.map.get(0)[currentDigit - 1]).append(" ").append("Hundred");
				}
			}
		}
		return parsedValue.toString().trim();
	}

	private String reverse(String value){
		String reversedValue = "";
		for(char c : value.toCharArray()){
			reversedValue = c + reversedValue;
		}
		return reversedValue;
	}

	private List<String> reverse(List<String> values){
		List<String> reversedArray = new ArrayList<>();
		for(int i = values.size() - 1; i >= 0; i--){
			reversedArray.add(values.get(i));
		}
		return reversedArray;
	}
}
