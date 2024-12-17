use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct CreateEpreuveDto {
    pub nom: String,
    #[serde(rename = "testAnalyseId")]
    pub fk_id_analyse: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UpdateEpreuveDto {
    pub nom: Option<String>,
    #[serde(rename = "testAnalyseId")]
    pub fk_id_analyse: Option<i32>,
}

impl CreateEpreuveDto {
    pub fn new(nom: &str, fk_id_analyse: i32) -> CreateEpreuveDto {
        CreateEpreuveDto {
            nom: nom.to_string(),
            fk_id_analyse,
        }
    }
}

impl UpdateEpreuveDto {
    pub fn new(nom: &str, fk_id_analyse: i32) -> UpdateEpreuveDto {
        UpdateEpreuveDto {
            nom: Some(nom.to_string()),
            fk_id_analyse: Some(fk_id_analyse),
        }
    }
}
