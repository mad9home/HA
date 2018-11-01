/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.s7tcpbinding.internal.config;

/**
 * The {@link S7TCPBindingConfiguration} is responsible for holding configuration information needed to access/poll the
 * S7TCP Controller.
 *
 * @author David
 */
public class S7TCPBindingConfiguration {

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String REFRESH = "refresh";

    public String host = "10.100.0.1";
    public int port = 2012;
    public int refresh = 2; // seconds

}
