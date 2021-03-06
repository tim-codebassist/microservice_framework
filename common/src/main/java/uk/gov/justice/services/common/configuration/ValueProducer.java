package uk.gov.justice.services.common.configuration;

import static java.lang.String.format;
import static uk.gov.justice.services.common.configuration.CommonValueAnnotationDef.localValueAnnotationOf;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.naming.NamingException;


/**
 * Looks up context specific jndi names in order to inject their values into @Value annotated properties.
 */
@ApplicationScoped
public class ValueProducer extends AbstractValueProducer {

    @Inject
    ServiceContextNameProvider serviceContextNameProvider;

    public ValueProducer() throws NamingException {
        super();
    }

    @Value
    @Produces
    public String produceValue(final InjectionPoint ip) throws NamingException {
        return jndiValueFor(localValueAnnotationOf(ip));
    }

    @Override
    protected String jndiNameFrom(final CommonValueAnnotationDef annotation) {
        return format("java:/app/%s/%s", serviceContextNameProvider.getServiceContextName(), annotation.key());
    }

}
