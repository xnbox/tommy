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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.CatalinaBaseConfigurationSource;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Common Utilities 
 *
 */
public class CommonUtils {

	/* formatter settings */
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %5$s%6$s%n");
	}

	public static final String APP_WAR = "app.war";
	public static final String APP_ZIP = "app.zip";
	public static final String APP_DIR = "app";

	private static final String WAR_EXT = ".war";

	private static Class       clazz = CommonUtils.class;
	private static ClassLoader cl    = clazz.getClassLoader();

	/**
	 * Copy the Apache Tomcat configuration files
	 *
	 * @param targetPath
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyConfResource(Path targetPath, String fileName) throws IOException {
		try (InputStream is = cl.getResourceAsStream("META-INF/tomcat/conf/" + fileName)) {
			if (is == null)
				return;
			Path path = targetPath.resolve(fileName);
			Files.copy(is, path);
		}
	}

	/**
	 * Copy the keystore file
	 *
	 * @param targetPath
	 * @throws IOException
	 */
	public static void copyKeystoreResource(Path targetPath) throws IOException {
		String fileName = "localhost-rsa.jks";
		try (InputStream is = cl.getResourceAsStream("META-INF/tomcat/conf/keystore/" + fileName)) {
			if (is == null)
				return;
			Path path = targetPath.resolve(fileName);
			Files.copy(is, path);
		}
	}

	/**
	 * Copy the Apache Tomcat "server.xml" file
	 *
	 * @param targetPath
	 * @param fileName
	 * @param xmlDocument
	 * @throws Throwable
	 */
	public static void copyConfDocumentXml(Path targetPath, String fileName, Document xmlDocument) throws Throwable {
		Path         path = targetPath.resolve(fileName);
		OutputStream os   = Files.newOutputStream(path);
		storeXmlDocument(xmlDocument, os);
	}

	public static Path copyWarResource(Path targetPath, String warResource) throws IOException, URISyntaxException {
		int    pos         = warResource.lastIndexOf('/');
		String warFileName = warResource.substring(pos + 1);
		warFileName = changeFileExtToWar(warFileName);
		try (InputStream is = cl.getResourceAsStream(warResource)) {
			if (is == null)
				return null;
			Path path = targetPath.resolve(warFileName);
			Files.copy(is, path);
			return path;
		}
	}

	/**
	 * Store XML document
	 *
	 * @param xmlDocument
	 * @param os
	 * @throws Throwable
	 */
	public static void storeXmlDocument(Document xmlDocument, OutputStream os) throws Throwable {
		Transformer  transformer  = TransformerFactory.newInstance().newTransformer();
		DocumentType documentType = xmlDocument.getDoctype();
		if (documentType != null) {
			String publicId = documentType.getPublicId();
			if (publicId != null)
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
		}
		transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
		Source source = new DOMSource(xmlDocument);
		Result result = new StreamResult(os);
		transformer.transform(source, result);
	}

	/**
	 * Extract resource from war file
	 *
	 * @return
	 * @throws MalformedURLException
	 */
	public static String getWarResource() {
		// Fallback chain: app.war -> app.zip -> app (DIR) -> null

		String warResource;
		URL    warUrl;

		warResource = APP_WAR;
		warUrl      = cl.getResource(warResource);
		if (warUrl != null)
			return warResource;

		warResource = APP_ZIP;
		warUrl      = cl.getResource(warResource);
		if (warUrl != null)
			return warResource;

		warResource = APP_DIR;
		warUrl      = cl.getResource(warResource);
		if (warUrl != null)
			return warResource;

		return null;
	}

	/**
	 * Change file extension to ".war"
	 *
	 * @param warFileName
	 * @return
	 */
	public static String changeFileExtToWar(String warFileName) {
		/* Tomcat will handle file only with ".war" extension */
		if (warFileName.endsWith(WAR_EXT))
			return warFileName;
		int pos = warFileName.lastIndexOf('.');
		if (pos == -1)
			return warFileName + WAR_EXT;
		return warFileName.substring(0, pos) + WAR_EXT;
	}

	/**
	 * Get context path
	 *
	 * @param contextPath
	 * @return
	 */
	public static String getContextPath(String contextPath) {
		if (contextPath == null)
			contextPath = "";
		contextPath = contextPath.trim();
		if (contextPath.isEmpty())
			contextPath = "/";
		if (!contextPath.startsWith("/"))
			contextPath = "/" + contextPath;
		int pos = contextPath.indexOf('/');
		contextPath = contextPath.substring(pos);
		pos         = contextPath.indexOf('?');
		if (pos != -1)
			contextPath = contextPath.substring(0, pos);
		if (contextPath.length() > 1 && contextPath.endsWith("/"))
			contextPath = contextPath.substring(0, contextPath.length() - 1);
		return contextPath;
	}

	/**
	 * Get WAR path
	 *
	 * @param jarFileName
	 * @param webappsPath
	 * @param app
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static Path getWarPath(String jarFileName, Path webappsPath, String app, char[] password) throws Exception {
		Path   warPath;
		String warResource = getWarResource();
		if (warResource == null) { // No embedded app file found
			if (app == null)
				return null;
			// --app parameter specified
			boolean warIsUrl = app.startsWith("file://") || app.startsWith("http://") || app.startsWith("https://");
			warPath = webappsPath.resolve(APP_WAR);
			if (warIsUrl) { // URL provided as --app parameter
				try (InputStream is = new URL(app).openStream()) {
					Files.copy(is, warPath);
				}
			} else { // Directory or file provided as --app parameter
				Path abs = Paths.get(app).toAbsolutePath();
				if (Files.isDirectory(abs)) // External directory
					Zip4jUtils.zipDir(abs, warPath);
				else // External file
					Files.copy(abs, warPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} else if (warResource.equals(APP_DIR)) { // Embedded directory
			warPath = webappsPath.resolve(APP_WAR);
			ZipUtils.copyDir(APP_DIR, Paths.get(jarFileName), warPath);
		} else // Embedded .war or .zip file
			warPath = copyWarResource(webappsPath, warResource);
		Zip4jUtils.decryptZip(warPath, password);
		return warPath;
	}

	/**
	 * Prepare Apache Tomcat configuration
	 *
	 * @param confPath
	 * @param port
	 * @throws Throwable
	 */
	public static void prepareTomcatConf(Path confPath, Path keystorePath, Integer port, Integer sslPort, boolean redirect) throws Throwable {
		/* update server.xml document */
		Document serverXmlDocument = null;
		try (InputStream is = cl.getResourceAsStream("META-INF/tomcat/conf/server.xml")) {
			if (is != null) {
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder        builder        = builderFactory.newDocumentBuilder();
				serverXmlDocument = builder.parse(is);

				if (port != null) {
					Node portNode = (Node) XPathFactory.newInstance().newXPath().compile("/Server/Service/Connector/@port").evaluate(serverXmlDocument, XPathConstants.NODE);
					portNode.setTextContent(Integer.toString(port)); // update node with real TCP port number
				}

				if (sslPort != null) {
					Node redirectPortNode = (Node) XPathFactory.newInstance().newXPath().compile("/Server/Service/Connector/@redirectPort").evaluate(serverXmlDocument, XPathConstants.NODE);
					redirectPortNode.setTextContent(Integer.toString(sslPort)); // update node with real TCP port number
				}

				Node autoDeployNode = (Node) XPathFactory.newInstance().newXPath().compile("/Server/Service/Engine/Host/@autoDeploy").evaluate(serverXmlDocument, XPathConstants.NODE);
				autoDeployNode.setTextContent(Boolean.toString(false));

				/* Add TLS(SSL) support */

				Node    serviceNode      = (Node) XPathFactory.newInstance().newXPath().compile("/Server/Service").evaluate(serverXmlDocument, XPathConstants.NODE);
				Element tlsConnectorNode = serverXmlDocument.createElement("Connector");

				if (sslPort == null)
					sslPort = 8443;

				tlsConnectorNode.setAttribute("port", Integer.toString(sslPort));
				tlsConnectorNode.setAttribute("protocol", "org.apache.coyote.http11.Http11NioProtocol");
				tlsConnectorNode.setAttribute("SSLEnabled", "true");
				//tlsConnectorNode.setAttribute("maxThreads", "150");
				serviceNode.appendChild(tlsConnectorNode);

				Element upgradeProtocolEl = serverXmlDocument.createElement("UpgradeProtocol");
				upgradeProtocolEl.setAttribute("className", "org.apache.coyote.http2.Http2Protocol");
				tlsConnectorNode.appendChild(upgradeProtocolEl);

				Element sslHostConfigEl = serverXmlDocument.createElement("SSLHostConfig");
				tlsConnectorNode.appendChild(sslHostConfigEl);

				Element certificateEl = serverXmlDocument.createElement("Certificate");
				certificateEl.setAttribute("certificateKeystoreFile", "conf/keystore/localhost-rsa.jks");
				certificateEl.setAttribute("certificateKeystorePassword", "changeit");
				certificateEl.setAttribute("type", "RSA");
				sslHostConfigEl.appendChild(certificateEl);
			}
		}

		Document webXmlDocument = null;
		try (InputStream is = cl.getResourceAsStream("META-INF/tomcat/conf/web.xml")) {
			if (is != null) {
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder        builder        = builderFactory.newDocumentBuilder();
				webXmlDocument = builder.parse(is);

				/* Add TLS(SSL) support */
				if (redirect) {

					Node    webAppNode           = (Node) XPathFactory.newInstance().newXPath().compile("/web-app").evaluate(webXmlDocument, XPathConstants.NODE);
					Element securityConstraintEl = webXmlDocument.createElement("security-constraint");
					webAppNode.appendChild(securityConstraintEl);

					Element webResourceCollectionEl = webXmlDocument.createElement("web-resource-collection");
					securityConstraintEl.appendChild(webResourceCollectionEl);

					Element webResourceNameEl = webXmlDocument.createElement("web-resource-name");
					webResourceNameEl.setTextContent("Secured");
					webResourceCollectionEl.appendChild(webResourceNameEl);

					Element urlPatternEl = webXmlDocument.createElement("url-pattern");
					urlPatternEl.setTextContent("/*");
					webResourceCollectionEl.appendChild(urlPatternEl);

					Element userDataConstraintEl = webXmlDocument.createElement("user-data-constraint");
					securityConstraintEl.appendChild(userDataConstraintEl);

					Element transportGuaranteeEl = webXmlDocument.createElement("transport-guarantee");
					transportGuaranteeEl.setTextContent("CONFIDENTIAL");
					userDataConstraintEl.appendChild(transportGuaranteeEl);
				}
			}
		}

		copyConfDocumentXml(confPath, "server.xml", serverXmlDocument);
		copyConfDocumentXml(confPath, "web.xml", webXmlDocument);
		//copyConfResource(confPath, "web.xml");
		copyConfResource(confPath, "tomcat-users.xsd");
		copyConfResource(confPath, "tomcat-users.xml");
		copyConfResource(confPath, "logging.properties");
		copyConfResource(confPath, "jaspic-providers.xsd");
		copyConfResource(confPath, "jaspic-providers.xml");
		copyConfResource(confPath, "context.xml");
		copyConfResource(confPath, "catalina.properties");
		copyConfResource(confPath, "catalina.policy");

		copyKeystoreResource(keystorePath);
	}

	/**
	 * Prepare Apache Tomcat for start up
	 *
	 * @param logger
	 * @param catalinaHome
	 * @param app
	 * @param argz
	 * @return
	 * @throws Throwable
	 */
	public static Tomcat prepareTomcat(Logger logger, String catalinaHome, String app, String[] argz) throws Throwable {
		File catalinaBaseFile = Files.createTempDirectory("catalina_base-").toFile();
		catalinaBaseFile.deleteOnExit();
		String catalinaBase = catalinaBaseFile.getAbsolutePath();

		System.setProperty(Constants.CATALINA_HOME_PROP, catalinaHome);
		System.setProperty(Constants.CATALINA_BASE_PROP, catalinaBase);

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

		/* Why do I need to do this? (fails in with "Caused by: java.lang.Error: factory already defined" without it). */
		TomcatURLStreamHandlerFactory.disable();

		Tomcat tomcat = new Tomcat();

		InitialContext initialContext = new InitialContext();
		initialContext.createSubcontext("java:");
		initialContext.createSubcontext("java:comp");
		initialContext.createSubcontext("java:comp/env");
		initialContext.createSubcontext("java:comp/env/tommy");

		initialContext.bind("java:comp/env/tommy/app", app); // String
		initialContext.bind("java:comp/env/tommy/args", argz); // String[]
		initialContext.bind("java:comp/env/tommy/stdin", System.in); // InputStream
		initialContext.bind("java:comp/env/tommy/stdout", System.out); // PrintStream
		initialContext.bind("java:comp/env/tommy/stderr", System.err); // PrintStream

		NamingManager.setInitialContextFactoryBuilder(environment -> environment1 -> initialContext);

		tomcat.setAddDefaultWebXmlToWebapp(true);
		tomcat.init(new CatalinaBaseConfigurationSource(new File(catalinaHome), catalinaHome + '/' + Catalina.SERVER_XML));

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

		return tomcat;
	}

}
