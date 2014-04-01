/**
 *  Copyright (c) 2011-2014 Exxeleron GmbH
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.exxeleron.qjava;

import java.util.EventListener;

/**
 * Listener class for kdb+ subscription
 */
public interface QMessagesListener extends EventListener {

    /**
     * Invoked when a new message has been received from a kdb+ server.
     * 
     * @param message
     *            a {@link QMessage} containing received data
     */
    public void messageReceived( QMessage message );

    /**
     * Invoked when an error has been encountered.
     * 
     * @param message
     *            a {@link QErrorMessage} encapsulating error
     */
    public void errorReceived( QErrorMessage message );
}
