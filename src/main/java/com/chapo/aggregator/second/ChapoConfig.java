package com.chapo.aggregator.second;

import com.chapo.aggregator.ChapoConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jdbc.store.JdbcMessageStore;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@EnableIntegration
@Configuration
public class ChapoConfig {

    @Bean
    public Queue myQueue() {
        return new Queue("queue-agreggator-chapo", true); // Your queue name, durable
    }

    @Bean("requestChannel")
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

//    @Bean
//    public JdbcMessageStore messageStore(DataSource dataSource) {
//        return new JdbcMessageStore(dataSource);
//    }

    @Bean
    public AmqpInboundChannelAdapter amqpInbound(CachingConnectionFactory connectionFactory, Queue myQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(myQueue);
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(container); // Correct constructor
        adapter.setOutputChannel(inputChannel());
        adapter.setMessageConverter(new ChapoConverter());
        return adapter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MessageGroupStore messageStore() {
        return new JdbcMessageStore(dataSource);
    }

    @Bean
    public IntegrationFlow chapoFlow(MessageGroupStore messageStore) {
        return IntegrationFlow
            .from("requestChannel")
            .aggregate(aggregatorSpec -> aggregatorSpec
                           .correlationStrategy(message -> ((Message<Lancamento>) message).getPayload().getConta())
                           .releaseStrategy(group -> group.size() >= 2)
                           .outputProcessor(group -> {
                               Lote lote = Lote.criarLote();
                               group.getMessages().forEach(message -> lote.adicionarLancamento(((Message<Lancamento>) message).getPayload()));
                               return lote;
                           })
                           .messageStore(messageStore)
                           .expireGroupsUponCompletion(true)
                           .sendPartialResultOnExpiry(true)
                           .groupTimeout(1000) // Optional: Add a timeout for incomplete groups
                      )
            .channel("outputChannel")
            .<Lote>handle(msgLote -> {
                Lote lote = ((Lote)msgLote.getPayload());
                lote.getLancamentos()
                    .forEach(l -> System.out.println(l.getConta() + " " + l.getDescricao() + " " + lote.getUuid()));
            })
            .get();
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("org/springframework/integration/jdbc/schema-mysql.sql")));
        return initializer;
    }

}
