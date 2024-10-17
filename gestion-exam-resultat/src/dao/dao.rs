use anyhow::Result;

pub trait Dao<T> {
    fn insert(&self, data: T) -> bool;
    fn remove(&self, id: i32) -> bool;
    fn find(&self, id: i32) -> Result<T>;
    fn find_all(&self) -> Result<Vec<T>>;
}
