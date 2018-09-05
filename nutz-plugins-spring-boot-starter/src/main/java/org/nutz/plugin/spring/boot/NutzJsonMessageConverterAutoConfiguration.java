package org.nutz.plugin.spring.boot;

import java.text.DecimalFormat;
import java.util.TimeZone;

import org.nutz.integration.spring.NutzJsonMessageConverter;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.plugin.spring.boot.config.NutzJsonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
	@ConditionalOnExpression("${nutz.json.enabled:false}")
	public HttpMessageConverter<Object> json() {
		JsonFormat format = null;
		if (jsonProperties.getMode() != null) {// 直接模式设置
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
		} else {
			format = Json.fromJson(JsonFormat.class, Json.toJson(jsonProperties));
		}
		if (Strings.isNotBlank(jsonProperties.getActived())) {
			format.setActived(jsonProperties.getActived());
		}
		if (Strings.isNotBlank(jsonProperties.getLocked())) {
			format.setLocked(jsonProperties.getLocked());
		}
		if (Strings.isNotBlank(jsonProperties.getDateFormat())) {
			format.setDateFormat(jsonProperties.getDateFormat());
		}
		if (Strings.isNotBlank(jsonProperties.getNumberFormat())) {
			format.setNumberFormat(new DecimalFormat(jsonProperties.getNumberFormat()));
		}
		if (Strings.isNotBlank(jsonProperties.getTimeZone())) {
			format.setTimeZone(TimeZone.getTimeZone(jsonProperties.getTimeZone()));
		}
		return new NutzJsonMessageConverter().setFormat(format).setIgnoreType(jsonProperties.getIgnoreType());
	}

}
