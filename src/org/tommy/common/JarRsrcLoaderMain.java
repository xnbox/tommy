package org.tommy.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader;

public class JarRsrcLoaderMain {
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		String jarFileName = JarRsrcLoaderMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		jarFileName = URLDecoder.decode(jarFileName, StandardCharsets.UTF_8);
		String newArgs[] = new String[args.length + 1];
		newArgs[0] = jarFileName;
		System.arraycopy(args, 0, newArgs, 1, args.length);
		JarRsrcLoader.main(newArgs);
	}
}
