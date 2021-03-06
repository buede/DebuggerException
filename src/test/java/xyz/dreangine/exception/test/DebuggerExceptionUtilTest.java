package xyz.dreangine.exception.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xyz.dreangine.exception.util.DebuggerExceptionUtil;

public class DebuggerExceptionUtilTest {
	private File propsFile;
	private String propsFileString, propsFileFullString, className, methodName, falseMethodName, autoGenMsg;
	private Properties props;
	private FileInputStream fis;
	private FileOutputStream fos;
	
	@BeforeEach
	public void setup() throws Exception {
		this.className = "com.dreangine.exception.DebuggerException"; //$NON-NLS-1$
		this.methodName = "com.dreangine.exception.DebuggerException.isDebug"; //$NON-NLS-1$
		this.falseMethodName = "com.dreangine.exception.FalseClass.falseMethod"; //$NON-NLS-1$
		this.propsFileString = "exceptions_test"; //$NON-NLS-1$
		this.propsFileFullString = this.propsFileString + ".properties"; //$NON-NLS-1$
		this.propsFile = new File(this.propsFileFullString);
		if(this.propsFile.exists()) {
			this.propsFile.delete();
		}
		this.propsFile.createNewFile();
		this.props = new Properties();
		this.fis = new FileInputStream(this.propsFile);
		this.fos = new FileOutputStream(this.propsFile);
		this.autoGenMsg = "<auto-generated message>"; //$NON-NLS-1$
	}

	@Test
	public void testGenerateMissingEntries() throws Exception {
		long lenghtStart, lenghtEnd;
		String dirString, debugRegex, lineRegex, defaultMessageRegex;
		List<String> listDefaults, returnListDefaults;
		File dir;
		
		dirString = "bin"; //$NON-NLS-1$
		debugRegex = "DEBUG"; //$NON-NLS-1$
		lineRegex = "line"; //$NON-NLS-1$
		defaultMessageRegex="defaultMessage"; //$NON-NLS-1$
		dir = new File(dirString);
		listDefaults = new ArrayList<>();
		listDefaults.add(debugRegex);
		listDefaults.add(lineRegex);
		listDefaults.add(defaultMessageRegex);
		DebuggerExceptionUtil.generateMissingEntries(dir, this.propsFile);
		lenghtStart = this.propsFile.length();
		returnListDefaults = DebuggerExceptionUtil.checkPropsFile(this.propsFile, listDefaults);
		assertEquals(0, returnListDefaults.size());
		DebuggerExceptionUtil.generateMissingEntries(dir, this.propsFile);
		lenghtEnd = this.propsFile.length();
		assertEquals(lenghtStart, lenghtEnd);
	}

	@Test
	public void testCheckPropsFile() throws Exception {
		List<String> methods, returnMethods;
		
		methods = new ArrayList<>();
		methods.add(this.methodName);
		returnMethods = DebuggerExceptionUtil.checkPropsFile(this.propsFile, methods);
		assertEquals(methods, returnMethods);
		this.propsFile.delete();
		returnMethods = DebuggerExceptionUtil.checkPropsFile(this.propsFile, methods);
		assertEquals(methods, returnMethods);
	}
	
	@Test
	public void testCheckPropsFileForUnused() throws Exception {
		Map<String, String> returnMethods;
		List<String> methods;
		
		this.props.setProperty(this.methodName, "Some strange error message"); //$NON-NLS-1$
		this.props.store(this.fos, "No comments"); //$NON-NLS-1$
		methods = new ArrayList<>();
		methods.add(this.falseMethodName);
		returnMethods = DebuggerExceptionUtil.checkPropsFileForUnused(this.propsFile, methods);
		assertTrue(returnMethods.size() > 0);
		assertFalse(methods.containsAll(returnMethods.keySet()));
	}

	@Test
	public void testUpdatePropsFile() throws Exception {
		Map<String, List<String>> cmMap;
		List<String> methods;
		String propValue, autoGenMsgLocal;
		
		autoGenMsgLocal = "<auto-generated message>"; //$NON-NLS-1$
		methods = new ArrayList<>();
		methods.add(this.methodName);
		cmMap = new HashMap<>();
		cmMap.put(this.className, methods);
		DebuggerExceptionUtil.updatePropsFile(this.propsFile, cmMap);
		this.props.load(this.fis);
		propValue = this.props.getProperty(this.methodName);
		assertNotNull(propValue);
		assertEquals(propValue, autoGenMsgLocal);
	}
	
	@Test
	public void testUpdatePropsFileUnused() throws Exception {
		Map<String, List<String>> cmMap;
		List<String> methods, unused;
		String propValue;
		
		this.props.setProperty(this.falseMethodName, this.autoGenMsg);
		this.props.store(this.fos, "No comments"); //$NON-NLS-1$
		methods = new ArrayList<>();
		methods.add(this.methodName);
		cmMap = new HashMap<>();
		cmMap.put(this.className, methods);
		unused = new ArrayList<>();
		unused.add(this.falseMethodName);
		DebuggerExceptionUtil.updatePropsFile(this.propsFile, cmMap, unused);
		this.props.load(this.fis);
		propValue = this.props.getProperty(this.methodName);
		assertNotNull(propValue);
		assertEquals(propValue, this.autoGenMsg);
	}
	
	@AfterEach
	public void reset() throws IOException {
		this.fis.close();
		this.fos.close();
		this.props.clear();
		this.propsFile.delete();
	}
}