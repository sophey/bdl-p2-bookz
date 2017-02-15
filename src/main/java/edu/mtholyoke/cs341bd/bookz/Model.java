package edu.mtholyoke.cs341bd.bookz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
	Map<String, GutenbergBook> library;

	public Model() {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);

		// Load up book data from static/catalog.json
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
