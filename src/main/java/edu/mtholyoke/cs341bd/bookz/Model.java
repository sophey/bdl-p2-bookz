package edu.mtholyoke.cs341bd.bookz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
  Map<String, GutenbergBook> library;
  Map<Character, List<GutenbergBook>> booksStartingWith;
  
  Map<GutenbergBook, String> flagged; // id to problem
  
  List<GutenbergBook> booksWithSearch;

  public final int NUM_PER_PAGE = 20;

  public Model() throws IOException {
    // start with an empty hash-map; tell it it's going to be big in advance:
    library = new HashMap<>(40000);
    // do the hard work:
    DataImport.loadJSONBooks(library);
    // store books starting with different characters in map
    storeBooksStartingWith();
    // set flagged to empty
    flagged = new HashMap<>();
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

  /**
   * Gets the number of pages in a list of books starting with character.
   *
   * @param c
   * @return
   */
  public int getNumPagesStartingWithChar(char c) {
    return getNumPages(getBooksStartingWith(c));
  }

  /**
   * General get number of pages given a list of books.
   *
   * @param books
   * @return
   */
  public int getNumPages(List<GutenbergBook> books) {
    if (books == null)
      return 0;
    return (int) Math.ceil(books.size() / NUM_PER_PAGE);
  }

  /**
   * Gets the books starting with a character on a certain page.
   *
   * @param firstChar
   * @param page
   * @return
   */
  public List<GutenbergBook> getBooksStartingWith(char firstChar, int page) {
    return getPage(getBooksStartingWith(firstChar), page);
  }

  /**
   * More general way to get the page. Takes in a list of books and a page
   * number and generates the list of books on that page.
   *
   * @param books list of books to pull from
   * @param page  page to pull
   * @return page
   */
  public <T> List<T> getPage(List<T> books, int page) {
    int startIndex = (page - 1) * NUM_PER_PAGE;
    if (books == null)
      return null;
    if (startIndex >= books.size()) {
      startIndex = books.size() - books.size() % NUM_PER_PAGE;
    }
    int endIndex = startIndex + NUM_PER_PAGE;
    if (endIndex >= books.size()) {
      endIndex = books.size();
    }

    return books.subList(startIndex, endIndex);
  }

  public List<GutenbergBook> getRandomBooks(int count) {
    return ReservoirSampler.take(count, library.values());
  }


  public Map<GutenbergBook, String> getFlagged() {
    return flagged;
  }

  public void addFlagged(String id, String problem) {
    flagged.put(getBook(id), problem);
  } 
  
  /**
   * Gets the books that have the parameter in its title.
   *
   * @param title
   * @param page
   * @return
   */
  public List<GutenbergBook> searchBooks(String book, int page) {
	storeBooksForSearch(book);
    return getPage(searchBooks(), page);
  }
  
  public List<GutenbergBook> searchBooks() {
	return booksWithSearch;
  }
  
  public void storeBooksForSearch(String book) {
	book = book.toLowerCase();
	
    
	booksWithSearch = new ArrayList<>();
    for (GutenbergBook newBook : library.values()) {
		/**
		 * Search for one word
		 */
    	//split string into array of strings
		//String [] words = newBook.title.split("\\s+");
		//http://stackoverflow.com/questions/4674850/converting-a-sentence-string-to-a-string-array-of-words-in-java
//		for (String word : words) {
//			// check for a non-word character 
//		    // It may also be necessary to adjust the character class
//			word = word.replaceAll("[^\\w]", "");
//			word = word.toLowerCase();
//			if (word.equals(book) || word.contains(book))
//				booksWithSearch.add(newBook);
//		} 	 
		/**
		 * Search for multiple words
		 */
		//newBook.title = newBook.title.toLowerCase();
		String lowerCaseBook = newBook.title.toLowerCase();
		if (lowerCaseBook.contains(book)) {
			booksWithSearch.add(newBook);
		}
    }
  } 
  
  public int getNumPagesForSearch() {
	    return getNumPages(searchBooks());
	  }
  
}
