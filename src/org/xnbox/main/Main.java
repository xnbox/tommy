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

package org.xnbox.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.CatalinaBaseConfigurationSource;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.tommy.common.utils.CommonUtils;
import org.tommy.common.utils.LoggerUtils;
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

	private static final String JAVA_COMP_ENV_XNBOX_APP  = "java:comp/env/xnbox/app";
	private static final String JAVA_COMP_ENV_XNBOX_ARGS = "java:comp/env/xnbox/args";

	private static final String ARGS_APP_OPTION          = "--app";
	private static final String ARGS_PASSWORD_OPTION     = "--password";
	private static final String ARGS_HELP_OPTION         = "--help";
	private static final String ARGS_ABOUT_OPTION        = "--about";
	private static final String ARGS_INFO_OPTION         = "--info";
	private static final String ARGS_PORT_OPTION         = "--port";
	private static final String ARGS_CONTEXT_PATH_OPTION = "--contextPath";

	public static void main(String[] args) throws Throwable {
		/* JAR: META-INF/MANIFEST.MF - Manifest */
		Manifest   manifest           = null;
		Attributes manifestAttributes = null;
		try (InputStream is = cl.getResourceAsStream("META-INF/MANIFEST.MF"); Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
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
		System.setProperty("xnbox.build.version", buildVersion);

		/*
		 * Build-Timestamp
		 */
		String buildTimestamp = manifestAttributes.getValue("Build-Timestamp");
		System.setProperty("xnbox.build.timestamp", buildTimestamp);

		/* parse command line */
		int specialParamCount = 0;

		String jarFileName = args[0];
		specialParamCount += 1;

		String  app         = null;
		char[]  password    = null;
		boolean help        = false;
		boolean about       = false;
		boolean info        = false;
		int     port        = 8080;
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
			} else if (args[i].equals(ARGS_ABOUT_OPTION)) {
				about              = true;
				specialParamCount += 1;
			} else if (args[i].equals(ARGS_INFO_OPTION)) {
				info               = true;
				specialParamCount += 1;
			}
		}

		if (help) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			sb.append("java -jar xn.jar [options] [custom arg1] [custom arg2] ...\n");
			sb.append("\n");
			sb.append("Options:\n");
			sb.append("  --help                   print help message\n");
			sb.append("  --info                   print system info\n");
			if (app == null)
				sb.append("  --app <file | dir | URL> run app from ZIP (or WAR) archive, directory or URL\n");
			sb.append("  --password <password>    provide password (for encrypted ZIP (or WAR) archive)\n");

			System.out.println(sb);
			System.exit(0);
		} else if (info) {
			System.out.println("xnbox.build.version:   " + buildVersion);
			System.out.println("xnbox.build.timestamp: " + buildTimestamp);
			System.out.println("os.name:               " + SystemProperties.OS_NAME);
			System.out.println("os.arch:               " + SystemProperties.OS_ARCH);
			System.out.println("java.vm.name:          " + SystemProperties.JAVA_JAVA_VM_NAME);
			System.out.println("java.version:          " + SystemProperties.JAVA_JAVA_VERSION);
			System.out.println("java.class.version:    " + SystemProperties.JAVA_JAVA_CLASS_VERSION);
			System.out.println("java.awt.headless:     " + SystemProperties.JAVA_AWT_HEADLESS);
			System.exit(0);
		}

		String[]       argz           = Arrays.copyOfRange(args, specialParamCount, args.length);
		InitialContext initialContext = new InitialContext() {

											private Map<String, Object> table = new HashMap<>();

											@Override
											public void bind(String key, Object value) {
												table.put(key, value);
											}

											@Override
											public Object lookup(String key) throws NamingException {
												return table.get(key);
											}
										};
		initialContext.bind(JAVA_COMP_ENV_XNBOX_APP, app);
		initialContext.bind(JAVA_COMP_ENV_XNBOX_ARGS, argz);

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
		/*
		 * context path
		 */
		contextPath = CommonUtils.getContextPath(contextPath);

		CommonUtils.prepareTomcatConf(confPath, port);

		File catalinaBaseFile = Files.createTempDirectory("catalina_base-").toFile();
		catalinaBaseFile.deleteOnExit();
		String catalinaBase = catalinaBaseFile.getAbsolutePath();

		Tomcat tomcat = startTomcat(logger, catalinaHome, catalinaBase, contextPath, warPath);
		NamingManager.setInitialContextFactoryBuilder(environment -> environment1 -> initialContext);
		//logger.log(Level.CONFIG, "System Properties: " + System.getProperties());
		//logger.log(Level.CONFIG, "Environment variables: " + System.getenv().toString());
		logger.log(Level.CONFIG, "Build Timestamp: " + buildTimestamp);
		logger.log(Level.CONFIG, "WAR: " + warPath);

		tomcat.getServer().await();
	}

	private static Tomcat startTomcat(Logger logger, String catalinaHome, String catalinaBase, String contextPath, Path warPath) throws Throwable {
		System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Constants.CATALINA_HOME_PROP, catalinaHome);
		System.setProperty(Constants.CATALINA_BASE_PROP, catalinaBase);

		/* Why do I need to do this? (fails in with "Caused by: java.lang.Error: factory already defined" without it). */
		TomcatURLStreamHandlerFactory.disable();

		Tomcat tomcat = new Tomcat();
		tomcat.setAddDefaultWebXmlToWebapp(true);
		tomcat.init(new CatalinaBaseConfigurationSource(new File(catalinaHome), catalinaHome + '/' + Catalina.SERVER_XML));
		org.apache.catalina.Context ctx = tomcat.addWebapp(contextPath, warPath.toString());

		logger.info("SERVER: " + ctx.getServletContext().getServerInfo());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (tomcat != null)
					try {
						tomcat.stop();
					} catch (LifecycleException e) {
						e.printStackTrace();
					}
			}
		});

		tomcat.start();
		return tomcat;
	}

}