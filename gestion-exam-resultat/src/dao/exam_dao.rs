use crate::models::Exam;

use super::dao::Dao;

struct ExamDao {}

impl<Exam> Dao<Exam> for ExamDao {
    fn find(id: i32) -> anyhow::Result<Exam> {
        todo!()
    }

    fn insert(data: Exam) -> bool {
        false
    }

    fn remove(id: i32) -> bool {
        false
    }

    fn find_all() -> anyhow::Result<Vec<Exam>> {
        Ok(Vec::new())
    }
}
