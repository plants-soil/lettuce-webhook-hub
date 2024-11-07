# lettuce-common
The common modules and function, which are all fundamental functions for further business modules and functions.
All these modules are business-less.

## lettuce-common-config
Functions for configuration search, load and getter.
Could load configurations from OS env variables, Java system properties, Java keystore file, and configuration file "lettuce.properties".
com.plantssoil.common.config.ConfigFactory: Singleton instance to search, load and get configuration via name from all sort of configurations.
com.plantssoil.common.config.LettuceConfiguration: All configuration name constants.
com.plantssoil.common.config.ConfigurableLoader: Some configuration items are Factory classes or Object types (Which implements interface com.plantssoil.common.config.IConfigurable), ConfigurableLoader could create singleton or non-singleton instances from configuration item name.

## lettuce-common-exception
Defined the parent exception which include exception code (for tracking purpose), all exceptions should extends this class in other modules for easier issue tracking.

## lettuce-common-filesystem
The directory loader, to locate configuration or data directory.
Also included 2 loader, one for configuration file loader, another for database change log file loader.

## lettuce-common-httpclient
As httpclient, used to call API from remote URL.
Support 3 kind of clients:
com.plantssoil.common.httpclient.impl.PlainClientNotifier, non-signature in header when call remote API
com.plantssoil.common.httpclient.impl.FixedKeyClientNotifier, a solid (fixed) specified key as the signature in header when call remote API.
com.plantssoil.common.httpclient.impl.SignaturedClientNotifier, use encrypted payload as the signature in header when call remote API.

## lettuce-common-mq
Just like a broker, defined universal message publisher & subscriber function.
com.plantssoil.common.mq.IMessageServiceFactory, the singleton factory for IMessagePublisher & IMessageSubscriber creation.
com.plantssoil.common.mq.IMessagePublisher, could publish messages via this interface, careless MQ type / connection / channel (session).
com.plantssoil.common.mq.IMessageSubscriber, could subscribe messages via this interface, careless MQ type / connection / channel (session).
Support RabbitMQ / ActiveMQ / Redis as the MQ service provider.

## lettuce-common-persistence
Just like a broker, defined universal initiator, persistence, query interfaces. could initialize, CRUD, query all data via these interface.
Support JPA for RDBMS, and some kinds of NOSQL.

## lettuce-common-security
Some utilities for data encryption / decryption, such as AES / Sha512Hmac algorithms.
Also support the KeyStore read and write utilities.


## lettuce-common-test 
Temp directory create and remote utilities for testing purpose.