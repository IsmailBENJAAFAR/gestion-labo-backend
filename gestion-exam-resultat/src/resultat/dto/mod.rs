use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct CreateResultatDto {
    #[serde(rename = "examId")]
    pub fk_id_exam: i32,
    pub observation: String,
    pub score: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UpdateResultatDto {
    #[serde(rename = "examId")]
    pub fk_id_exam: Option<i32>,
    pub observation: Option<String>,
    pub score: Option<i32>,
}

impl CreateResultatDto {
    pub fn new(fk_id_exam: i32, observation: &str, score: i32) -> CreateResultatDto {
        CreateResultatDto {
            fk_id_exam,
            observation: observation.to_string(),
            score,
        }
    }
}

impl UpdateResultatDto {
    pub fn new(fk_id_exam: i32, observation: &str, score: i32) -> UpdateResultatDto {
        UpdateResultatDto {
            fk_id_exam: Some(fk_id_exam),
            observation: Some(observation.to_string()),
            score: Some(score),
        }
    }
}
