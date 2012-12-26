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
package com.tristrambrasil.ladder.canvas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Main configuration class for the application. Turns on
 *
 * @Component scanning, loads externalized application.properties, and sets up
 * the database.
 *
 * @author Keith Donald
 */
@Configuration
@ComponentScan(basePackages = "org.springframework.social.canvas", excludeFilters = {
    @Filter(Configuration.class)})
@PropertySource("classpath:org/springframework/social/canvas/config/application.properties")
public class MainConfig {

//    @Bean
//    public RideManagementSystem rideManagementSystem() {
//        RideManagementSystem prevalentSystem = (RideManagementSystem) prevayler().prevalentSystem();
//        return prevalentSystem;
//    }
//
//    @Bean
//    public Prevayler prevayler() {
//        Prevayler prevayler = null;
//        try {
//            prevayler = PrevaylerFactory.createPrevayler(new RideManagementSystem(), "/home/guilherme/carona");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return prevayler;
//    }
}
