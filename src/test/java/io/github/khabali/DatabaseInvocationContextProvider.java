package io.github.khabali;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class DatabaseInvocationContextProvider implements TestTemplateInvocationContextProvider {

    private final Map<String, JdbcDatabaseContainer> containers;

    public DatabaseInvocationContextProvider() {
        containers = new HashMap<>();
        containers.put("postgresql", new PostgreSQLContainer("postgres:11.1"));
        containers.put("mysql", (MySQLContainer) new MySQLContainer("mysql:8.0.13").withCommand(
                "--default-authentication-plugin=mysql_native_password"));
        containers.put("mariadb", new MariaDBContainer("mariadb:10.4.0"));
        containers.put("mssql", new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2017-CU12"));
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext extensionContext) {
        return containers.keySet().stream().map(this::invocationContext);
    }

    private TestTemplateInvocationContext invocationContext(final String database) {
        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
                return database;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                final JdbcDatabaseContainer databaseContainer = containers.get(database);
                return asList((BeforeEachCallback) context -> databaseContainer.start(),
                        (AfterAllCallback) context -> databaseContainer.stop(), new ParameterResolver() {

                            @Override
                            public boolean supportsParameter(ParameterContext parameterCtx,
                                    ExtensionContext extensionCtx) {
                                return parameterCtx.getParameter().getType().equals(JdbcDatabaseContainer.class);
                            }

                            @Override
                            public Object resolveParameter(ParameterContext parameterCtx,
                                    ExtensionContext extensionCtx) {
                                return databaseContainer;
                            }
                        });
            }
        };
    }
}
