/*
 * Copyright 2022-2024 sephy.top
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.sephy.infra.beanmapper;

import org.modelmapper.config.Configuration;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NamingConvention;

// @ConfigurationProperties(prefix = "model-mapper")
// @Data
public class ModelMapperProperties {

    private boolean ambiguityIgnored = true;

    private boolean fieldMatchingEnabled = false;

    private boolean fullTypeMatchingRequired = false;

    private boolean implicitMappingEnabled = true;

    private boolean skipNullEnabled = false;

    private NameTokenizer destinationNameTokenizer;

    private NameTransformer destinationNameTransformer;

    private NamingConvention destinationNamingConvention;

    private NameTokenizer sourceNameTokenizer;

    private NameTransformer sourceNameTransformer;

    private NamingConvention sourceNamingConvention;

    private Configuration.AccessLevel fieldAccessLevel;

    private Configuration.AccessLevel methodAccessLevel;

    private MatchingStrategy matchingStrategy;

}
