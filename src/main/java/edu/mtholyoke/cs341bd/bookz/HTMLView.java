package edu.mtholyoke.cs341bd.bookz;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HTMLView {

	private String metaURL;

	public HTMLView(String baseURL) {
		this.metaURL = "<base href=\"" + baseURL + "\">";
	}

	/**
	 * HTML top boilerplate; put in a function so that I can use it for all the
	 * pages I come up with.
	 * 
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 * @param title
	 *            the title of the page, since that goes in the header.
	 */
	void printPageStart(PrintWriter html, String title) {
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		html.println("  <a href='/front'><h1 class=\"logo\">Writr</h1></a>");
	}

	public String getStaticURL(String resource) {
		return "static/" + resource;
	}

	/**
	 * HTML bottom boilerplate; close all the tags we open in
	 * printPageStart.
	 *
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 */
	void printPageEnd(PrintWriter html) {
		html.println("  </body>");
		html.println("</html>");
	}

	void showFrontPage(Model model, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");

			printPageEnd(html);
		}
	}

	public void showBookPage(GutenbergBook book, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			printBookHTML(html, book);
			printPageEnd(html);
		}
	}

	private void printBookHTML(PrintWriter html, GutenbergBook book) {
		html.println("<div class='book'>");
		html.println("<div class='title'>"+book.title+"</div>");
		html.println("<div class='creator'>"+book.creator+"</div>");
		// TODO, finish up fields.
		html.println("</div>");
	}

	public void showBookCollection(List<GutenbergBook> theBooks, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");

			for (int i = 0; i < Math.min(20,theBooks.size()); i++) {
				printBookHTML(html, theBooks.get(i));
			}

			printPageEnd(html);
		}
	}
}
