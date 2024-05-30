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
