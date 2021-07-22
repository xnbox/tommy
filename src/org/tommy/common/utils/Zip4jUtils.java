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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;

public class Zip4jUtils {

	/**
	 * 
	 * @param encryptedZip
	 * @param password
	 * @throws IOException
	 */
	public static void decryptZip(Path encryptedZip, char[] password) throws IOException {
		ZipFile zipFile = new ZipFile(encryptedZip.toFile());
		if (!zipFile.isEncrypted())
			return;
		zipFile.setPassword(password);
		Path tmpPath = Files.createTempDirectory("xnbox-tmp");
		zipFile.extractAll(tmpPath.toString());
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setIncludeRootFolder(false);
		zipParameters.setCompressionLevel(CompressionLevel.FASTEST);
		zipFile.addFolder(tmpPath.toFile(), zipParameters);
		Files.walk(tmpPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

	/**
	 * 
	 * @param dir
	 * @param outZip
	 * @throws IOException
	 */
	public static void zipDir(Path dir, Path outZip) throws IOException {
		ZipFile       zipFile       = new ZipFile(outZip.toFile());
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setIncludeRootFolder(false);
		zipParameters.setCompressionLevel(CompressionLevel.FASTEST);
		zipFile.addFolder(dir.toFile(), zipParameters);
	}
}