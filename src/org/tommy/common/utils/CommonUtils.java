package org.tommy.common.utils;

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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

public class CommonUtils {
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %5$s%6$s%n");
	}

	public static final String APP_WAR = "app.war";
	public static final String APP_ZIP = "app.zip";
	public static final String APP_DIR = "app";

	private static final String WAR_EXT = ".war";

	private static Class       clazz = CommonUtils.class;
	private static ClassLoader cl    = clazz.getClassLoader();

	public static void copyConfResource(Path targetPath, String fileName) throws IOException {
		try (InputStream is = cl.getResourceAsStream("META-INF/CONFIG/catalina_home_conf/" + fileName)) {
			if (is == null)
				return;
			Path path = targetPath.resolve(fileName);
			Files.copy(is, path);
		}
	}

	public static void copyConfServerXml(Path targetPath, String fileName, Document serverXmlDocument) throws Throwable {
		Path         path = targetPath.resolve(fileName);
		OutputStream os   = Files.newOutputStream(path);
		storeXmlDocument(serverXmlDocument, os);
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

	public static String getWarResource() throws MalformedURLException {
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

	public static String changeFileExtToWar(String warFileName) {
		/* Tomcat will handle file only with ".war" extension */
		if (warFileName.endsWith(WAR_EXT))
			return warFileName;
		int pos = warFileName.lastIndexOf('.');
		if (pos == -1)
			return warFileName + WAR_EXT;
		return warFileName.substring(0, pos) + WAR_EXT;
	}

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

	public static Path getWarPath(String jarFileName, Path webappsPath, String app, char[] password) throws Exception {
		Path   warPath;
		String warResource = getWarResource();
		if (warResource == null) { // No embedded app file found
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

	public static void prepareTomcatConf(Path confPath, int port) throws Throwable {
		/* update server.xml document */
		Document serverXmlDocument = null;
		try (InputStream is = cl.getResourceAsStream("META-INF/CONFIG/catalina_home_conf/server.xml")) {
			if (is != null) {
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder        builder        = builderFactory.newDocumentBuilder();
				serverXmlDocument = builder.parse(is);

				Node portNode = (Node) XPathFactory.newInstance().newXPath().compile("/Server/Service/Connector/@port").evaluate(serverXmlDocument, XPathConstants.NODE);
				portNode.setTextContent(Integer.toString(port)); // update node with real TCP port number

				Node autoDeployNode = (Node) XPathFactory.newInstance().newXPath().compile("/Server/Service/Engine/Host/@autoDeploy").evaluate(serverXmlDocument, XPathConstants.NODE);
				autoDeployNode.setTextContent(Boolean.toString(false));
			}
		}

		copyConfServerXml(confPath, "server.xml", serverXmlDocument);
		copyConfResource(confPath, "web.xml");
		copyConfResource(confPath, "tomcat-users.xsd");
		copyConfResource(confPath, "tomcat-users.xml");
		copyConfResource(confPath, "logging.properties");
		copyConfResource(confPath, "jaspic-providers.xsd");
		copyConfResource(confPath, "jaspic-providers.xml");
		copyConfResource(confPath, "context.xml");
		copyConfResource(confPath, "catalina.properties");
		copyConfResource(confPath, "catalina.policy");
	}
}