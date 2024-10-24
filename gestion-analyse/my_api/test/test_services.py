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

        # test creating data
        print("Testing database data creation\n")
        create = create_analyse(
            data={
                "nom": "new idea",
                "description": "new idea of how to prepare a cake",
                "idFkLaboratoire": 1,
            }
        )
        self.assertEqual(create.get("response_status"), status.HTTP_200_OK)

        # test fetching data : getAll
        print("\nTesting data fetching from database: -> All\n")
        self.assertEqual(len(get_all().get("response_data")), 1)

        # test fetching data : getById
        print("\nTesting data fetching from database: -> by Id\n")
        self.assertTrue(
            get_by_id(id=1).get("response_data"),
        )

        # test updating data
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

        # test partial update
        print("\nTesting data updating in the database\n")
        update = update_analyse(
            data={
                "nom": "No idea",
                "description": "I really hate the fact that I spent 5 hours on less than 100 lines of code ._.)",
            },
            id=1,
        )
        self.assertEqual(
            update.get("response_status"),
            status.HTTP_200_OK,
        )

        # test delete data
        print("\nTesting data deletion from the database\n")
        delete = delete_analyse(id=1)
        self.assertEqual(delete.get("response_status"), status.HTTP_200_OK)
