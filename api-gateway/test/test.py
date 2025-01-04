from testcontainers.core.container import DockerClient, DockerContainer, wait_for_logs
import requests

caddy_test_message = "test caddy success"

def generate_caddy_file():
    with open("../Caddyfile") as caddyfile:
        text = caddyfile.read().replace("labo-sai.engineer {", f':80 {{\n\trespond "{caddy_test_message}"')
        with open("./Caddyfile", "w") as caddyfile_test:
            caddyfile_test.write(text)
            print("Caddyfile_test written.")

def generate_dockerfile():
    with open("../Dockerfile") as dockerfile:
        text = dockerfile.read().replace("COPY ./dist/* /var/www/html/\n", "")
        with open("./Dockerfile", "w") as dockerfile_test:
            dockerfile_test.write(text)
            print("Dockerfile written")

def build_image():
    client = DockerClient()
    client.build(path=".", tag="caddy_test")

def test_caddy():
    print("Running caddy_test container")
    with DockerContainer("caddy_test").with_exposed_ports(80) as container:
        wait_for_logs(container, "server running")
        host = container.get_container_host_ip()
        port = container.get_exposed_port(80)
        url = f"http://{host}:{port}"

        print(f"Making request to caddy_test container at {url}")
        response = requests.get(url)
        assert response.status_code == 200
        assert response.content.decode('utf-8') == caddy_test_message

        logs = container.get_logs()
        with open("logs.txt", "w") as logfile:
            logfile.write(logs[0].decode('utf-8'))
            logfile.write(logs[1].decode('utf-8'))
    print("Test Success!")

def test():
    generate_caddy_file()
    generate_dockerfile()
    build_image()
    test_caddy()

if __name__ == '__main__':
    test()
