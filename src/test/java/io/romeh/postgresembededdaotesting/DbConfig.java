package io.romeh.postgresembededdaotesting;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.flapdoodle.embed.process.runtime.Network;
import io.romeh.postgresembeddeddaotesting.dao.CustomerRepository;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

/**
 * @author romeh
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = {CustomerRepository.class})
@Profile("DaoTest")
public class DbConfig {
	private static final List<String> DEFAULT_ADDITIONAL_INIT_DB_PARAMS = Arrays
			.asList("--nosync", "--locale=en_US.UTF-8");

	@Bean
	@DependsOn("postgresProcess")
	public DataSource dataSource(PostgresConfig config) {

		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setUrl(format("jdbc:postgresql://%s:%s/%s", config.net().host(), config.net().port(), config.storage().dbName()));
		ds.setUsername(config.credentials().username());
		ds.setPassword(config.credentials().password());
		return ds;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

		LocalContainerEntityManagerFactoryBean lcemfb
				= new LocalContainerEntityManagerFactoryBean();
		lcemfb.setDataSource(dataSource);
		lcemfb.setPackagesToScan("io.romeh.postgresembeddeddaotesting.domain", "io.romeh.postgresembeddeddaotesting.dao");
		HibernateJpaVendorAdapter va = new HibernateJpaVendorAdapter();
		lcemfb.setJpaVendorAdapter(va);
		lcemfb.setJpaProperties(getHibernateProperties());
		lcemfb.afterPropertiesSet();
		return lcemfb;

	}

	@Bean
	public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();

		transactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties getHibernateProperties() {
		Properties ps = new Properties();
		ps.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
		ps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		ps.put("hibernate.hbm2ddl.auto", "update");
		ps.put("hibernate.connection.characterEncoding", "UTF-8");
		ps.put("hibernate.connection.charSet", "UTF-8");

		ps.put(AvailableSettings.FORMAT_SQL, "true");
		ps.put(AvailableSettings.SHOW_SQL, "true");
		return ps;

	}

	@Bean
	public PostgresConfig postgresConfig() throws IOException {

		final PostgresConfig postgresConfig = new PostgresConfig(Version.V9_6_8,
				new AbstractPostgresConfig.Net("localhost", Network.getFreeServerPort()),
				new AbstractPostgresConfig.Storage("test"),
				new AbstractPostgresConfig.Timeout(),
				new AbstractPostgresConfig.Credentials("user", "pass")
		);

		postgresConfig.getAdditionalInitDbParams().addAll(DEFAULT_ADDITIONAL_INIT_DB_PARAMS);

		return postgresConfig;
	}

	@Bean(destroyMethod = "stop")
	public PostgresProcess postgresProcess(PostgresConfig config) throws IOException {
		PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
		PostgresExecutable exec = runtime.prepare(config);
		PostgresProcess process = exec.start();
		return process;
	}


}
