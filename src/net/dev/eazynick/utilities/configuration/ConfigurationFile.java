package net.dev.eazynick.utilities.configuration;

import java.io.File;

public interface ConfigurationFile<T> {
	
	public void initConfiguration();
	
	public void setDefaults();
	
	public void save();
	
	public void reload();
	
	public File getDirectory();
	
	public File getFile();
	
	public T getConfiguration();

}
