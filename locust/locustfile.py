from locust import HttpUser, task

class GestionLaboUser(HttpUser):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    @task
    def connect(self):
        self.client.get("/dashboard")

    @task
    def examens(self):
        self.client.get("/dashboard/examen")
