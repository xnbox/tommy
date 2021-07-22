/*
MIT License

Copyright (c) 2021 xnbox team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

HOME:   https://xnbox.github.io
E-Mail: xnbox.team@outlook.com
*/

package org.tommy.common.utils;

import java.lang.reflect.Field;
import java.util.Map;

public class EnvVarUtils {

	/**
	 * 
	 * @return
	 */
	public static Map<String, String> getModifiableEnvironmentMap() {
		Map<String, String> unmodifiableEnv = System.getenv();
		try {
			Class<?> cl    = unmodifiableEnv.getClass();
			Field    field = cl.getDeclaredField("m");
			field.setAccessible(true);
			Map<String, String> modifiableEnv = (Map<String, String>) field.get(unmodifiableEnv);
			return modifiableEnv;
		} catch (Throwable e) {
			// Unable to access writable environment variable map
			return unmodifiableEnv;
		}
	}
}