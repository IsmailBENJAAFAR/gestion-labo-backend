use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ExamDto {
    pub nom: String,
    pub fk_id_analyse: i32,
}
