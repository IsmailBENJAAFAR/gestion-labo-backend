from django.test import TestCase
from testcontainers.core.container import DockerContainer
from testcontainers.core.container import wait_for_logs
from django.core import management
from rest_framework import status

from my_api.services.delete import delete_analyse
from my_api.services.create import create_analyse
from my_api.services.get import *
from my_api.services.update import update_analyse


class TestServices(TestCase):
    postgres = (
        DockerContainer("postgres:16")
        .with_env("POSTGRES_USER", "ami")
        .with_env("POSTGRES_PASSWORD", "pwd")
        .with_bind_ports(5432, 5432)
        .start()
    )

    wait_for_logs(postgres, "ready to accept connections")
    print("Starting Container at port 5432")
    management.call_command(
        "migrate",
        "my_api",
    )

    def test_test(self):

        print("Testing database data creation\n")
        create = create_analyse(
            data={
                "nom": "new idea",
                "description": "new idea of how to prepare a cake",
                "idFkLaboratoire": 1,
            }
        )
        self.assertEqual(create.get("response_status"), status.HTTP_200_OK)

        print("Testing database bad data creation\n")
        bad_create = create_analyse(
            data={
                "nom": "new idea",
                "description": "new idea of how to prepare a cake",
            }
        )
        self.assertEqual(bad_create.get("response_status"), status.HTTP_400_BAD_REQUEST)

        print("\nTesting data fetching from database: -> All\n")
        all = get_all()
        self.assertEqual(len(all.get("response_data")), 1)

        print("\nTesting data fetching from database: -> by Id\n")
        analyse_by_id = get_by_id(id=1)
        self.assertTrue(
            analyse_by_id.get("response_data"),
        )

        print("\nTesting data fetching from database with bad id: -> by Id\n")
        analyse_by_id_bad = get_by_id(id=100000)
        self.assertEqual(
            analyse_by_id_bad.get("response_status"), status.HTTP_404_NOT_FOUND
        )

        print("\nTesting data updating in the database\n")
        update = update_analyse(
            data={
                "nom": "new idea",
                "description": "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
                "idFkLaboratoire": 1,
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
        update_partial = update_analyse(
            data={
                "nom": "No idea",
                "description": "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
            },
            id=1,
        )
        self.assertEqual(
            update_partial.get("response_status"),
            status.HTTP_200_OK,
        )
        self.assertEqual(
            get_by_id(id=1).get("response_data")["nom"],
            "No idea",
        )

        print("\nTesting data updating in the database with bad id\n")
        update_bad = update_analyse(
            data={
                "nom": "new idea",
                "description": "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
                "idFkLaboratoire": 1,
            },
            id=100000000000,
        )
        self.assertEqual(
            update_bad.get("response_status"),
            status.HTTP_404_NOT_FOUND,
        )

        print("\nTesting data deletion from the database\n")
        delete = delete_analyse(id=1)
        self.assertEqual(delete.get("response_status"), status.HTTP_200_OK)

        print("\nTesting data deletion from the database with bad id\n")
        delete_bad = delete_analyse(id=100000000)
        self.assertEqual(delete_bad.get("response_status"), status.HTTP_404_NOT_FOUND)
