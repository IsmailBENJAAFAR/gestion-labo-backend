import dj_database_url
from django.test import TestCase
from testcontainers.core.container import DockerContainer
from testcontainers.core.container import wait_for_logs
from django.core import management
from rest_framework import status

from my_api.services.delete import delete_analyse
from my_api.services.create import create_analyse
from my_api.services.get import *
from my_api.services.update import update_analyse
from gestion_analyse import settings


class TestServices(TestCase):
    databases = ["testxx"]
    # allow_database_queries = True
    postgres = (
        DockerContainer("postgres:16")
        .with_env("POSTGRES_USER", "ami")
        .with_env("POSTGRES_PASSWORD", "pwd")
        .with_exposed_ports(5432)
        .start()
    )
    # newDatabase["idkanymore"] = postgres.get_exposed_port(5432)
    settings.DATABASES["testxx"]["PORT"] = postgres.get_exposed_port(5432)
    print(settings.DATABASES["testxx"])
    wait_for_logs(postgres, "ready to accept connections")
    print("Starting Container at port 5432")

    management.call_command("makemigrations", "my_api")
    management.call_command("migrate", "my_api", "--database=testxx")

    def test_test(self):
        self.allow_database_queries = True

        print("Testing database data creation\n")
        create = create_analyse(
            data={
                "nom": "new idea",
                "description": "new idea of how to prepare a cake",
                "id_fk_laboratoire": 1,
            }
        )
        self.assertEqual(create.get("response_status"), status.HTTP_201_CREATED)

        print("Testing database bad data creation\n")
        create = create_analyse(
            data={
                "nom": "new idea",
                "description": "new idea of how to prepare a cake",
            }
        )
        self.assertEqual(create.get("response_status"), status.HTTP_400_BAD_REQUEST)

        print("\nTesting data fetching from database: -> All\n")
        all = get_all()
        self.assertEqual(len(all.get("response_data")), 1)

        print("\nTesting data fetching from database: -> by Id\n")
        analyse_by_id = get_by_id(id=1)
        self.assertTrue(
            analyse_by_id.get("response_data"),
        )

        print("\nTesting data fetching from database with bad id: -> by Id\n")
        analyse_by_id = get_by_id(id=100000)
        self.assertEqual(
            analyse_by_id.get("response_status"), status.HTTP_404_NOT_FOUND
        )

        print("\nTesting data updating in the database\n")
        update = update_analyse(
            data={
                "nom": "new idea",
                "description": "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
                "id_fk_laboratoire": 1,
            },
            id=1,
        )
        self.assertEqual(
            update.get("response_status"),
            status.HTTP_200_OK,
        )
        self.assertEqual(
            get_by_id(id=1).get("response_data")["description"],
            "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
        )

        print("\nTesting data partial updating in the database\n")
        update = update_analyse(
            data={
                "nom": "No idea",
                "description": "blank",
            },
            id=1,
        )
        self.assertEqual(
            update.get("response_status"),
            status.HTTP_200_OK,
        )
        self.assertEqual(
            (
                get_by_id(id=1).get("response_data")["nom"],
                get_by_id(id=1).get("response_data")["description"],
            ),
            ("No idea", "blank"),
        )

        print("\nTesting data updating in the database with bad id\n")
        update = update_analyse(
            data={
                "nom": "new idea",
                "description": "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
                "id_fk_laboratoire": 1,
            },
            id=100000000000,
        )
        self.assertEqual(
            update.get("response_status"),
            status.HTTP_404_NOT_FOUND,
        )

        print("\nTesting data deletion from the database\n")
        deletex = delete_analyse(id=1)
        self.assertEqual(deletex.get("response_status"), status.HTTP_204_NO_CONTENT)

        print("\nTesting data deletion from the database with bad id\n")
        delete = delete_analyse(id=100000000)
        self.assertEqual(delete.get("response_status"), status.HTTP_404_NOT_FOUND)
