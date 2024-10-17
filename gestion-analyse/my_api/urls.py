from django.urls import path
from .views import analyse_views

urlpatterns = [
    path("",view=analyse_views.get_all,name="Analyse-view-list"),
    path("create",view=analyse_views.create,name="Analyse-create-list"),
    path("<int:id>/",view=analyse_views.RUD_by_id,name="Analyse-view-rud"),
    # path("Laboratoire/",view=labo_views.get_all,name="Laboratoire-view-list"),
    # path("Laboratoire/create",view=labo_views.create,name="Laboratoire-create-list"),
    # path("Laboratoire/<int:id>/",view=labo_views.RUD_by_id,name="Laboratoire-view-rud"),
]
