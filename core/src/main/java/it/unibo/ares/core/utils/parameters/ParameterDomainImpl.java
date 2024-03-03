package it.unibo.ares.core.utils.parameters;

import java.util.function.Predicate;

/**
 * Implementazione di un dominio di un parametro.
 * 
 * @param <T> Il tipo di valore del dominio
 */
public final class ParameterDomainImpl<T> implements ParameterDomain<T> {
    private final String description;
    private final Predicate<T> predicate;

    /**
     * Crea un nuovo dominio.
     * 
     * @param description la decrizione del dominio in linguaggio naturale
     * @param predicate   Il predicato per testare che un valore sia nel dominio
     */
    public ParameterDomainImpl(final String description, final Predicate<T> predicate) {
        this.description = description;
        this.predicate = predicate;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean isValueValid(final T value) {
        return this.predicate.test(value);
    }

}