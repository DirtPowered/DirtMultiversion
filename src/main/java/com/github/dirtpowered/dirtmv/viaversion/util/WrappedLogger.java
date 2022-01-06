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

package com.github.dirtpowered.dirtmv.viaversion.util;

import org.pmw.tinylog.Logger;

import java.text.MessageFormat;
import java.util.logging.Level;

public class WrappedLogger extends java.util.logging.Logger {
    private final static String LOG_PREFIX = "[ViaVersion] ";

    public WrappedLogger() {
        super("ViaLogger", null);
    }

    @Override
    public void log(Level level, String message, Object[] objects) {
        log(level, MessageFormat.format(message, objects));
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        if (level == Level.FINE) {
            Logger.debug(LOG_PREFIX + message, throwable);
        } else if (level == Level.WARNING) {
            Logger.warn(LOG_PREFIX + message, throwable);
        } else if (level == Level.SEVERE) {
            Logger.error(LOG_PREFIX + message, throwable);
        } else if (level == Level.INFO) {
            Logger.info(LOG_PREFIX + message, throwable);
        } else {
            Logger.trace(LOG_PREFIX + message, throwable);
        }
    }

    @Override
    public void log(Level level, String message) {
        if (level == Level.FINE) {
            Logger.debug(LOG_PREFIX + message);
        } else if (level == Level.WARNING) {
            Logger.warn(LOG_PREFIX + message);
        } else if (level == Level.SEVERE) {
            Logger.error(LOG_PREFIX + message);
        } else if (level == Level.INFO) {
            Logger.info(LOG_PREFIX + message);
        } else {
            Logger.trace(LOG_PREFIX + message);
        }
    }

    @Override
    public void log(Level level, String message, Object object) {
        if (level == Level.FINE) {
            Logger.debug(LOG_PREFIX + message, object);
        } else if (level == Level.WARNING) {
            Logger.warn(LOG_PREFIX + message, object);
        } else if (level == Level.SEVERE) {
            Logger.error(LOG_PREFIX + message, object);
        } else if (level == Level.INFO) {
            Logger.info(LOG_PREFIX + message, object);
        } else {
            Logger.trace(LOG_PREFIX + message, object);
        }
    }
}
