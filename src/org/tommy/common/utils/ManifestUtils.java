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
