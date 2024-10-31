package com.plantssoil.common.event;

import java.util.concurrent.Future;

public interface IEventSubscriber {
    public Future<IEvent> receive();
}
