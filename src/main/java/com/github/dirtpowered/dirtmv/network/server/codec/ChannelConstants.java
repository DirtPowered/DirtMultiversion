/*
 * Copyright (c) 2020-2021 Dirt Powered
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

package com.github.dirtpowered.dirtmv.network.server.codec;

public class ChannelConstants {
    public static final String DEFAULT_PIPELINE = "minecraft_pipeline";
    public static final String CONNECTION_THROTTLE = "connection_throttle";
    public static final String LEGACY_PING = "legacy_ping";
    public static final String LEGACY_ENCODER = "legacy_encoder";
    public static final String LEGACY_DECODER = "legacy_decoder";
    public static final String TIMEOUT_HANDLER = "timeout";
    public static final String PACKET_ENCRYPTION = "packet_encryption";
    public static final String PACKET_DECRYPTION = "packet_decryption";
    public static final String SERVER_HANDLER = "server_handler";
    public static final String CLIENT_HANDLER = "client_handler";
    public static final String DETECTION_HANDLER = "netty_detection_handler";
    public static final String NETTY_LENGTH_DECODER = "netty_length_decoder";
    public static final String NETTY_LENGTH_ENCODER = "netty_length_encoder";
    public static final String NETTY_PACKET_DECODER = "netty_packet_decoder";
    public static final String NETTY_PACKET_ENCODER = "netty_packet_encoder";
}
