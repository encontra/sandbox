package pt.inevo.encontra.client.array;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;

/**
 * Helper class for converting from Java arrays to JavaScript arrays
 * ({@link JsArrayNumber} and {@link JsArrayInteger}).
 *
 */
public class JsArrayUtils {

        /**
         * Converts from a Java array to a JavaScript array.
         *
         * @param array the Java array to convert from.
         * @return the equivalent JavaScript array.
         */
        public static JsArrayNumber toJsArrayNumber(float... array) {
                JsArrayNumber jsArray = JsArrayNumber.createArray().cast();
                for (float v : array) {
                        jsArray.push(v);
                }
                return jsArray;
        }

        /**
         * Converts from a Java array to a JavaScript array.
         *
         * @param array the Java array to convert from.
         * @return the equivalent JavaScript array.
         */
        public static JsArrayNumber toJsArrayNumberFromDouble(double... array) {
                JsArrayNumber jsArray = JsArrayNumber.createArray().cast();
                for (double v : array) {
                        jsArray.push(v);
                }
                return jsArray;
        }

        /**
         * Converts from a Java array to a JavaScript array.
         *
         * @param array the Java array to convert from.
         * @return the equivalent JavaScript array.
         */
        public static JsArrayInteger toJsArrayInteger(int... array) {
                JsArrayInteger jsArray = JsArrayInteger.createArray().cast();
                for (int v : array) {
                        jsArray.push(v);
                }
                return jsArray;
        }
}
