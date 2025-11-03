package Backend.FMM.Configure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = "Backend.FMM.Repository")  // Scan repositories
@EntityScan(basePackages = "Backend.FMM.Entity")  // Scan entities
@EnableJpaAuditing  // Bật auditing nếu cần @CreatedDate
public class JpaConfig {

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("Backend.FMM.Entity");  // Scan entities

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Custom Hibernate properties (sử dụng Map<String, Object>)
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.naming.physical-strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");  //Hỗ trợ snake_case
        properties.put("hibernate.show_sql", true);  // Log SQL
        properties.put("hibernate.format_sql", true);  // Format SQL log
        properties.put("hibernate.hbm2ddl.auto", "update");  // Tự cập nhật schema (cẩn thận production)
        em.setJpaPropertyMap(properties);

        return em;
    }
}
