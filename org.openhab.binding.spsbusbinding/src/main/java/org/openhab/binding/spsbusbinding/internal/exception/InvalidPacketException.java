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
public class InvalidPacketException extends Exception {

    private static final long serialVersionUID = 2329888129618281640L;

    public InvalidPacketException(String message) {
        super(message);
    }

    public InvalidPacketException(Throwable cause) {
        super(cause);
    }

    public InvalidPacketException(String message, Throwable cause) {
        super(message, cause);
    }

}
