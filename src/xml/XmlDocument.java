package xml;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * parse xml document
 * Created by gloria_z on 14-5-19.
 */
public interface XmlDocument {
    public boolean createDocument(String sourceFile, String targerFile);
    public boolean
    parseDocument(String fileName, ArrayList<HashMap<String, String>> items);
    public boolean deleteDocument(String fileName);
}
