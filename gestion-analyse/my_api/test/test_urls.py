from django.test import SimpleTestCase
from django.urls import resolve, reverse

from my_api.controllers.analyse_controller import AnalyseById, AnalyseGeneral


class TestSomethhing(SimpleTestCase):
    def test_of_the_test_i_wanna_kill_my_self(self):
        print("testing getAll and post Url\n")
        url = reverse("Analyse-list-all-create")
        self.assertEquals(resolve(url).func.view_class, AnalyseGeneral)

    def test_of_the_test_i_wanna_kill_my_self2(self):
        print("testing getById, update and delete Url\n")
        url = reverse("Analyse-create-update-delete", args=[1])
        self.assertEquals(resolve(url).func.view_class, AnalyseById)
