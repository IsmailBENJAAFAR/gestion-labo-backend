use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ExamDto {
    pub fk_num_dossier: i32,
    pub fk_id_epreuve: i32,
    pub fk_id_test_analyse: i32,
}

impl ExamDto {
    pub fn new(fk_num_dossier: i32, fk_id_epreuve: i32, fk_id_test_analyse: i32) -> ExamDto {
        ExamDto {
            fk_num_dossier,
            fk_id_epreuve,
            fk_id_test_analyse,
        }
    }
}
