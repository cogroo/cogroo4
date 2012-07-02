package br.ccsl.cogroo.addon;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.uno.XComponentContext;

// Should keep only one instance of the grammar checker component.
public class SingletonFactory implements XSingleComponentFactory {

	private Main instance;
	private final Object lock = new Object();

	public final Object createInstanceWithArgumentsAndContext(
			final Object[] arguments, final XComponentContext xContext)
			throws com.sun.star.uno.Exception {
		return createInstanceWithContext(xContext);
	}

	public final Object createInstanceWithContext(
			final XComponentContext xContext) throws com.sun.star.uno.Exception {
		synchronized (lock) {
			if (instance == null) {
				instance = new Main(xContext);
			} else {
				instance.changeContext(xContext);
			}
		}

		return instance;
	}
}
