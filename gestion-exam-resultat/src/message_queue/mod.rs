use tokio::sync::mpsc::Receiver;

pub enum QueueType {
    // TODO: the point enum can contain more data related to the queue
    Point,
    Topic,
}

pub struct QueueMessage {
    pub msg_type: QueueType,
    pub message: String,
}

pub fn init_queue() {}

pub async fn run_queue(_rx: Receiver<QueueMessage>) {}
