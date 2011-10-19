/*
 * Copyright 2011 Eduardo Yáñez Parareda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elpaso.android.gpro.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class ParserHelper {
    private static final String TAG = "ParserHelper";
    private static final int KB_SIZE = 1024;

    /**
     * Lee el contenido del {@link InputStream}, y convierte todos los caracteres escapados en UTF-8,
     * y finalmente devuelve otro {@link InputStream} con el contenido ya en UTF-8.
     * 
     * @param is Stream de entrada.
     * @return Otro {@link InputStream} codificado en UTF-8.
     * @throws IOException si no se puede leer/cerrar el stream de entrada.
     */
    public static InputStream unscapeStream(InputStream is) throws IOException {
        String content = readStream(is);
        String unscapedContent = unescape(content);
        return new ByteArrayInputStream(unscapedContent.getBytes());
    }

    /**
     * Convierte entidades XML que representan letras acentuadas, eñes, etc... a caracteres UTF.
     * 
     * @param str La cadena que se quiere decodificar.
     * @return la cadena decodificada.
     */
    static String unescape(String str) {
        StringBuilder buf = null;
        String entityName = null;
        char ch = ' ';
        char charAt1 = ' ';
        int entityValue = 0;
        buf = new StringBuilder(str.length());
        for (int i = 0, l = str.length(); i < l; ++i) {
            ch = str.charAt(i);
            if (ch == '&') {
                int semi = str.indexOf(';', i + 1);

                if (semi == -1) {
                    buf.append(ch);
                    continue;
                }
                entityName = str.substring(i + 1, semi);

                if (entityName.charAt(0) == '#') {
                    charAt1 = entityName.charAt(1);
                    if (charAt1 == 'x' || charAt1 == 'X') {
                        entityValue = Integer.valueOf(entityName.substring(2), 16);
                    } else {
                        entityValue = Integer.parseInt(entityName.substring(1));
                    }
                }
                if (entityValue == -1) {
                    buf.append('&');
                    buf.append(entityName);
                    buf.append(';');
                } else {
                    buf.append((char) (entityValue));
                }
                i = semi;
            } else {
                buf.append(ch);
            }
        }
        try {
            return new String(buf.toString().getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return buf.toString();
        }
    }

    /**
     * Lee el contenido de un stream, y al terminar lo cierra.
     * 
     * @param is - InputStream.
     * @return El contenido del stream en una cadena.
     * @throws IOException Si ocurre algún error intentando acceder al stream.
     */
    private static String readStream(InputStream is) throws IOException {
        try {
            StringBuilder content = new StringBuilder();
            byte[] buffer = new byte[KB_SIZE];
            int len;
            while ((len = is.read(buffer)) > 0) {
                content.append(new String(buffer, 0, len));
            }
            return content.toString();
        } catch (IOException e) {
            Log.e(TAG, "Can't open input stream: " +  e.getLocalizedMessage());
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Can't close input stream: " + e.getLocalizedMessage());
                }
            }
        }
    }
}
