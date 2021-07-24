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

package org.tommy.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.startup.Tomcat;
import org.tommy.common.utils.CommonUtils;
import org.tommy.common.utils.LoggerUtils;
import org.tommy.common.utils.ManifestUtils;
import org.tommy.common.utils.SystemProperties;

/*
The manifest file can have any name, but is commonly named manifest.json and served from the root (your website's top-level directory).
The specification suggests the extension should be .webmanifest, but browsers also support .json extensions, which is may be easier for developers to understand.

<link rel="manifest" href="/manifest.json">

https://web.dev/add-manifest/
 */
public class Main {

	private static Class       clazz  = Main.class;
	private static ClassLoader cl     = clazz.getClassLoader();
	private static Logger      logger = LoggerUtils.createLogger(clazz);

	// The range 49152–65535 (215 + 214 to 216 − 1) contains dynamic or private ports that cannot be registered with IANA.
	// This range is used for private or customized services, for temporary purposes, and for automatic allocation of ephemeral ports.

	private static final String ARGS_APP_OPTION          = "--app";
	private static final String ARGS_PASSWORD_OPTION     = "--password";
	private static final String ARGS_HELP_OPTION         = "--help";
	private static final String ARGS_INFO_OPTION         = "--info";
	private static final String ARGS_PORT_OPTION         = "--port";
	private static final String ARGS_CONTEXT_PATH_OPTION = "--contextPath";

	public static void main(String[] args) throws Throwable {
		ManifestUtils.extractBuildDataFromManifest(logger);

		/* parse command line */
		int specialParamCount = 0;

		String jarFileName = args[0];
		specialParamCount += 1;

		String  app         = null;
		char[]  password    = null;
		boolean help        = false;
		boolean info        = false;
		Integer port        = null;
		String  contextPath = "/";
		for (int i = 1; i < args.length; i++) {
			if (args[i].equals(ARGS_APP_OPTION)) {
				app                = args[++i];
				specialParamCount += 2;
			} else if (args[i].equals(ARGS_PASSWORD_OPTION)) {
				password           = args[++i].toCharArray();
				specialParamCount += 2;
			} else if (args[i].equals(ARGS_CONTEXT_PATH_OPTION)) {
				contextPath        = args[++i];
				specialParamCount += 2;
			} else if (args[i].equals(ARGS_PORT_OPTION)) {
				try {
					int portCli = Integer.parseInt(args[++i]);
					if (portCli >= 1 && portCli <= 65535)
						port = portCli;
				} catch (Throwable e) {
					// ignore exception
				}
				specialParamCount += 2;
			} else if (args[i].equals(ARGS_HELP_OPTION)) {
				help               = true;
				specialParamCount += 1;
			} else if (args[i].equals(ARGS_INFO_OPTION)) {
				info               = true;
				specialParamCount += 1;
			}
		}

		if (help) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			sb.append("Tommy " + System.getProperty("build.version") + " " + System.getProperty("build.timestamp") + ". OS: " + SystemProperties.OS_NAME + " (" + SystemProperties.OS_ARCH + "). JVM: " + SystemProperties.JAVA_JAVA_VM_NAME + " (" + SystemProperties.JAVA_JAVA_VERSION + ").\n");
			sb.append("\n");
			if (app != null) {
				sb.append("Usage:\n");
				sb.append("\n");
				sb.append("java -jar tommy.jar [options] [custom arg1] [custom arg2] ...\n");
				sb.append("\n");
				sb.append("Options:\n");
				sb.append("  --help                   print help message\n");
				sb.append("  --app <file | dir | URL> run app from ZIP (or WAR) archive, directory or URL\n");
				sb.append("  --port                   port number, default: 8080\n");
				sb.append("  --contextPath            context path, default: /\n");
				sb.append("  --password <password>    provide password (for encrypted ZIP (or WAR) archive)\n");
			}
			System.out.println(sb);
			System.exit(0);
		}

		/* JAR: META-INF/CONFIG/system.properties - System Properties (optional) */
		try (InputStream is = cl.getResourceAsStream("META-INF/CONFIG/system.properties"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			if (is == null)
				logger.log(Level.WARNING, "\"META-INF/CONFIG/system.properties\" resource (optional) is not found");
			else
				System.getProperties().load(reader);
		} catch (IOException e) { // never throws
			logger.log(Level.SEVERE, "Unknown error", e);
		}

		//		/* JAR: META-INF/CONFIG/env.properties - Environment variables (optional) */
		//		try (InputStream is = cl.getResourceAsStream("META-INF/CONFIG/env.properties"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
		//			if (is == null)
		//				logger.log(Level.WARNING, "\"META-INF/CONFIG/env.properties\" resource (optional) is not found");
		//			else {
		//				Properties properties = new Properties();
		//				properties.load(reader);
		//
		//				Map<String, String> env = EnvVarUtils.getModifiableEnvironmentMap();
		//				for (Enumeration<?> enumeration = properties.propertyNames(); enumeration.hasMoreElements();) {
		//					String key   = (String) enumeration.nextElement();
		//					String value = properties.getProperty(key);
		//					try {
		//						env.put(key, value);
		//					} catch (Throwable e) {
		//						logger.log(Level.WARNING, "Env map is not mdiicable");
		//					}
		//				}
		//			}
		//		} catch (IOException e) { // never throws
		//			logger.log(Level.SEVERE, "Unknown error", e);
		//		}

		File catalinaHomeFile = Files.createTempDirectory("catalina_home-").toFile();
		catalinaHomeFile.deleteOnExit();
		String catalinaHome     = catalinaHomeFile.getAbsolutePath();
		Path   catalinaHomePath = catalinaHomeFile.toPath();
		Path   webappsPath      = catalinaHomePath.resolve("webapps");
		Files.createDirectories(webappsPath);
		Path confPath = catalinaHomePath.resolve("conf");
		Files.createDirectories(confPath);

		Path warPath = CommonUtils.getWarPath(jarFileName, webappsPath, app, password);
		if (warPath == null) {
			logger.log(Level.SEVERE, "App not found.");
			System.exit(0);
		}

		/*
		 * context path
		 */
		contextPath = CommonUtils.getContextPath(contextPath);

		CommonUtils.prepareTomcatConf(confPath, port);

		String[]                    argz   = Arrays.copyOfRange(args, specialParamCount, args.length);
		Tomcat                      tomcat = CommonUtils.prepareTomcat(logger, catalinaHome, app, argz);
		org.apache.catalina.Context ctx    = tomcat.addWebapp(contextPath, warPath.toString());

		logger.info("SERVER: " + ctx.getServletContext().getServerInfo());
		tomcat.start();

		//logger.log(Level.CONFIG, "System Properties: " + System.getProperties());
		//logger.log(Level.CONFIG, "Environment variables: " + System.getenv().toString());
		logger.log(Level.CONFIG, "WAR: " + warPath);

		tomcat.getServer().await();
	}

}