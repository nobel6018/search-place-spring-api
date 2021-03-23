package com.cococloudy.magnolia.config

import com.zaxxer.hikari.HikariDataSource
import org.h2.tools.Server
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("local")
class H2ServerConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun dataSource(): DataSource {
        defaultRun()

        return HikariDataSource()
    }

    private fun defaultRun(): Server {
        return Server.createTcpServer(
            "-tcp",
            "-tcpAllowOthers",
            "-ifNotExists",
            "-tcpPort", "9092"
        ).start()
    }
}