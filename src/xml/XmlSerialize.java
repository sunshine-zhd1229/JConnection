package xml;

import java.io.*;

/**
 * serialize xml file
 * Created by gloria_z on 14-5-21.
 */
public class XmlSerialize {
    //transform object to byte array
    public static byte[] serializeToByteArray(Serializable obj) {
        if (obj == null)
            return null;

        ByteArrayOutputStream out;
        try {
            out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.close();
            return out.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getClass().getSimpleName());
        }
        return null;
    }

    //transform byte array to object
    public static Object deSerializeFromByteArray(byte[] input) {
        if (input != null)
            try {
                ObjectInputStream in =
                    new ObjectInputStream(new ByteArrayInputStream(input));
                return in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getClass().getName());
            } catch (ClassNotFoundException e) {
                System.out.println(e.getClass().getName());
            }
        return null;
    }

    //serialize object to file
    public static boolean serialize(Serializable obj, String fileName) {
        if (obj == null || fileName == null)
            return false;

        try {
            ObjectOutputStream out = new ObjectOutputStream((
                new BufferedOutputStream(new FileOutputStream(fileName))));
            out.writeObject(obj);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("no such file");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //get object from file
    public static Object deSerialize(String fileName) {
        if (fileName == null)
            return false;

        try {
            ObjectInputStream in = new ObjectInputStream((
                    new BufferedInputStream(new FileInputStream(fileName))));
            Object obj = in.readObject();
            in.close();
            return obj;
        } catch (FileNotFoundException e) {
            System.err.println("no such file");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
