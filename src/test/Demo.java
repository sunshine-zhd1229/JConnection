package test;

import org.dom4j.Document;
import xml.DocumentCrypt;
import xml.SerializableObject;
import xml.XmlOperator;
import xml.XmlSerialize;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * test demo
 * Created by gloria_z on 14-5-23.
 */
public class Demo {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String[] items = {"user", "name", "driver", "url", "password"};
        XmlOperator opt = new XmlOperator(items);
        Document document = opt.createDocument("config.ini");
        ArrayList<HashMap<String, String>> list = new
                ArrayList<HashMap<String, String>>();
        opt.parseDocument(document, list);
        for (HashMap map : list)
            System.out.println(map);
        DocumentCrypt crypt = new DocumentCrypt("RSA", 2048);
        crypt.initialize();
        crypt.saveKeys("key.dat");
        HashMap<String, String> map = crypt.fetchKey("key.dat");
        SerializableObject<Document> o =
                new SerializableObject<Document>(document);

        byte[] temp = XmlSerialize.serializeToByteArray(o);
        byte[] keydata = crypt.encrypt(map.get(crypt.PUBLIC_KEY), temp);

        XmlSerialize.serialize(keydata, "test.xml");

        keydata = (byte[]) XmlSerialize.deSerialize("test.xml");
        temp = crypt.decrypt(map.get(crypt.PRIVATE_KEY), keydata);
        o = (SerializableObject<Document>)
                XmlSerialize.deSerializeFromByteArray(temp);
        list.clear();
        opt.parseDocument(o.getObject(), list);
        for (HashMap map1 : list)
            System.out.println(map1);
    }
}
