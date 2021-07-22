# Tommy
[![License MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](https://github.com/xnbox/tommy/blob/master/LICENSE)

<h3>About:</h3>
<p><strong>Tommy</strong> is a single-file executable container that makes it possible to launch a static and dynamic web apps by providing built-in server and container functionality.</p>

<p>
Under the hood Tommy mainly assembled from Apache Tomcat</a> application server and Eclipse "jar-in-jar" library.
The minimal app is just plain <code>index.html</code> or <code>index.jsp</code>.
App can be packed as <abbr title="Web application ARchive">WAR</abbr> or ZIP archive and optionally contain <abbr title="Java Server Pages">JSP</abbr>, servlets and all static stuff like CSS, JavaScript files etc.
</p>


<h3>Download:</h3>
<a href="https://github.com/xnbox/tommy/releases/download/2.14.1/tommy-2.14.1.jar">tommy-2.14.1.jar</a> Latest release


<h3>Features:</h3>
<ul>
	<li>Single cross-platform executable jar (starts from ~10Mb)</li>
	<li>Command line args and environment variables</li>
	<li>Supports standard password protected ZIP archives</li>
</ul>

<h3>Run app:</h3>


Run ZIP (or WAR) file:
```bash
java -jar tommy.jar --app MyKillerApp.war
```


Run ZIP (or WAR) file with custom command-line args:
```bash
java -jar tommy.jar --app MyKillerApp.war myparam1 myparam2
```


Run ZIP (or WAR) from web server:
```bash
java -jar tommy.jar --app https://example.com/MyKillerApp.zip
```


Run app from directory:
```bash
java -jar tommy.jar --app MyKillerAppDir
```


Run encrypted ZIP (or WAR) archive:
```bash
java -jar tommy.jar --app MyKillerApp.zip --password mysecret
```


Show CLI help console output:
```bash
java -jar tommy.jar --help
```


<h3>Embed app:</h3>
<ul>
	<li>Option 1. Copy your app content into the <code>/app</code> directory of the <code>tommy.jar</code>
	</li>
	<li>Option 2. Pack your app as <code>app.war</code> or <code>app.zip</code> (the archive can be encrypted) and copy archive to the root directory of the <code>tommy.jar</code>
	</li>
</ul>

Brand your app by renaming the <code>tommy.jar</code> to the <code>MyKillerApp.jar</code>

Run your embedded app:
```bash
java -jar MyKillerApp.jar
```


Run password-protected embedded app with custom command-line args:
```bash
java -jar MyKillerApp.jar --password mysecret myparam1 myparam2
```

<h3>F.A.Q.</h3>

<strong>Q.</strong> My app failed with <code>java.lang.ClassNotFoundException: javax.servlet.\*</code>
<br>
<strong>A.</strong> As a result of the move from Java EE to Jakarta EE, starts with v10 Apache Tomcat supports only the Jakarta EE spec. <code>javax.servlet.\*</code> is no longer supported.
Replace the <code>javax.servlet.\*</code> imports in your code with <code>jakarta.servlet.\*</code>
