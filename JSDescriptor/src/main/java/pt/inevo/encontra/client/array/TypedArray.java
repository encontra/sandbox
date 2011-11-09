package pt.inevo.encontra.client.array;

/**
 * The typed array view types represent a view of an {@link ArrayBuffer} that
 * allows for indexing and manipulation.
 *
 * @author hao1300@gmail.com
 */
public abstract class TypedArray extends ArrayBufferView {

    protected TypedArray() {

    }

    /**
     * Set multiple values, reading input values from the array.
     *
     * The input array and this array may use the same underlying
     * {@link ArrayBuffer}. In this situation, setting the values takes place as
     * if all the data is first copied into a temporary buffer that does not
     * overlap either of the arrays, and then the data from the temporary buffer
     * is copied into the current array.
     *
     * @param array
     */
    public final native void set(Uint8Array array) /*-{
        this.set(array);
    }-*/;

    /**
     * Set multiple values, reading input values from the array.
     *
     * The input array and this array may use the same underlying
     * {@link ArrayBuffer}. In this situation, setting the values takes place as
     * if all the data is first copied into a temporary buffer that does not
     * overlap either of the arrays, and then the data from the temporary buffer
     * is copied into the current array.
     *
     * @param array
     * @param offset indicates the index in the current array where values are
     *                            written.
     */
    public final native void set(Uint8Array array, int offset) /*-{
        this.set(array, offset);
    }-*/;

    /**
     * Gets the length of this array in elements.
     */
    public final native int getLength() /*-{
        return this.length;
    }-*/;
}
