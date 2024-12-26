use anyhow::Result;
use axum::async_trait;

#[cfg_attr(test, mockall::automock)]
#[async_trait]
pub trait Dao<T: Sync + Send> {
    async fn insert(&self, data: &T) -> Result<i32>;
    async fn update(&self, data: &T) -> Result<T>;
    async fn remove(&self, id: i32) -> Result<bool>;
    async fn find(&self, id: i32) -> Result<T>;
    async fn find_all(&self) -> Result<Vec<T>>;
}
