use tokio::sync::mpsc::Receiver;

// TODO: remove this derive after implementing queue
#[allow(dead_code)]
pub enum QueueType {
    // TODO: the point enum can contain more data related to the queue
    Point,
    Topic,
}

// TODO: remove this derive after implementing queue
#[allow(dead_code)]
pub struct QueueMessage {
    msg_type: QueueType,
    message: String,
}

pub fn init_queue() {}

pub async fn run_queue(_rx: Receiver<QueueMessage>) {}
