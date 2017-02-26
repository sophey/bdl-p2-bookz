package edu.mtholyoke.cs341bd.bookz;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jfoley
 */
public class BookzServer extends AbstractHandler {
  Server jettyServer;
  HTMLView view;
  Model model;

  public BookzServer(String baseURL, int port) throws IOException {
    view = new HTMLView(baseURL);
    jettyServer = new Server(port);
    model = new Model();

    // We create a ContextHandler, since it will catch requests for us under
    // a specific path.
    // This is so that we can delegate to Jetty's default ResourceHandler to
    // serve static files, e.g. CSS & images.
    ContextHandler staticCtx = new ContextHandler();
    staticCtx.setContextPath("/static");
    ResourceHandler resources = new ResourceHandler();
    resources.setBaseResource(Resource.newResource("static/"));
    staticCtx.setHandler(resources);

    // This context handler just points to the "handle" method of this
    // class.
    ContextHandler defaultCtx = new ContextHandler();
    defaultCtx.setContextPath("/");
    defaultCtx.setHandler(this);

    // Tell Jetty to use these handlers in the following order:
    ContextHandlerCollection collection = new ContextHandlerCollection();
    collection.addHandler(staticCtx);
    collection.addHandler(defaultCtx);
    jettyServer.setHandler(collection);
  }

  /**
   * Once everything is set up in the constructor, actually start the server
   * here:
   *
   * @throws Exception if something goes wrong.
   */
  public void run() throws Exception {
    jettyServer.start();
    jettyServer.join(); // wait for it to finish here! We're using threads
    // behind the scenes; so this keeps the main thread around until
    // something can happen!
  }

  /**
   * The main callback from Jetty.
   *
   * @param resource what is the user asking for from the server?
   * @param jettyReq the same object as the next argument, req, just cast to a
   *                 jetty-specific class (we don't need it).
   * @param req      http request object -- has information from the user.
   * @param resp     http response object -- where we respond to the user.
   * @throws IOException      -- If the user hangs up on us while we're
   *                          writing back or
   *                          gave us a half-request.
   * @throws ServletException -- If we ask for something that's not there,
   *                          this might
   *                          happen.
   */
  @Override
  public void handle(String resource, Request jettyReq, HttpServletRequest
      req, HttpServletResponse resp)
      throws IOException, ServletException {
    System.out.println(jettyReq);

    String method = req.getMethod();
    String path = req.getPathInfo();

    if ("GET".equals(method)) {
      if ("/robots.txt".equals(path)) {
        // We're returning a fake file? Here's why: http://www.robotstxt.org/
        resp.setContentType("text/plain");
        try (PrintWriter txt = resp.getWriter()) {
          txt.println("User-Agent: *");
          txt.println("Disallow: /");
        }
        return;
      }

      if ("/searchBook".equals(path)) {
        // search and display book(s)\
        String book = req.getParameter("searchBook");
        System.out.println(book);
      }

      String titleCmd = Util.getAfterIfStartsWith("/title/", path);
      if (titleCmd != null) {
        char firstChar = titleCmd.charAt(0);
        int pageNum = Integer.parseInt(titleCmd.substring(2));
        view.showBookCollection(this.model.getBooksStartingWith(firstChar,
            pageNum), pageNum, model.getNumPagesStartingWithChar(firstChar),
            Character.toString(firstChar), resp);
      }

      // Check for startsWith and substring
      String bookId = Util.getAfterIfStartsWith("/book/", path);
      if (bookId != null) {
        view.showBookPage(this.model.getBook(bookId), resp);
      }

      String flagId = Util.getAfterIfStartsWith("/flag/", path);
      if (flagId != null) {
        view.showFlagPage(this.model.getBook("etext" + flagId), resp);
      }

      String reviewPage = Util.getAfterIfStartsWith("/flagged", path);
      if (reviewPage != null) {
        view.showReviewPage(model.getFlagged(), resp);
      }

      // Front page!
      if ("/front".equals(path) || "/".equals(path)) {
        view.showFrontPage(this.model, resp);
        return;
      }
    } else if ("POST".equals(method)) {
      if (path.contains("/submitFlag")) {
        handleForm(req, resp);
        return;
      }
    }
  }


  /**
   * When a user submits (enter key) or pressed the "Submit" button, we'll
   * get their request in here. This is called explicitly from handle, above.
   *
   * @param req  -- we'll grab the form parameters from here.
   * @param resp -- where to write their "success" page.
   * @throws IOException again, real life happens.
   */
  private void handleForm(HttpServletRequest req,
                          HttpServletResponse resp)
      throws IOException {
    Map<String, String[]> parameterMap = req.getParameterMap();

    // if for some reason, we have multiple "message" fields in our form,
    // just put a space between them, see Util.join.
    // Note that message comes from the name="message" parameter in our
    // <input> elements on our form.
    String problem = Util.join(parameterMap.get("problem"));
    String id = Util.join(parameterMap.get("id"));

    if (problem != null && id != null) {
      // Good, got new message from form.
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);

      model.addFlagged(id, problem);

      submitPage(resp);

      return;
    }

    // user submitted something weird.
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad flag.");
  }

  private void submitPage(HttpServletResponse resp) {
    // Respond!
    try (PrintWriter html = resp.getWriter()) {
      view.printPageStart(html, "Bookz: Flag Submitted!");
      // Print actual redirect directive:
      html.println("<meta http-equiv=\"refresh\" content=\"3; url=front \">");

      // Thank you, link.
      html.println("<div class=\"body\">");
      html.println("<div class=\"thanks\">");
      html.println("<p>Thanks for your flag!</p>");
      html.println("<a href=\"front\">Back to the front page...</a> " +
          "(automatically redirect in 3 seconds).");
      html.println("</div>");
      html.println("</div>");

      view.printPageEnd(html);

    } catch (IOException ignored) {
      // Don't consider a browser that stops listening to us after
      // submitting a form to be an error.
    }
  }

}
