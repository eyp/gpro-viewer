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
package com.elpaso.android.gpro.exceptions;

/**
 * Exception for parsing errors.
 * 
 * @author eduardo.yanez
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = 8982703759193368838L;

    public ParseException() {
        super();
    }

    public ParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseException(String detailMessage) {
        super(detailMessage);
    }

    public ParseException(Throwable throwable) {
        super(throwable);
    }
}
