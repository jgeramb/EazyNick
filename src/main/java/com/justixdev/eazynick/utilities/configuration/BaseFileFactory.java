package com.justixdev.eazynick.utilities.configuration;

import com.justixdev.eazynick.EazyNick;

public interface BaseFileFactory<T> {
	
	<V extends ConfigurationFile<T>> V createConfigurationFile(EazyNick eazyNick, Class<V> type);

}
