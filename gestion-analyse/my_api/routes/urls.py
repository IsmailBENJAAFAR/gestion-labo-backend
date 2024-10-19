from django.urls import path
from ..controllers import analyse_controller

urlpatterns = [
    path("", view=analyse_controller.get_all, name="Analyse-list-all"),
    path("", view=analyse_controller.create, name="Analyse-create"),
    path("<int:id>/", view=analyse_controller.delete, name="Analyse-delete"),
    path("<int:id>/", view=analyse_controller.get_by_id, name="Analyse-list-by-id"),
    path("<int:id>/", view=analyse_controller.update, name="Analyse-list-by-id"),
]
