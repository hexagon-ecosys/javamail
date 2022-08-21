/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.mail.pop3;

import java.io.IOException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.test.TestServer;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Test POP3Store.
 *
 * @author sbo
 * @author Bill Shannon
 */
public final class POP3StoreTest {

    /**
     * Check is connected.
     */
    @Test
    public void testIsConnected() {
        TestServer server = null;
        try {
            final POP3Handler handler = new POP3HandlerNoopErr();
            server = new TestServer(handler);
            server.start();

            final Properties properties = new Properties();
            properties.setProperty("mail.pop3.host", "localhost");
            properties.setProperty("mail.pop3.port", "" + server.getPort());
            final Session session = Session.getInstance(properties);
            //session.setDebug(true);

            final Store store = session.getStore("pop3");
            try {
                store.connect("test", "test");
                final Folder folder = store.getFolder("INBOX");
                folder.open(Folder.READ_ONLY);

                // Check
                assertFalse(folder.isOpen());
            } finally {
                store.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (server != null) {
                server.quit();
            }
        }
    }

    /**
     * Check that enabling APOP with a server that doesn't support APOP
     * (and doesn't return any information in the greeting) doesn't fail.
     */
    @Test
    public void testApopNotSupported() {
        TestServer server = null;
        try {
            final POP3Handler handler = new POP3HandlerNoGreeting();
            server = new TestServer(handler);
            server.start();

            final Properties properties = new Properties();
            properties.setProperty("mail.pop3.host", "localhost");
            properties.setProperty("mail.pop3.port", "" + server.getPort());
            properties.setProperty("mail.pop3.apop.enable", "true");
            final Session session = Session.getInstance(properties);
            //session.setDebug(true);

            final Store store = session.getStore("pop3");
            try {
                store.connect("test", "test");
		// success!
            } finally {
                store.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (server != null) {
                server.quit();
            }
        }
    }

    /**
     * Check whether POP3 XOAUTH2 connection can be established using single line authentication format (default)
     */
    @Test
    public void testXOAUTH2POP3Connection() {
        TestServer server = null;

        try {
            final POP3Handler handler = new POP3HandlerXOAUTH();
            server = new TestServer(handler);
            server.start();

            final Properties properties = new Properties();
            properties.setProperty("mail.pop3.host", "localhost");
            properties.setProperty("mail.pop3.port", "" + server.getPort());
            properties.setProperty("mail.pop3.auth.mechanisms", "XOAUTH2");

            final Session session = Session.getInstance(properties);

            final POP3Store store = (POP3Store) session.getStore("pop3");
            try {
                store.protocolConnect("localhost", server.getPort(), "test", "test");
            } catch (Exception ex) {
                System.out.println(ex);
                ex.printStackTrace();
                fail(ex.toString());
            } finally {
                store.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (server != null) {
                server.quit();
            }
        }
    }

    /**
     * Check whether POP3 XOAUTH2 connection can be established using single line authentication format
     * when the authentication format has ben set
     * using: mail.pop3.auth.xoauth2.two.line.authentication.format property
     */
    @Test
    public void testXOAUTH2POP3ConnectionWithSingleLineAuthenticationFlag() {
        TestServer server = null;

        try {
            final POP3Handler handler = new POP3HandlerXOAUTH();
            server = new TestServer(handler);
            server.start();

            final Properties properties = new Properties();
            properties.setProperty("mail.pop3.host", "localhost");
            properties.setProperty("mail.pop3.port", "" + server.getPort());
            properties.setProperty("mail.pop3.auth.mechanisms", "XOAUTH2");
            properties.setProperty("mail.pop3.disablecapa", "false");
            properties.setProperty("mail.pop3.auth.xoauth2.two.line.authentication.format", "false");

            final Session session = Session.getInstance(properties);

            final POP3Store store = (POP3Store) session.getStore("pop3");
            try {
                store.protocolConnect("localhost", server.getPort(), "test", "test");
            } catch (Exception ex) {
                System.out.println(ex);
                ex.printStackTrace();
                fail(ex.toString());
            } finally {
                store.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (server != null) {
                server.quit();
            }
        }
    }

    /**
     * Check whether POP3 XOAUTH2 authentication method is invoked using two line authentication format
     * using: mail.pop3.auth.xoauth2.two.line.authentication.format property
     */
    @Test
    public void testXOAUTH2POP3ConnectionWithTwoLineAuthenticationFlag() {
        TestServer server = null;

        try {
            final POP3Handler handler = new POP3HandlerXOAUTH();
            server = new TestServer(handler);
            server.start();

            final Properties properties = new Properties();
            properties.setProperty("mail.pop3.host", "localhost");
            properties.setProperty("mail.pop3.port", "" + server.getPort());
            properties.setProperty("mail.pop3.auth.mechanisms", "XOAUTH2");
            properties.setProperty("mail.pop3.disablecapa", "false");
            properties.setProperty("mail.pop3.auth.xoauth2.two.line.authentication.format", "true");

            final Session session = Session.getInstance(properties);

            final POP3Store store = (POP3Store) session.getStore("pop3");
            try {
                store.protocolConnect("localhost", server.getPort(), "test", "test");
            } catch (Exception ex) {
                assertTrue(ex instanceof AuthenticationFailedException);
                assertTrue("We are expecting an exception here as the test server " +
                        "do not allow for two lane authentication format ", ex.toString().contains("unknown command"));
            } finally {
                store.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (server != null) {
                server.quit();
            }
        }
    }

    /**
     * Custom handler of AUTH command.
     *
     * @author Mateusz Marzęcki
     */
    private static class POP3HandlerXOAUTH extends POP3Handler {
        @Override
        public void auth() throws IOException {
            this.println("+OK POP3 server ready");
        }

        @Override
        public void capa() throws IOException {
            this.writer.println("+OK");
            this.writer.println("SASL PLAIN XOAUTH2");
            this.println(".");
        }
    }

    /**
     * Custom handler. Returns ERR for NOOP.
     *
     * @author sbo
     */
    private static final class POP3HandlerNoopErr extends POP3Handler {

        /**
         * {@inheritDoc}
         */
	@Override
        public void noop() throws IOException {
            this.println("-ERR");
        }
    }

    /**
     * Custom handler.  Don't include any extra information in the greeting.
     *
     * @author Bill Shannon
     */
    private static final class POP3HandlerNoGreeting extends POP3Handler {

        /**
         * {@inheritDoc}
         */
	@Override
        public void sendGreetings() throws IOException {
            this.println("+OK");
        }
    }
}
