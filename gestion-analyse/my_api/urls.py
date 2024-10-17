from django.urls import path
from .views import analyse_views,labo_views

urlpatterns = [
    path("Analyse/",view=analyse_views.get_all,name="Analyse-view-list"),
    path("Analyse/create",view=analyse_views.create,name="Analyse-create-list"),
    path("Analyse/<int:id>/",view=analyse_views.RUD_by_id,name="Analyse-view-rud"),
    # path("Analyse/name=<str:name>/",view=views.AnalyseSearchByName.as_view(),name="Analyse-name-r"),
    path("Laboratoire/",view=labo_views.get_all,name="Laboratoire-view-list"),
    path("Laboratoire/create",view=labo_views.create,name="Laboratoire-create-list"),
    path("Laboratoire/<int:id>/",view=labo_views.RUD_by_id,name="Laboratoire-view-rud"),
]
