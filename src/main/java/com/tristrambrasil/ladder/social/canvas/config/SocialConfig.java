/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tristrambrasil.ladder.social.canvas.config;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import com.tristrambrasil.ladder.social.canvas.user.SecurityContext;
import com.tristrambrasil.ladder.social.canvas.user.SimpleConnectionSignUp;
import com.tristrambrasil.ladder.social.canvas.user.SimpleSignInAdapter;
import com.tristrambrasil.ladder.social.canvas.user.User;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mongo.ConnectionConverter;
import org.springframework.social.connect.mongo.ConnectionService;
import org.springframework.social.connect.mongo.MongoUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

/**
 * Spring Social Configuration.
 *
 * @author Keith Donald
 */
@Configuration
@ComponentScan(basePackages = "org.springframework.social.connect.mongo")
@PropertySource("classpath:com/tristrambrasil/ladder/social/canvas/config/application.properties")
public class SocialConfig {
    
    @Inject
    private Environment environment;
    @Inject
    private ConnectionService mongoService;

    /**
     * When a new provider is added to the app, register its
     * {@link ConnectionFactory} here.
     *
     * @see FacebookConnectionFactory
     */
    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(environment.getProperty("facebook.clientId"),
                environment.getProperty("facebook.clientSecret")));
        return registry;
    }

    /**
     * Singleton data access object providing access to connections across all
     * users.
     */
    @Bean
    public UsersConnectionRepository usersConnectionRepository() {
        MongoUsersConnectionRepository repository = new MongoUsersConnectionRepository(mongoService,
                connectionFactoryLocator(), Encryptors.noOpText());
        repository.setConnectionSignUp(new SimpleConnectionSignUp());
        return repository;
    }

    /**
     * Request-scoped data access object providing access to the current user's
     * connections.
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository() {
        User user = SecurityContext.getCurrentUser();
        return usersConnectionRepository().createConnectionRepository(user.getId());
    }

    /**
     * A proxy to a request-scoped object representing the current user's
     * primary Facebook account.
     *
     * @throws NotConnectedException if the user is not connected to facebook.
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Facebook facebook() {
        return connectionRepository().getPrimaryConnection(Facebook.class).getApi();
    }

    /**
     * The Spring MVC Controller that allows users to sign-in with their
     * provider accounts.
     */
    @Bean
    public ProviderSignInController providerSignInController() {
        ProviderSignInController signInController = new ProviderSignInController(connectionFactoryLocator(), usersConnectionRepository(), new SimpleSignInAdapter());
        signInController.setPostSignInUrl("http://apps.facebook.com/diii-ladder/");
        return signInController;
    }

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(
                new Mongo(environment.getProperty("mongo.hostName"),
                environment.getProperty("mongo.portNumber", Integer.class)),
                environment.getProperty("mongo.databaseName"));
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        mongoTemplate.setWriteConcern(WriteConcern.SAFE);
        return mongoTemplate;
    }

    public @Bean
    TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }

    public @Bean
    ConnectionConverter connectionConverter() {
        return new ConnectionConverter(
                connectionFactoryLocator(),
                textEncryptor());
    }

}