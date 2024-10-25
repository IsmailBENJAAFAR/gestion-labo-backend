from my_api.models.Analyse import Analyse
from my_api.serializers.serializers import (
    AnalyseGenericSerializer,
    UpdateAnalyseSerializer,
)
from rest_framework import status


def update_analyse(data: dict[str:any], id: str) -> dict[str, any]:
    """
    Summary:
    Updates an Analyse, takes the old data and an id as input :
    """
    try:
        analyse = Analyse.objects.get(id=id)
    except:
        return {
            "response_data": {},
            "response_status": status.HTTP_404_NOT_FOUND,
        }
    analyse_serializer = UpdateAnalyseSerializer(analyse, data=data)
    if analyse_serializer.is_valid():
        analyse_serializer.save()
        analyse_serializer_get = AnalyseGenericSerializer(Analyse.objects.get(id=id))
        return {
            "response_data": analyse_serializer_get.data,
            "response_status": status.HTTP_200_OK,
        }
    else:
        return {"response_data": {}, "response_status": status.HTTP_400_BAD_REQUEST}
