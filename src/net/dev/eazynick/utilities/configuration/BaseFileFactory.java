package net.dev.eazynick.utilities.configuration;

import net.dev.eazynick.EazyNick;

public interface BaseFileFactory<T> {
	
	public ConfigurationFile<T> createConfigurationFile(EazyNick eazyNick);

}
