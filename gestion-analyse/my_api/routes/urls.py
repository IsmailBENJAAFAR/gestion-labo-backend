from django.urls import path
from ..controllers import analyse_controller

urlpatterns = [
    path(
        "",
        view=analyse_controller.AnalyseGeneral.as_view(),
        name="Analyse-list-all-create",
    ),
    path(
        "<int:id>/",
        view=analyse_controller.AnalyseById.as_view(),
        name="Analyse-create-update-delete",
    ),
]
