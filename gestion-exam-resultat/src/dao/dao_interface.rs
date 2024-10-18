use anyhow::Result;

pub trait Dao<T> {
    async fn insert(&self, data: T) -> Result<bool>;
    async fn remove(&self, id: i32) -> Result<bool>;
    async fn find(&self, id: i32) -> Result<T>;
    async fn find_all(&self) -> Result<Vec<T>>;
}
