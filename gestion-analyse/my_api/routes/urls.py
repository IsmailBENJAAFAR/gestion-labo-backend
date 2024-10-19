from django.urls import path
from ..controllers import analyse_controller

urlpatterns = [
    path(
        "", view=analyse_controller.get_all_and_create, name="Analyse-list-all-create"
    ),
    path(
        "<int:id>/",
        view=analyse_controller.get_update_delete_by_id,
        name="Analyse-create-update-delete",
    ),
]
