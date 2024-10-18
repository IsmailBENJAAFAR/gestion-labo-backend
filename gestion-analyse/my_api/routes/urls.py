from django.urls import path
from ..controllers import analyse_controller

urlpatterns = [
    path("",view=analyse_controller.get_all,name="Analyse-view-list"),
    path("create",view=analyse_controller.create,name="Analyse-create-list"),
    path("<int:id>/",view=analyse_controller.RUD_by_id,name="Analyse-view-rud"),
]
