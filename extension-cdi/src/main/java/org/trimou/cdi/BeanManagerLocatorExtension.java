package org.trimou.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * Workaround to get a {@link BeanManager} instance.
 *
 * @author Martin Kouba
 */
public class BeanManagerLocatorExtension implements Extension {

	static BeanManager providedBeanManager = null;

	public void observeAfterDeploymentValidation(
			@Observes AfterDeploymentValidation event, BeanManager beanManager) {
		providedBeanManager = beanManager;
	}

}