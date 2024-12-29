use anyhow::Result;
use rabbitmq_stream_client::{error::StreamCreateError, types::Message, Environment};
use tokio::sync::mpsc::Receiver;

pub enum EventType {
    Point(String),
    Topic(String),
}

pub struct QueueMessage {
    pub destination: EventType,
    pub message: String,
}

pub struct QueueInstance {
    environment: Environment,
}

impl QueueInstance {
    /// Initializes the connexion to the message queue
    pub async fn new() -> Result<Self> {
        // TODO: add production host configuration through !cfg(debug_assertions)
        let environment = Environment::builder().build().await?;
        Ok(QueueInstance { environment })
    }

    /// Handles messages to be sent to the queue
    pub async fn run(&self, mut rx: Receiver<QueueMessage>) {
        loop {
            match rx.recv().await {
                Some(message) => match message.destination {
                    EventType::Point(_stream) => todo!("Handle point message"),
                    EventType::Topic(stream) => {
                        if let Err(e) = self.handle_topic_message(&stream, &message.message).await {
                            tracing::error!("error: {e}");
                        }
                    }
                },
                None => tracing::debug!("Message queue channel has been closed"),
            }
        }
    }

    pub async fn handle_topic_message(&self, stream: &str, message: &str) -> Result<()> {
        let create_response = self
            .environment
            .stream_creator()
            .max_length(rabbitmq_stream_client::types::ByteCapacity::GB(5))
            .create(&stream)
            .await;

        if let Err(e) = create_response {
            if let StreamCreateError::Create { stream, status } = e {
                match status {
                    rabbitmq_stream_client::types::ResponseCode::StreamAlreadyExists => {}
                    err => tracing::error!("couldn't create stream: {stream}, status: {err:?}"),
                }
            }
        }

        let producer = self.environment.producer().build(stream).await?;
        producer
            .send_with_confirm(Message::builder().body(message).build())
            .await?;
        Ok(())
    }
}

// TODO: You need to handle messages that are in the queue in another tokio task, where you for
// example delete an entity if a folder or user from another microservice (for example) got deleted
