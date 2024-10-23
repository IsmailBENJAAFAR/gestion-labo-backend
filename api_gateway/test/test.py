from testcontainers.core.container import DockerClient, DockerContainer, wait_for_logs
import requests

def generate_caddy_file():
    with open("../Caddyfile") as caddyfile:
        text = caddyfile.read().replace("labo-sai.engineer {", ':80 {\n\trespond "test caddy success"')
        with open("./Caddyfile", "w") as caddyfile_test:
            caddyfile_test.write(text)
            print("Caddyfile_test written.")
    with open("../Dockerfile") as dockerfile:
        text = dockerfile.read()
        with open("./Dockerfile", "w") as dockerfile_test:
            dockerfile_test.write(text)
            print("Dockerfile written")

def docker_build():
    client = DockerClient()
    _, logs = client.build(path=".", tag="caddy_test")
    with DockerContainer("caddy_test").with_exposed_ports(80) as container:
        wait_for_logs(container, "server running")
        host = f"http://{container.get_container_host_ip()}"
        port = container.get_exposed_port(80)

        response = requests.get(f"{host}:{port}")
        assert response.status_code == 200

        logs = container.get_logs()
        print(f"Stdout: {logs[0].decode('ascii')}")
        print(f"Stderr: {logs[1].decode('ascii')}")

def test():
    generate_caddy_file()
    docker_build()

if __name__ == '__main__':
    test()
