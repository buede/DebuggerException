package xyz.dreangine.exception.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.Test;

import xyz.dreangine.exception.DebuggerException;

public class DebuggerExceptionTest {

	@Test
	public void testDebuggerException() {
		DebuggerException de = new DebuggerException();
		assertNotNull(de);
	}

	@Test
	public void testDebuggerExceptionWithException() {
		Exception e = new Exception();
		DebuggerException de = new DebuggerException(e);
		assertNotNull(de);
	}
	
	@Test
	public void testDebuggerExceptionWithEmptyException() {
		Exception e = null;
		assertNull(e);
		assertThrows(NullPointerException.class, () -> new DebuggerException(e));
	}

	@Test
	public void testToString() {
		DebuggerException de = new DebuggerException();
		assertNotNull(de.toString());
		System.out.println(de);
	}
	
	@Test
	public void testSetDebug() {
		DebuggerException de = new DebuggerException();
		System.out.println(de);
		de.setDebug(!de.isDebug());
		System.out.println(de);
	}

	@Test
	public void testStacks() {
		try {
			exceptionWithMessageGenerator(true);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			exceptionWithMessageGenerator(false);
		} catch (Exception e) {
			System.out.println(new DebuggerException(e));
		}
		
		try {
			exceptionWithMessageGenerator(false);
		} catch (Exception e) {
			System.out.println(new DebuggerException());
		}
		
		try {
			exceptionGenerator();
		} catch (Exception e) {
			System.out.println(new DebuggerException(e));
		}
	}
	
	@Test
	public void testStacksWithWrapper() {
		try {
			exceptionWrapper(true);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			exceptionWrapper(false);
		} catch (Exception e) {
			System.out.println(new DebuggerException(e));
		}
		
		try {
			exceptionWrapper(false);
		} catch (Exception e) {
			System.out.println(new DebuggerException());
		}
	}
	
	public void exceptionWithMessageGenerator(boolean debugger) throws Exception {
		if(debugger) {
			throw new DebuggerException();
		}
		throw new Exception("Some error message"); //$NON-NLS-1$
	}
	
	public void exceptionGenerator() throws Exception {
		throw new Exception();
	}
	
	public void exceptionWrapper(boolean debugger) throws Exception {
		exceptionWithMessageGenerator(debugger);
	}
}
