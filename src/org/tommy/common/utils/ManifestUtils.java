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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManifestUtils {

	public static void extractBuildDataFromManifest(Logger logger) {
		/* JAR: META-INF/MANIFEST.MF - Manifest */
		Manifest   manifest           = null;
		Attributes manifestAttributes = null;
		try (InputStream is = ManifestUtils.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			if (is == null) {
				logger.log(Level.SEVERE, "\"META-INF/MANIFEST.MF\" is not found!");
				System.exit(1);
				return;
			}
			manifest           = new Manifest(is);
			manifestAttributes = manifest.getMainAttributes();
		} catch (IOException e) { // never throws
			logger.log(Level.SEVERE, "Unknown error", e);
		}
		/*
		 * Build-Version
		 */
		String buildVersion = manifestAttributes.getValue("Build-Version");
		System.setProperty("build.version", buildVersion);

		/*
		 * Build-Timestamp
		 */
		String buildTimestamp = manifestAttributes.getValue("Build-Timestamp");
		System.setProperty("build.timestamp", buildTimestamp);
	}
}
