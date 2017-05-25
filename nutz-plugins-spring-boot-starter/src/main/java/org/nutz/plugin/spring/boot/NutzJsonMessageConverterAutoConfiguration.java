package org.nutz.plugin.spring.boot;

import java.text.NumberFormat;
import java.util.Locale;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.plugin.spring.boot.config.NutzJsonProperties;
import org.nutz.plugin.spring.boot.converters.NutzJsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
@Configuration
@ConditionalOnClass({ Json.class })
@EnableConfigurationProperties(NutzJsonProperties.class)
public class NutzJsonMessageConverterAutoConfiguration {

	@Autowired
	private NutzJsonProperties jsonProperties;

	@Bean
	@ConditionalOnProperty("nutz.json")
	public HttpMessageConverter json() {
		JsonFormat format = null;
		switch (jsonProperties.getMode()) {
		case COMPACT:
			format = JsonFormat.compact();
			break;
		case FORLOOK:
			format = JsonFormat.forLook();
			break;
		case FULL:
			format = JsonFormat.full();
			break;
		case NICE:
			format = JsonFormat.nice();
			break;
		case TIDY:
			format = JsonFormat.tidy();
			break;
		default:
			format = JsonFormat.compact();
			break;
		}
		format.setDateFormat(jsonProperties.getDateFormat());
		if (Strings.isNotBlank(jsonProperties.getActived())) {
			format.setActived(jsonProperties.getActived());
		}
		format.setAutoUnicode(jsonProperties.isAutoUnicode());
		format.setIndent(jsonProperties.getIndent());
		format.setIgnoreNull(jsonProperties.isIgnoreNull());

		if (Strings.isNotBlank(jsonProperties.getIndentBy())) {
			format.setIndentBy(jsonProperties.getIndentBy());
		}

		if (Strings.isNotBlank(jsonProperties.getLocked())) {
			format.setLocked(jsonProperties.getLocked());
		}

		format.setNullAsEmtry(jsonProperties.isNullAsEmtry());
		format.setNumberFormat(NumberFormat.getCurrencyInstance(Locale.CHINA));
		format.setQuoteName(jsonProperties.isQuoteName());
		format.setUnicodeLower(jsonProperties.isUnicodeLower());
		return new NutzJsonMessageConverter().setFormat(format);
	}

}
