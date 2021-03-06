# 🌆 Tommy Web Server
[![License MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](https://github.com/xnbox/tommy/blob/master/LICENSE)
[![Version 10.0.10](https://img.shields.io/badge/version-10.0.10-4DC71F?style=flat-square)](https://github.com/xnbox/tommy/releases)

<h2>About:</h2>
<p><strong>Tommy</strong> is a tiny single-file fully configurable Apache Tomcat web server that allows you to run or embed static and dynamic (JSP and Servlets) web applications.

<p>
An app can be provided as a directory or packed as <abbr title="Web application ARchive">WAR</abbr> (or ZIP) archive that can contain servlets, <abbr title="Java Server Pages">JSP</abbr>, HTML and all other static stuff like CSS, JavaScript, etc.
</p>

<p>
We use the Tommy web server in other our projects:
<ul>
	<li><a href="https://github.com/xnbox/tommybox#readme">TommyBox</a></li>
	<li><a href="https://github.com/xnbox/DeepfakeHTTP#readme">DeepfakeHTTP</li>
</ul>
</p>

<h2>Download:</h2>
Download the <a href="https://github.com/xnbox/tommy/releases/latest">latest release</a> of <code>tommy.jar</code>

<h2>Features:</h2>
<ul>
	<li>No dependencies</li>
	<li>No installation</li>
	<li>No own configuration files, instead, Tommy uses standard Tomcat configuration files</li>
	<li>Single executable jar (starts from ~10Mb)</li>
	<li>Supports custom command line args, stdin, stdout, stderr</li>
</ul>

<h2>Supported web apps:</h2>
<ul>
	<li>WAR files</li>
	<li>Web apps packed as ZIP archives (including standard password-protected ZIPs)</li>
	<li>Exploded web apps (local directories)</li>
	<li>Remote WAR / ZIP files (on HTTP servers)</li>
	<li>Embedded WAR / ZIP files and directories</li>
</ul>

<h2>Usage:</h2>


```text
java -jar tommy.jar [options] [custom arg]...

Options:
        --help                  print help message
        --app <file|dir|URL>    run app from ZIP or WAR archive, directory or URL
        --port <number>         HTTP TCP port number, default: 8080
        --port-ssl <number>     HTTPS TCP port number, default: 8443
        --redirect              redirect HTTP to HTTPS
        --context-path <string> context path, default: /
        --password <string>     provide password (for encrypted ZIP (or WAR) archive)
```


<h2>Run app:</h2>


Run ZIP (or WAR) file:
```bash
java -jar tommy.jar --app MyKillerApp.war
```


Run ZIP (or WAR) file with custom command-line args:
```bash
java -jar tommy.jar --app MyKillerApp.war myparam1 myparam2 ...
```


Run ZIP (or WAR) from web server:
```bash
java -jar tommy.jar --app https://example.com/MyKillerApp.zip
```


Run exploded web app from directory:
```bash
java -jar tommy.jar --app MyKillerAppDir
```


Run password-protected ZIP (or WAR) archive:
```bash
java -jar tommy.jar --app MyKillerApp.zip --password mysecret
```


Run password-protected ZIP (or WAR) archive with custom command-line args:
```bash
java -jar tommy.jar --app MyKillerApp.zip --password mysecret myparam1 myparam2 ...
```


<h2>Embed app:</h2>
<ul>
	<li>Option 1. Copy your app content into the <code>/app</code> directory of the <code>tommy.jar</code>.
	</li>
	<li>Option 2. Pack your app as <code>app.war</code> or <code>app.zip</code> (the archive can be encrypted) and copy the archive to the root directory of the <code>tommy.jar</code>.
	</li>
</ul>

Brand your app by renaming the <code>tommy.jar</code> to the <code>MyKillerApp.jar</code>.


Run embedded app:
```bash
java -jar MyKillerApp.jar
```


Run embedded app with custom command-line args:
```bash
java -jar MyKillerApp.jar myparam1 myparam2 ...
```


Run password-protected embedded app:
```bash
java -jar MyKillerApp.jar --password mysecret
```


Run password-protected embedded app with custom command-line args:
```bash
java -jar MyKillerApp.jar --password mysecret myparam1 myparam2 ...
```


<h2>Tomcat configuration:</h2>
Tommy uses the Apache Tomcat configuration files from <code>/META-INF/tomcat/conf</code> directory of the <code>tommy.jar</code> archive.


<h2>Access to the custom command-line args and system streams programmatically (JNDI):</h2>


```java

// ...somewhere in your Servlet or JSP

InitialContext ctx = new InitialContext();

/* get custom command-line args */
String[] args = (String[]) ctx.lookup("java:comp/env/tommy/args");

/* get standard input (stdin) */
InputStream stdin = (InputStream) ctx.lookup("java:comp/env/tommy/stdin");

/* get standard output (stdout) */
PrintStream stdout = (PrintStream) ctx.lookup("java:comp/env/tommy/stdout");

/* get standard error (stderr) */
PrintStream stderr = (PrintStream) ctx.lookup("java:comp/env/tommy/stderr");

/* get "--app" parameter value */
String app = (String) ctx.lookup("java:comp/env/tommy/app");

// ...


```


<h2>F.A.Q.</h2>

<strong>Q.</strong> How to hide Tomcat stack trace and server info in error pages?
<br><br>
<strong>A.</strong> Steps:
<ol>
<li>Edit the file <code>/META-INF/tomcat/conf/server.xml</code> of the <code>tommy.jar</code> archive</li>
<li>Search for the <code>&lt;Host&gt;</code> element</li>
<li>Just below that line, insert the following <code>&lt;Valve&gt;</code> element:</li>
</ol>

```xml
<Valve className      = "org.apache.catalina.valves.ErrorReportValve"
       showReport     = "false"
       showServerInfo = "false" />
```
<h2></h2>

<strong>Q.</strong> My app failed with <code>java.lang.ClassNotFoundException: javax.servlet.\*</code>
<br><br>
<strong>A.</strong> As a result of the move from Java EE to Jakarta EE, starting from v10, Apache Tomcat supports only the Jakarta EE spec. <code>javax.servlet.\*</code> is no longer supported.
Replace the <code>javax.servlet.\*</code> imports in your code with <code>jakarta.servlet.\*</code>.

