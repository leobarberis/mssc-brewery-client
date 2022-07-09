package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jt on 2019-08-08.
 */
@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {

    private final int maxTotalConnections;
    private final int maxTotalConnectionsPerRoute;
    private final int ConnectionRequestTimeout;
    private final int socketTimeout;

    public BlockingRestTemplateCustomizer(@Value("${apache.client.maxTotalConnections}") int maxTotalConnections,
                                          @Value("${apache.client.maxTotalConnectionsPerRoute}") int maxTotalConnectionsPerRoute,
                                          @Value("${apache.client.ConnectionRequestTimeout}") int connectionRequestTimeout,
                                          @Value("${apache.client.socketTimeout}") int socketTimeout) {
        this.maxTotalConnections = maxTotalConnections;
        this.maxTotalConnectionsPerRoute = maxTotalConnectionsPerRoute;
        ConnectionRequestTimeout = connectionRequestTimeout;
        this.socketTimeout = socketTimeout;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxTotalConnectionsPerRoute);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(ConnectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(this.clientHttpRequestFactory());
    }
}
