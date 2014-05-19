package xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

import static xml.XmlOperator.Status.*;

/**
 * operate xml document: create, parse, delete
 * Created by gloria_z on 14-5-19.
 */
public class XmlOperator implements XmlDocument {
    //config items
    private Set<String> keys;

    public XmlOperator() {
        keys = new HashSet<String>();
    }

    public XmlOperator(Set<String> keys) {
        this.keys = new HashSet<String>(keys);
    }

    public XmlOperator(String[]keys) {
        this.keys = new HashSet<String>();
        Collections.addAll(this.keys, keys);
    }

    protected enum Status {
        FIND_BEGINNING,
        FIND_GROUP,
        BEGIN_COPY,
        FINISH,
        ERROR {
            public void
            errorMessage(String message, int lineNumber, String line) {
                System.err.println("Config file error: " + message + ", line "
                        + lineNumber +": " + line);
            }
        };
        public void
        errorMessage(String message, int lineNumber, String line){}

        public void
        errorMessage(String message){
            System.err.println("Config file error: " + message);
        }
    }

    @Override
    public boolean createDocument(String sourceFile, String targerFile) {
        if (sourceFile == null || targerFile == null)
            return false;
        String errMessage = "";
        BufferedReader inputFile;
        OutputStream out;
        try {
            //open the source/target file.
            File file = new File(sourceFile);
            if (!file.exists()) {
                ERROR.errorMessage("config file doesn't exists");
                return false;
            }

            inputFile = new BufferedReader(new FileReader(file));
            //create a document
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("config");
            Element group = null;
            //parse sourceFile
            String line;
            Status status = FIND_BEGINNING;
            int lineNumber = 0;
            while ((line = inputFile.readLine()) != null) {
                lineNumber++;
                switch(status) {
                    case FIND_BEGINNING:
                        //config file start with "begin" and end with "end"
                        if (line.toLowerCase().equals("begin"))
                            status = FIND_GROUP;
                        break;
                    case FIND_GROUP:
                        //every config segment start with "group" and end whit
                        //"group end"
                        if (line.toLowerCase().equals("group")) {
                            group = root.addElement(line);
                            status = BEGIN_COPY;
                        } else if (line.toLowerCase().equals("end"))
                            status = FINISH;
                        break;
                    case BEGIN_COPY:
                        //config item: item=value
                        if (line.contains("=")) {
                            String[] pairs = line.split("=");
                            if (pairs.length != 2) {
                                status = ERROR;
                            } else {
                                pairs[0] = pairs[0].trim();
                                pairs[1] = pairs[1].trim();
                                if (keys.contains(pairs[0])) {
                                    Element item = group.addElement(pairs[0]);
                                    item.addText(pairs[1]);
                                } else {
                                    status = ERROR;
                                    errMessage = "can't resolve " + pairs[0];
                                }
                            }
                        } else if(line.equals("end group")) {
                            status = FIND_GROUP;
                        } else {
                            status = ERROR;
                        }
                        break;
                }
                if (status == FINISH) {
                    //write into the target file
                    out =
                    new BufferedOutputStream(new FileOutputStream(targerFile));
                    OutputFormat format = OutputFormat.createPrettyPrint();
                    format.setEncoding("UTF-8");
                    XMLWriter writer = new XMLWriter(out, format);
                    writer.write(document);
                    writer.close();
                    out.close();
                    inputFile.close();
                    return true;
                }
                if (status == ERROR) {
                    //print error
                    ERROR.errorMessage(errMessage, lineNumber, line);
                    inputFile.close();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override @SuppressWarnings("unchecked")
    public boolean
    parseDocument(String fileName, ArrayList<HashMap<String, String>> items) {
        SAXReader reader = new SAXReader();
        File file = new File(fileName);
        if (!file.exists()) {
            ERROR.errorMessage("config file doesn't exists");
            return false;
        }

        Document document;
        try {
            document = reader.read(file);
            //get root
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            //get child node
            for (Element element : childElements) {
                HashMap<String, String> map = new HashMap<String, String>();
                List<Element> ele = element.elements();
                for (Element e : ele)
                    map.put(e.getName(), e.getText());
                items.add(map);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteDocument(String fileName) {
        if (fileName == null)
            return false;
        File file = new File(fileName);
        if (file.exists())
            return false;
        return file.isFile() && file.delete();
    }

    public boolean setConfigItems(Set<String> keys) {
        if (keys == null)
            return false;
        for (String key : keys)
            this.keys.add(key);
        return true;
    }

    public boolean setConfigItems(String[]keys) {
        if (keys == null)
            return false;
        Collections.addAll(this.keys, keys);
        return false;
    }

    public static void main(String[] args) {
        String[]items = {"user", "name", "driver", "url", "password"};
        XmlOperator opt = new XmlOperator(items);
        opt.createDocument("config.ini", "src/config/config.xml");
        ArrayList<HashMap<String, String>> list = new
                ArrayList<HashMap<String, String>>();
        opt.parseDocument("src/config/config.xml", list);
        for (HashMap map : list)
            System.out.println(map);
        opt.deleteDocument("src/config/config.xml");
    }
}
