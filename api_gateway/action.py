import os, shutil
from argparse import ArgumentParser
from pathlib import Path
from git import Repo
from docker import DockerClient

def clone_frontend_repository(user: str, token: str):
    frontend_url = f"https://{user}:{token}@github.com/soufianeamini/gestion-labo-frontend.git"
    Repo.clone_from(frontend_url, "frontend")

    assert Path("frontend").exists() and Path("frontend").is_dir()

def change_dir(path: str):
    os.chdir(path)
    print("Changing to directory: ", os.getcwd())

def build_dist():
    change_dir("frontend")
    assert os.system("npm i") == 0
    assert os.system("npm run build") == 0
    shutil.copytree("./dist/gestion-labo-frontend/browser", "../dist")
    change_dir("..")
    
def build_docker_image(registry: str, version: str):
    image_name = "gateway"
    tag = f"{registry}/{image_name}:{version}"
    client = DockerClient()
    print(f"Building image with tag: {tag}")
    client.images.build(path="./", tag=tag)

def push_image_to_registry(tag:str, registry: str, registry_user: str, registry_token: str):
    image_name = "gateway"
    client = DockerClient()
    client.login(username=registry_user, password=registry_token, registry=registry)
    res = client.images.push(f"{registry}/{image_name}", tag=tag)
    print(f"res: {res}")

def get_args() -> tuple[str, str, str, str, str]:
    parser = ArgumentParser()
    parser.add_argument("--token")
    parser.add_argument("--user")
    parser.add_argument("--registry")
    parser.add_argument("--registry-user")
    parser.add_argument("--registry-token")
    args = parser.parse_args()

    assert args.user is not None, "Please specify --user for access to the frontend repo"
    assert args.token is not None, "Please specify --token for access to the frontend repo"
    assert args.registry is not None, "Please specify --registry"
    assert args.registry_user is not None, "Please specify --registry-user"
    assert args.registry_token is not None, "Please specify --registry-token"

    return args.user, args.token, args.registry, args.registry_user, args.registry_token

if __name__ == "__main__":
    user, token, registry, registry_user, registry_token = get_args()
    with open("VERSION") as version:
        version = version.read().strip()
        print(f"Building gateway version: {version}")
    clone_frontend_repository(user, token)
    build_dist()
    build_docker_image(registry, version)
    push_image_to_registry(version, registry, registry_user, registry_token)
