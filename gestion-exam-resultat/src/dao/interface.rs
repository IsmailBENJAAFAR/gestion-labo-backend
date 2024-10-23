use anyhow::Result;
use axum::async_trait;

#[cfg_attr(test, mockall::automock)]
#[async_trait]
pub trait Dao<T: std::marker::Sync + std::marker::Send> {
    async fn insert(&self, data: T) -> Result<bool>;
    async fn remove(&self, id: i32) -> Result<bool>;
    async fn find(&self, id: i32) -> Result<T>;
    async fn find_all(&self) -> Result<Vec<T>>;
}
