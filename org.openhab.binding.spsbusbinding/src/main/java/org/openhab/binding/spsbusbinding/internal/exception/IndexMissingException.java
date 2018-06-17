/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.spsbusbinding.internal.exception;

/**
 *
 * @author David
 *
 */
public class IndexMissingException extends Exception {

    private static final long serialVersionUID = 7620780437792012873L;

    public IndexMissingException(String message) {
        super(message);
    }

    public IndexMissingException(Throwable cause) {
        super(cause);
    }

    public IndexMissingException(String message, Throwable cause) {
        super(message, cause);
    }

}
