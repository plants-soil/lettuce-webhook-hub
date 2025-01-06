package com.plantssoil.common.mq;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * Message service factory, could use this factory to create message publisher /
 * consumer<br/>
 * This factory implements AutoCloseable interface, could release all resources
 * attached in {@link AutoCloseable#close()}
 * 
 * @author danialdy
 * @Date 2 Nov 2024 3:36:00 pm
 */
public interface IMessageServiceFactory<T> extends IConfigurable, AutoCloseable {
    /**
     * create message publisher (which is used to publish message)
     * 
     * @return Message Publisher instance
     */
    public IMessagePublisher<T> createMessagePublisher();

    /**
     * create message consumer (which is used to consume message)
     * 
     * @return Message Consumer instance
     */
    public IMessageConsumer<T> createMessageConsumer();

    /**
     * Get the default implementation of this factory (which is singleton)<br/>
     * The default implementation is specified by configuration
     * {@link LettuceConfiguration#MESSAGE_SERVICE_FACTORY_CONFIGURABLE}
     * 
     * @return Singleton instance of {@link IMessageServiceFactory}
     */
    @SuppressWarnings("unchecked")
    public static <T> IMessageServiceFactory<T> getFactoryInstance() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE);
        if (configurable instanceof IMessageServiceFactory) {
            return (IMessageServiceFactory<T>) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IMessageServiceFactory.class.getName());
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, err);
        }
    }

    /**
     * Get the default implementation of this factory (which is singleton)<br/>
     * The default implementation is specified by configuration
     * {@link LettuceConfiguration#MESSAGE_SERVICE_FACTORY_CONFIGURABLE}
     * 
     * @param defaultImplementation The default implementation class if
     *                              {@link LettuceConfiguration#MESSAGE_SERVICE_FACTORY_CONFIGURABLE}
     *                              is not configured
     * @return Singleton instance of {@link IMessageServiceFactory}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> IMessageServiceFactory<T> getFactoryInstance(Class<? extends IMessageServiceFactory> defaultImplementation) {
        if (defaultImplementation == null) {
            return getFactoryInstance();
        }
        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE,
                defaultImplementation.getName());
        if (configurable instanceof IMessageServiceFactory) {
            return (IMessageServiceFactory<T>) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IMessageServiceFactory.class.getName());
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, err);
        }
    }
}
