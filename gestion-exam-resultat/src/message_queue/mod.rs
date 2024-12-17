use tokio::sync::mpsc::Receiver;

pub enum EventType {
    // TODO: the point enum can contain more data related to the queue
    Point,
    Topic,
}

pub struct QueueMessage {
    pub msg_type: EventType,
    pub message: String,
}

pub struct QueueInstance {}

impl QueueInstance {
    /// Initialize the connexion to the message queue
    pub fn new() -> Self {
        QueueInstance {}
    }

    /// Handles messages to be sent to the queue
    pub async fn run(&self, _rx: Receiver<QueueMessage>) {}
}

// TODO: You need to handle messages that are in the queue in another tokio task, where you for
// example delete an entity if a folder or user from another microservice (for example) got deleted
