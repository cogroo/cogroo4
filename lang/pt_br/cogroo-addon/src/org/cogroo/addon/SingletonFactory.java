/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.addon;

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
