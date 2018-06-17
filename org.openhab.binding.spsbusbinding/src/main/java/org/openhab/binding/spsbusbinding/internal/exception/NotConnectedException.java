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
public class NotConnectedException extends Exception {

    private static final long serialVersionUID = -7809184880866428571L;

    public NotConnectedException(String message) {
        super(message);
    }

    public NotConnectedException(Throwable cause) {
        super(cause);
    }

    public NotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
