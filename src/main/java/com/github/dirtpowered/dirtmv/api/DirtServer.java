/*
 * Copyright (c) 2020-2022 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.dirtmv.api;

import com.github.dirtpowered.dirtmv.data.user.UserData;

import javax.annotation.Nullable;
import java.util.List;

//TODO: API
public interface DirtServer {

    /**
     * Gets the full name of project
     *
     * @return full name
     */
    String getName();

    /**
     * Gets the proxy version
     *
     * @return version string
     */
    String getVersion();

    /**
     * Gets the proxy configuration
     *
     * @return {@link Configuration config} object
     */
    Configuration getConfiguration();

    /**
     * Searches for userdata in session registry from provided username
     *
     * @param username - player username
     * @return {@link UserData UserData} object
     */
    @Nullable
    UserData getUserDataFromUsername(String username);

    /**
     * Gets all fully initialized connections data object
     *
     * @return List of {@link UserData UserData}
     */
    List<UserData> getAllConnections();
}
