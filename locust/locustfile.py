from locust import HttpUser, task

class GestionLaboUser(HttpUser):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        with open("url.txt") as url_file:
            self.baseUrl = url_file.read().strip()

    def url(self, url: str):
        return f"{self.baseUrl}/{url}"

    @task
    def connect(self):
        self.client.get(self.url("/dashboard"))
