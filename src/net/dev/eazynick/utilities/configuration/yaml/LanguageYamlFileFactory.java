package net.dev.eazynick.utilities.configuration.yaml;

import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.BaseFileFactory;

public class LanguageYamlFileFactory implements BaseFileFactory<YamlConfiguration> {

	private String language;
	
	public LanguageYamlFileFactory(String language) {
		this.language = language;
	}
	
	@Override
	public LanguageYamlFile createConfigurationFile(EazyNick eazyNick) {
		//Initialize language file
		LanguageYamlFile languageYamlFile = new LanguageYamlFile(eazyNick, "lang/", language + ".yml");
		languageYamlFile.setLanguage(language);
		languageYamlFile.initConfiguration();
		
		return languageYamlFile;
	}

}
