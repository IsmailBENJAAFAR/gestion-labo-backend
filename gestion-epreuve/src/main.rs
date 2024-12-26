use anyhow::Result;
use gestion_epreuve::run_app;

#[tokio::main]
async fn main() -> Result<()> {
    run_app().await
}
