use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct CreateExamDto {
    #[serde(rename = "dossierId")]
    pub fk_num_dossier: i32,
    #[serde(rename = "epreuveId")]
    pub fk_id_epreuve: i32,
    #[serde(rename = "testAnalyseId")]
    pub fk_id_test_analyse: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UpdateExamDto {
    #[serde(rename = "dossierId")]
    pub fk_num_dossier: Option<i32>,
    #[serde(rename = "epreuveId")]
    pub fk_id_epreuve: Option<i32>,
    #[serde(rename = "testAnalyseId")]
    pub fk_id_test_analyse: Option<i32>,
}

impl CreateExamDto {
    pub fn new(fk_num_dossier: i32, fk_id_epreuve: i32, fk_id_test_analyse: i32) -> CreateExamDto {
        CreateExamDto {
            fk_num_dossier,
            fk_id_epreuve,
            fk_id_test_analyse,
        }
    }
}
