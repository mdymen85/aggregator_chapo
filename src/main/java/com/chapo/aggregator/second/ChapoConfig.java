package com.chapo.aggregator.second;

import com.chapo.aggregator.ChapoConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jdbc.store.JdbcMessageStore;
import org.springframework.integration.store.MessageGroupStore;
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
        return new QueueChannel();
    }

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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Bean
  public IntegrationFlow chapoFlow(MessageGroupStore messageStore) {
//    public IntegrationFlow chapoFlow() {
        return IntegrationFlow
            .from("requestChannel")
            .aggregate(aggregatorSpec -> aggregatorSpec
                           .correlationStrategy(message -> ((Message<Lancamento>) message).getPayload().getConta())
                           .releaseStrategy(group -> group.size() >= 5)
                           .outputProcessor(group -> {
                               Lote lote = Lote.criarLote();
                               group.getMessages().forEach(message -> lote.adicionarLancamento(((Message<Lancamento>) message).getPayload()));
                               return lote;
                           })
                           .messageStore(messageStore)
                           .expireGroupsUponCompletion(true)
                           .sendPartialResultOnExpiry(true)
//                           .groupTimeout(1000) // Optional: Add a timeout for incomplete groups
                      )
            .channel("outputChannel")
            .<Lote>handle(msgLote -> {
                Lote lote = ((Lote)msgLote.getPayload());
                lote.getLancamentos()
                    .forEach(l -> System.out.println(l.getConta() + " " + l.getDescricao() + " " + lote.getUuid()));

                rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
                rabbitTemplate.convertAndSend(rabbitMQConfig.getDirectQueueName(), lote);
            })
            .get();
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
//        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("org/springframework/integration/jdbc/schema-mysql.sql")));
        return initializer;
    }

}
