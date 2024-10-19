from rest_framework import status
from my_api.models.Analyse import Analyse
from my_api.serializers.serializer import AnalyseSerializer


def get_all(method: str):
    """_summary_
        Gets all the analyses from the DB
    Args:
        method (str): HTTP method used, usually passed from query.method when calling this function

    Returns:
        _type_: (dict[str, Any] | None)
    """
    if method == "GET":
        analyses = Analyse.objects.all()
        analyse_serializer = AnalyseSerializer(analyses, many=True)
        return {
            "response_data": analyse_serializer.data,
            "response_status": status.HTTP_200_OK,
        }


def get_by_id(method, id):
    """- _summary_
        Get analyse by id
    Args:
        method (str): HTTP method used, usually passed from query.method when calling this function
        id (int): the analyse id

    Returns:
        _type_: (dict[str, Any] | None)
    """
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
            "response_status": status.HTTP_200_OK,
        }
