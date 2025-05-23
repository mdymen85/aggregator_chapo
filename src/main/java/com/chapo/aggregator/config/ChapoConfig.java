package com.chapo.aggregator.config;

import com.chapo.aggregator.domain.ChapoConverter;
import com.chapo.aggregator.domain.LancamentoService;
import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.chapo.aggregator.domain.dtos.LoteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jdbc.store.JdbcMessageStore;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@EnableIntegration
@Configuration
@Slf4j
@OnAggregatorEnabled
public class ChapoConfig {

    @Value("${application.aggregator.group-size:5}")
    private int groupSize;

    @Bean
    public Queue myQueue() {
        return new Queue("queue-agreggator-chapo", true);
    }

    @Bean("requestChannel")
    public MessageChannel inputChannel() {
        return new QueueChannel();
    }

    @Bean
    public AmqpInboundChannelAdapter amqpInbound(SimpleMessageListenerContainer simpleMessageListenerContainer) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(simpleMessageListenerContainer); // Correct constructor
        adapter.setOutputChannel(inputChannel());
        adapter.setMessageConverter(new ChapoConverter());
        return adapter;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory, Queue myQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(myQueue);
        container.setConcurrentConsumers(5);
        container.setMaxConcurrentConsumers(10);
        container.setPrefetchCount(1);
        return container;
    }

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setConcurrentConsumers(5);      // Start with 5 consumer threads
//        factory.setMaxConcurrentConsumers(10);  // Allow up to 10 consumer threads
//        factory.setPrefetchCount(1);           // Only fetch one message at a time per consumer
//        // (important for fair dispatch in round-robin)
//        factory.setDefaultRequeueRejected(false); // Do not requeue rejected messages by default
//        return factory;
//    }

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
    private LancamentoService lancamentoService;

    @Bean
    public IntegrationFlow chapoFlow(MessageGroupStore messageStore) {
        return IntegrationFlow
            .from("requestChannel")
            .aggregate(aggregatorSpec -> aggregatorSpec
                           .correlationStrategy(message -> ((Message<LancamentoDTO>) message).getPayload().getConta())
                           .releaseStrategy(group -> group.size() >= groupSize)
                           .outputProcessor(group -> {
                               LoteDTO loteDTO = LoteDTO.criarLote();
                               group.getMessages().forEach(message -> loteDTO.adicionarLancamento(((Message<LancamentoDTO>) message).getPayload()));
                               return loteDTO;
                           })
                           .messageStore(messageStore)
                           .expireGroupsUponCompletion(true)
                           .sendPartialResultOnExpiry(true)
//                           .groupTimeout(1000) // Optional: Add a timeout for incomplete groups
                      )
            .channel("outputChannel")
            .<LoteDTO>handle(msgLote -> {
                System.out.println("[" + Thread.currentThread().getId() + "]" + "Inicio do processamento : " + System.currentTimeMillis());
                LoteDTO loteDTO = ((LoteDTO)msgLote.getPayload());
                lancamentoService.updateBalance(loteDTO);
                System.out.println("[" + Thread.currentThread().getId() + "]" + "Fim do processamento : " + System.currentTimeMillis());

//                loteDTO.getLancamentoDTOS()
//                       .forEach(l -> System.out.println(l.getConta() + " " + l.getDescricao() + " " + loteDTO.getUuid()));
//                rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
//                rabbitTemplate.convertAndSend(rabbitMQConfig.getDirectQueueName(), loteDTO);
            })
            .get();
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        try {
//            initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("org/springframework/integration/jdbc/schema-mysql.sql")));
        } catch (Exception e) {
//            log.warn("");
        }

        return initializer;
    }

    @PostConstruct
    public void init() {
        System.out.println("Aggregator enabled");
        System.out.println("Group size : " + groupSize);
    }

}
