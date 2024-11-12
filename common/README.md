# lettuce-common
The common modules and function, which are all fundamental functions for further business modules and functions.

All these modules are business-less.

## lettuce-common-config
Functions for configuration search, load and getter.

Could load configurations from OS env variables, Java system properties, Java keystore file, and configuration file "lettuce.properties".

1. `com.plantssoil.common.config.ConfigFactory`: Singleton instance to search, load and get configuration via name from all sort of configurations. Could read encrypted configuration value, the variable enveloped via ${} also could be read.
2. `com.plantssoil.common.config.LettuceConfiguration`: All configuration name constants.
3. `com.plantssoil.common.config.ConfigurableLoader`: Some configuration items are Factory classes or Object types (Which implements interface `com.plantssoil.common.config.IConfigurable`), ConfigurableLoader could create singleton or non-singleton instances from configuration item name.

Example, read configuration item from all kinds of configuration source:

```java
Configuration config = ConfigFactory.getInstance().getConfiguration();
int systemproperty_integer = config.getInt("systemproperty_integer");
String systemproperty_string = config.getString("systemproperty_string");
boolean propertyfile_boolean = config.getBoolean("propertyfile_boolean");
BigDecimal jks_bigdecimal = config.getBigDecimal("jks_bigdecimal");
String systemproperty_replace = config.getString("systemproperty_replace");
```

## lettuce-common-exception
Defined the parent exception which include exception code (for tracking purpose), all exceptions should extends this class in other modules for easier issue tracking.

## lettuce-common-filesystem
The directory loader, to locate configuration or data directory.

Also included 2 loader, one for configuration file loader, another for database change log file loader.

## lettuce-common-httpclient
As HTTP Client, used to call API from remote URL.

Support 3 kind of clients:

1. `com.plantssoil.common.httpclient.impl.PlainClientNotifier`, non-signature in header when call remote API
2. `com.plantssoil.common.httpclient.impl.FixedKeyClientNotifier`, a solid (fixed) specified key as the signature in header when call remote API.
3. `com.plantssoil.common.httpclient.impl.SignaturedClientNotifier`, use encrypted payload as the signature in header when call remote API.

## lettuce-common-mq
As message queue broker, could publish or subscribe messages, no need have any idea of MQ.

1. `com.plantssoil.common.mq.IMessageServiceFactory`, the singleton factory for IMessagePublisher & IMessageSubscriber creation.
2. `com.plantssoil.common.mq.IMessagePublisher`, could publish messages via this interface, careless MQ type / connection / channel (session).
3. `com.plantssoil.common.mq.IMessageSubscriber`, could subscribe messages via this interface, careless MQ type / connection / channel (session).
Support RabbitMQ / ActiveMQ / Redis as the MQ service provider.

Example for message publisher:

```java
IMessagePublisher publisher = IMessageServiceFactory.getDefaultFactory().createMessagePublisher();
publisher.publisherId("PUBLISHER-ID-01").version("V1.0");
// publisher 20 messages
for (int i = 0; i < 20; i++) {
    publisher.publish("This is the " + i + " message comes from PUBLISHER-ID-01 (V1.0)");
}
```

Example for message publisher:

```java
IMessageSubscriber subscriber = IMessageServiceFactory.getDefaultFactory().createMessageSubscriber();
subscriber.consumerId("consumerId-" + i).publisherId("PUBLISHER-ID-01").version("V1.0").addMessageListener(new MessageListener());
subscriber.subscribe();
```


## lettuce-common-persistence
Just like a broker, defined universal initiator, persistence, query interfaces. could initialize, CRUD, query all data via these interface.

Support JPA for RDBMS, and some kinds of NOSQL (Mongodb).

- Example for create entity:

```java
Teacher teacher = newTeacherEntity();
try (IPersistence persists = IPersistenceFactory.getDefaultFactory().create()) {
    persists.create(teacher);
    successful.add(teacher.getTeacherId() + ", " + teacher.getTeacherName() + " created.");
} catch (Exception e) {
    e.printStackTrace();
    fail(e.getMessage());
}
```

- Example for update entity:

```java
Teacher t = null;
try (IPersistence persists = IPersistenceFactory.getDefaultFactory().create()) {
    IEntityQuery<Teacher> q = persists.createQuery(Teacher.class).firstResult(0).maxResults(5);
    t = q.singleResult().get();
} catch (Exception e) {
    e.printStackTrace();
    fail(e.getMessage());
}
        
t.setTeacherName("Teacher" + i);
try (IPersistence p = IPersistenceFactory.getDefaultFactory().create()) {
    Teacher updated = p.update(t);
    futures.add("Teacher name changed to: " + updated.getTeacherName());
} catch (Exception e) {
    e.printStackTrace();
    fail(e.getMessage());
}
```

- Example for remove entity:

```java
Teacher t = null;
try (IPersistence persists = IPersistenceFactory.getDefaultFactory().create()) {
    IEntityQuery<Teacher> q = persists.createQuery(Teacher.class).firstResult(0).maxResults(5);
    t = q.singleResult().get();
} catch (Exception e) {
    e.printStackTrace();
    fail(e.getMessage());
}

try (IPersistence p = IPersistenceFactory.getDefaultFactory().create()) {
    p.remove(t);
} catch (Exception e) {
    e.printStackTrace();
    fail(e.getMessage());
}
```

- Example for query entity (which have asynchronized type)

```java
try (IPersistence persists = IPersistenceFactory.getDefaultFactory().create()) {
    IEntityQuery<Student> query = persists.createQuery(Student.class);
    CompletableFuture<List<Student>> students = query.filter("studentName", IEntityQuery.FilterOperator.like, "Student-1.")
          .filter("gender", IEntityQuery.FilterOperator.equals, Student.Gender.Female).firstResult(0).maxResults(5).resultList();
    for (Student s : students.get()) {
        System.out.println(s);
    }
} catch (Exception e) {
    e.printStackTrace();
    fail(e.getMessage());
}
```

## lettuce-common-security
Some utilities for data encryption / decryption, such as AES / Sha512Hmac algorithms.
Also support the KeyStore read and write utilities.


## lettuce-common-test 
Temp directory create and remote utilities for testing purpose.