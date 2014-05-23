package xml;

import java.io.Serializable;

/**
 * Package the object
 * Created by gloria_z on 14-5-21.
 */
public class SerializableObject<T> implements Serializable {
    private T object;

    public SerializableObject(T obj) {
        object = obj;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
