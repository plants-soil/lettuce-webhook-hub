package com.plantssoil.webhook.core.impl;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.webhook.core.IWebhookEvent;

public class DefaultWebhookEventListener implements IMessageListener<DefaultWebhookMessage> {

    class WebhookEvent implements IWebhookEvent {
        private String publisherId;
        private String version;
        private String eventType;
        private String eventTag;
        private String contentType;
        private String charset;

        public WebhookEvent(String publisherId, String version, String eventType, String eventTag, String contentType, String charset) {
            super();
            this.publisherId = publisherId;
            this.version = version;
            this.eventType = eventType;
            this.eventTag = eventTag;
            this.contentType = contentType;
            this.charset = charset;
        }

        @Override
        public String getPublisherId() {
            return this.publisherId;
        }

        @Override
        public String getVersion() {
            return this.version;
        }

        @Override
        public String getEventType() {
            return this.eventType;
        }

        @Override
        public String getEventTag() {
            return this.eventTag;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public String getCharset() {
            return this.charset;
        }

    }

    @Override
    public void onMessage(DefaultWebhookMessage message, String consumerId) {
        final IWebhookEvent event = new WebhookEvent(message.getPublisherId(), message.getVersion(), message.getEventType(), message.getEventTag(),
                message.getContentType(), message.getCharset());
        DefaultWebhookPoster.getInstance().postWebhook(message, event);
    }

}
