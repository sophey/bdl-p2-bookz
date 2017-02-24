package edu.mtholyoke.cs341bd.bookz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
  Map<String, GutenbergBook> library;
  Map<Character, List<GutenbergBook>> booksStartingWith;

  public Model() throws IOException {
    // start with an empty hash-map; tell it it's going to be big in advance:
    library = new HashMap<>(40000);
    // do the hard work:
    DataImport.loadJSONBooks(library);
    // store books starting with different characters in map
    storeBooksStartingWith();
  }

  /**
   * Add the books starting with each character to the HashMap for easy
   * retrieval.
   */
  public void storeBooksStartingWith() {
    booksStartingWith = new HashMap<>();
    for (GutenbergBook book : library.values()) {
      char first = Character.toUpperCase(book.title.charAt(0));
      if (!booksStartingWith.containsKey(first)) {
        booksStartingWith.put(first, new ArrayList<>(10000));
      }
      booksStartingWith.get(first).add(book);
    }
  }

  public GutenbergBook getBook(String id) {
    return library.get(id);
  }

  public List<GutenbergBook> getBooksStartingWith(char firstChar) {
    return booksStartingWith.get(firstChar);
  }

  public List<GutenbergBook> getRandomBooks(int count) {
    return ReservoirSampler.take(count, library.values());
  }
}
