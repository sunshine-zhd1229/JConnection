package xml;

import org.dom4j.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * parse xml document
 * Created by gloria_z on 14-5-19.
 */
public interface XmlDocument {
    public Document createDocument(String sourceFile);
    public boolean
    parseDocument(Document document, ArrayList<HashMap<String, String>> items);
    public boolean deleteDocument(String fileName);
}
