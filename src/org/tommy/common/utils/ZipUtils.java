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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	/**
	 * 
	 * @param zipFilePath
	 * @param entryPath
	 * @return
	 * @throws IOException
	 */
	public static byte[] unzip(Path zipFilePath, String entryPath) throws IOException {
		try (ZipFile zipFile = new ZipFile(zipFilePath.toFile()); InputStream is = zipFile.getInputStream(new ZipEntry(entryPath))) {
			if (is == null)
				return null;
			return is.readAllBytes();
		}
	}

	/**
	 * 
	 * @param dir
	 * @param inZip
	 * @param outZip
	 * @throws IOException
	 */
	public static void copyDir(String dir, Path inZip, Path outZip) throws IOException {
		try (ZipFile zipFileIn = new ZipFile(inZip.toFile()); ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZip.toFile()))) {
			Enumeration<? extends ZipEntry> zipEntries = zipFileIn.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry zipEntry  = zipEntries.nextElement();
				String   entryName = zipEntry.getName();
				if (entryName.startsWith(dir + '/') && entryName.length() > dir.length() + 1) {
					Path   p            = Paths.get(entryName);
					String newEntryName = p.subpath(1, p.getNameCount()).toString();
					System.out.println(zipEntry);
					try (InputStream is = zipFileIn.getInputStream(zipEntry)) {
						if (!zipEntry.isDirectory()) {
							ZipEntry outZipEntry = new ZipEntry(newEntryName);
							zos.putNextEntry(outZipEntry);
							zos.write(is.readAllBytes());
							zos.flush();
							zos.closeEntry();
						}
					}
				}
			}
		}
	}
}