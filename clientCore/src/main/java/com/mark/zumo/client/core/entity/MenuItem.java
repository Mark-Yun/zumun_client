package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity
public class MenuItem implements Serializable {

    private static final String TAG = "MenuItem";
    @PrimaryKey public final long id;
    public final byte[] image;
    public final long storeId;
    public final int price;
    public final long createdDate;
    public final long modifiedDate;

    public MenuItem(long id, byte[] image, long storeId, int price, long createdDate, long modifiedDate) {
        this.id = id;
        this.image = image;
        this.storeId = storeId;
        this.price = price;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static MenuItem deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        MenuItem menuItem = (MenuItem) is.readObject();
        in.close();
        is.close();
        return menuItem;
    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(this);
            byte[] bytes = out.toByteArray();
            os.close();
            out.close();

            return bytes;
        } catch (IOException e) {
            Log.e(TAG, "serialize: ", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
