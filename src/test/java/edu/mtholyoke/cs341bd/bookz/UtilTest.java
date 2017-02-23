package edu.mtholyoke.cs341bd.bookz;

import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * @author jfoley.
 */
public class UtilTest {
  @Test
  public void encodeParametersInURL() throws Exception {
    Map<String, String> parameters = new TreeMap<>();
    parameters.put("q", "vampires are cool");
    parameters.put("page", Integer.toString(1));
    assertEquals("/search?page=1&q=vampires+are+cool", Util.encodeParametersInURL(parameters, "/search"));
  }

}