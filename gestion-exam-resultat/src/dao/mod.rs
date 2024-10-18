mod dao_interface;
mod exam_dao;
pub use dao_interface::Dao;
pub use exam_dao::ExamDao;

#[cfg(test)]
pub use dao_interface::MockDao;
