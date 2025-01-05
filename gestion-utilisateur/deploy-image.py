from argparse import ArgumentParser
from docker import DockerClient

image_name = "gestion-utilisateur"

def build_docker_image(registry: str, version: str):
    tag = f"{registry}/{image_name}:{version}"
    client = DockerClient()
    print(f"Building image with tag: {tag}")
    client.images.build(path="./", tag=tag)

def push_image_to_registry(tag: str, registry: str, registry_user: str, registry_token: str):
    client = DockerClient()
    client.login(username=registry_user, password=registry_token, registry=registry)
    res = client.images.push(f"{registry}/{image_name}", tag=tag)
    print(f"res: {res}")

def get_args() -> tuple[str, str, str]:
    parser = ArgumentParser()
    parser.add_argument("--registry")
    parser.add_argument("--registry-user")
    parser.add_argument("--registry-token")
    args = parser.parse_args()

    assert args.registry is not None and len(args.registry), "Please specify --registry"
    assert args.registry_user is not None and len(args.registry_user), "Please specify --registry-user"
    assert args.registry_token is not None and len(args.registry_token), "Please specify --registry-token"

    return args.registry, args.registry_user, args.registry_token

if __name__ == "__main__":
    registry, registry_user, registry_token = get_args()
    # TODO: Create a version file that can be updated here
    version = "0.1.0"
    print(f"Building gestion-utilisateur version: {version}")
    build_docker_image(registry, version)
    push_image_to_registry(version, registry, registry_user, registry_token)
