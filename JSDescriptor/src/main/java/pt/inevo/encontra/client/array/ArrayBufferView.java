package pt.inevo.encontra.client.array;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The ArrayBufferView type holds information shared among all of the types of
 * views of {@link ArrayBuffer}s.
 *
 */
public abstract class ArrayBufferView extends JavaScriptObject {

        protected ArrayBufferView() {

        }

        /**
         * @return The {@link ArrayBuffer} that this ArrayBufferView references.
         */
        public final native ArrayBuffer getBuffer() /*-{
                return this.buffer;
        }-*/;

        /**
         * @return The offset of this ArrayBufferView from the start of its
         *                               {@link ArrayBuffer}, in bytes, as fixed at construction time.
         */
        public final native int getByteOffset() /*-{
                return this.byteOffset;
        }-*/;

        /**
         * @return The length of the ArrayBufferView in bytes, as fixed at
         *                               construction time.
         */
        public final native int getByteLength() /*-{
                return this.byteLength;
        }-*/;
}
