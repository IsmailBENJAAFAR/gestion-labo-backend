from django.test import TestCase
from testcontainers.core.container import DockerContainer
from testcontainers.core.container import wait_for_logs
from django.core import management

from my_api.services.create import create_analyse
from my_api.services.get import get_all


class TestServices(TestCase):
    with DockerContainer("postgres:16").with_env("POSTGRES_USER", "ami").with_env(
        "POSTGRES_PASSWORD", "pwd"
    ).with_bind_ports(5432, 5432) as postgres:

        print("starting Container at port 5432")

        wait_for_logs(postgres, "ready to accept connections")
        management.call_command(
            "migrate",
            "my_api",
        )

        print("Testing data creation")

        create = create_analyse(
            data={
                "nom": "new idea",
                "description": "new idea of how to prepare a cake",
                "idFkLaboratoire": 1,
            }
        )

        print("\n")
        print(get_all())
        assert True
        print("\n\n\n\n\n\n\n")
        print(postgres.get_logs())
