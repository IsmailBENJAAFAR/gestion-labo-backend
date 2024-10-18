from rest_framework import status
from my_api.models.Analyse import Analyse
from my_api.serializers.serializer import AnalyseSerializer


def get_all(method):
    if method == "GET":
        analyses = Analyse.objects.all()
        analyse_serializer = AnalyseSerializer(analyses, many=True)
        return {
            "response_data": analyse_serializer.data,
            "response_status": status.HTTP_200_OK,
        }
    else:
        return {
            "response_data": {},
            "response_status": status.HTTP_400_BAD_REQUEST,
        }


def get_by_id(method, id):
    try:
        analyse = Analyse.objects.get(id=id)
    except:
        return {
            "response_data": {},
            "response_status": status.HTTP_404_NOT_FOUND,
        }

    if method == "GET":
        analyse_serializer = AnalyseSerializer(analyse)
        return {
            "response_data": analyse_serializer.data,
            "response_status": status.HTTP_400_BAD_REQUEST,
        }
