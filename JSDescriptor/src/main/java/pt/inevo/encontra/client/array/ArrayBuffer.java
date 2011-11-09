package pt.inevo.encontra.client.array;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The ArrayBuffer type describes a buffer used to store data for the
 * TypedArray interface and its subclasses
 *
 * @author hao1300@gmail.com
 */
public final class ArrayBuffer extends JavaScriptObject {

        protected ArrayBuffer() {

        }

        /**
         * Creates a new ArrayBuffer of the given length in bytes. The contents of
         * the ArrayBuffer are initialized to 0.
         *
         * @param length number of bytes
         * @return the new ArrayBuffer
         */
        public static native ArrayBuffer create(int length) /*-{
                return new $wnd.ArrayBuffer(length);
        }-*/;

        /**
         * The length of the ArrayBuffer in bytes, as fixed at construction time.
         */
        public native int getByteLength() /*-{
                return this.byteLength;
        }-*/;
}
