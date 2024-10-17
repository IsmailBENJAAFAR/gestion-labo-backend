use anyhow::Result;

pub trait Dao<T> {
    fn insert(data: T) -> bool;
    fn remove(id: i32) -> bool;
    fn find(id: i32) -> Result<T>;
    fn find_all() -> Result<Vec<T>>;
}
