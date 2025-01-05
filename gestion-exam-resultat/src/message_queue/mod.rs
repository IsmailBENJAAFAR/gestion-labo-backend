use anyhow::Result;
use lapin::{
    options::{BasicPublishOptions, ExchangeDeclareOptions},
    types::FieldTable,
    BasicProperties, Connection, ConnectionProperties,
};
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
    connection: Connection,
}

impl QueueInstance {
    /// Initializes the connexion to the message queue
    pub async fn new() -> Result<Self> {
        // TODO: add production host configuration through !cfg(debug_assertions)
        let addr = if cfg!(debug_assertions) {
            "amqp://127.0.0.1:5672/%2f"
        } else {
            "amqp://rabbitmq-service:5672/%2f"
        };
        let connection = Connection::connect(addr, ConnectionProperties::default()).await?;
        tracing::info!("connected to the queue through addr: {addr}");
        Ok(QueueInstance { connection })
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
        let channel = self.connection.create_channel().await?;
        channel
            .exchange_declare(
                "examen deletion",
                lapin::ExchangeKind::Topic,
                ExchangeDeclareOptions::default(),
                FieldTable::default(),
            )
            .await?;

        let confirm = channel
            .basic_publish(
                "examen deletion",
                stream,
                BasicPublishOptions::default(),
                message.as_bytes(),
                BasicProperties::default(),
            )
            .await?
            .await?;
        tracing::debug!("handle_topic_message: confirm: {confirm:?}");
        Ok(())
    }
}
