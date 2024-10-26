import os, shutil
from argparse import ArgumentParser
from pathlib import Path
from git import Repo

def clone_frontend_repository(user: str, token: str):
    frontend_url = f"https://{user}:{token}@github.com/soufianeamini/gestion-labo-frontend.git"
    Repo.clone_from(frontend_url, "frontend")

    assert Path("frontend").exists() and Path("frontend").is_dir()

def change_dir(path: str):
    os.chdir(path)
    print("Changing to directory: ", os.getcwd())

def build_dist():
    change_dir("frontend")
    assert os.system("npm run build") == 0
    shutil.copy2("./dist", "..")
    change_dir("..")
    
def build_docker_image():
    pass

# if tests are successful, build a docker image based on the version located in the VERSION file
# The docker image needs to have a version, and the dockerfile needs to copy the dist dir from the frontend into the image
# Then the image should be pushed to the docker registry
def get_user_token() -> tuple[str, str]:
    parser = ArgumentParser()
    parser.add_argument("--token")
    parser.add_argument("--user")
    args = parser.parse_args()

    assert args.user is not None, "Please specify --user for access to the frontend repo"
    assert args.token is not None, "Please specify --token for access to the frontend repo"

    return args.user, args.token

if __name__ == "__main__":
    user, token = get_user_token()
    # run_caddy_test()
    with open("VERSION") as version:
        version = version.read().strip()
        print(f"Version: {version}")
    clone_frontend_repository(user, token)
    build_dist()
    build_docker_image()
    # push_to_registry()
