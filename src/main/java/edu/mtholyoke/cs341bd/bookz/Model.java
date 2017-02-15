package edu.mtholyoke.cs341bd.bookz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Model {
	Map<String, GutenbergBook> library;
	private static ObjectMapper jsonLibrary = new ObjectMapper();
	static {
		jsonLibrary.registerModule(new JsonOrgModule());
	}

	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);

		// Load up book data from catalog.jsonl.gz
		try (BufferedReader catalogLines = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("catalog.jsonl.gz")), "UTF-8"))) {

			while(true) {
				String nextLine = catalogLines.readLine();
				if(nextLine == null) break; // done with file
				JSONObject json = jsonLibrary.readValue(nextLine, JSONObject.class);
				System.out.println(json);
			}

		}
	}

	public GutenbergBook getBook(String id) {
		return library.get(id);
	}

	public List<GutenbergBook> getBooksStartingWith(char firstChar) {
		// TODO, maybe it makes sense to not compute these every time.
		char query = Character.toUpperCase(firstChar);
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			char first = Character.toUpperCase(book.title.charAt(0));
			if(first == query) {
				matches.add(book);
			}
		}
		return matches;
	}
}
