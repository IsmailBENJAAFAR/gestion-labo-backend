use anyhow::Result;
use gestion_exam_resultat::run_app;

#[tokio::main]
async fn main() -> Result<()> {
    run_app().await
}
